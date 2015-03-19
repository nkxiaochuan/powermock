## Bypass encapsulation ##

### Quick summary ###

  1. Use `Whitebox.setInternalState(..)` to set a private member of an instance or class.
  1. Use `Whitebox.getInternalState(..)` to get a private member of an instance or class.
  1. Use `Whitebox.invokeMethod(..)` to invoke a private method of an instance or class.
  1. Use `Whitebox.invokeConstructor(..)` to create an instance of a class with a private constructor.

### Example ###

### Access internal state ###
For mutable objects internal state may change after a method has been invoked. When unit testing such objects it's good to have an easy way to get a hold of this state and see if it has updated accordingly. PowerMock supplies several useful reflection utilities specially designed to be used with unit testing. All of these reflection utilities are located in the `org.powermock.reflect.Whitebox` class.

For demonstration purposes let's say we have a class that looks like this:
```
public class ServiceHolder {

	private final Set<Object> services = new HashSet<Object>();

	public void addService(Object service) {
		services.add(service);
	}

	public void removeService(Object service) {
		services.remove(service);
	}
}
```

Let's say that we'd like to test the `addService` method (may of course be regarded as "too simple to break" but we test it for demonstration purposes). Here we'd like to make sure that the state of the `ServiceHolder` has been correctly updated after the `addService` method has been invoked. I.e. a new object has been added to the `services` set. One way of doing this is to add a package-private or protected method called something like `getServices()` which returns the `services` set. But by doing this we've added a method to the `ServiceHolder` class that has no other purpose than making the class testable. This method may possibly also be miss-used elsewhere in the code. Another alternative would be to use the `Whitebox.getInternalState(..)` method in PowerMock to accomplish the same thing without altering the production code. In this simple case the whole test for the `addService` method would look like:

```
@Test
public void testAddService() throws Exception {
	ServiceHolder tested = new ServiceHolder();
	final Object service = new Object();

	tested.addService(service);

        // This is how you get the private services set using PowerMock
	Set<String> services = Whitebox.getInternalState(tested,
			"services");

	assertEquals("Size of the \"services\" Set should be 1", 1, services
			.size());
	assertSame("The services Set should didn't contain the expect service",
			service, services.iterator().next());
}
```

Using PowerMock 1.0 or above you can also get the internal state by specifying the type of the field to get. In the above case we could thus write
```
Set<String> services = Whitebox.getInternalState(tested, Set.class);
```
to get the `services` field. This is a more type-safe way to get internal state and it's also the preferred way for this reason. In situations where you would have several fields of type `Set` in a class you'd still have to revert to using the field name approach.

It's just as easy to set internal state of an object. Say for example that we have the following class:
```
public class ReportGenerator {

	@Injectable
	private ReportTemplateService reportTemplateService;

	public Report generateReport(String reportId) {
		String templateId = reportTemplateService.getTemplateId(reportId);
		/*
		 * Imagine some other code here that generates the report based on the
		 * template id.
		 */
		return new Report("name");
	}
}
```

Here we imagine that we're using a dependency injection framework that automatically supply us with an instance of the `ReportTemplateService` in run-time. We have numerous options for testing this class. We could for example refactor the class to use constructor or setter injection, use java reflection or we could simply create a setter for `reportTemplateService` that should be used _only_ for test purposes (i.e. setting a mock instance). Another approach would be to let PowerMock do the reflection for you by using the `Whitebox.setInternalState(..)` method. In this case it would be really easy:
```
Whitebox.setInternalState(tested, "reportTemplateService", reportTemplateServiceMock);
```

Using PowerMock 1.0 or above makes it even easier because you can even leave out the field name doing:
```
Whitebox.setInternalState(tested, reportTemplateServiceMock);
```
This is the preferred way since this is more refactor friendly. You may still have to revert to the field name approach if you have several fields in your class with type `ReportTemplateService`.

The set and get internal state methods that we've just seen also has some slightly more advanced use cases. Many times it's useful to modify or read internal state somewhere in the class hierarchy of an object. Normally setInternalState and getInternalState traverses the super-class hierarchy looking for the field in subclasses and returns or set the first field matching the supplied field name. But in some situations you really want to get or set a particular field somewhere in the hierarchy. For example suppose that we have an instance of class A that extends B and both A and B has a field called `myPrivateString` of type `java.lang.String` but in this case we'd like to read the contents of B's field. By using `Whitebox.getInternalState(..)` we can do this easily by specifying where in the class hierarchy the field should be read from. In this case we'd write something like:
```
String myPrivateString = Whitebox.<String> getInternalState(instanceOfA, "myPrivateString", B.class);
```
or if you're using PowerMock 1.0 or above:
```
String myPrivateString = Whitebox.getInternalState(instanceOfA, String.class, B.class);
```
This also eliminates the cast in most situations.

Using the first approach there's another way to avoid the cast by specifying the return type:
```
String myPrivateString = Whitebox.getInternalState(instanceOfA, "myPrivateString", B.class, String.class);
```

If we instead like to set the state of the `myPrivateString` we'd do like this:
```
Whitebox.setInternalState(instanceOfA, "myPrivateString", "this is my private string", B.class);
```
or if you're using PowerMock 1.0 or above:
```
Whitebox.setInternalState(instanceOfA, String.class, "this is my private string", B.class);
```

### Invoking a private method ###
To invoke a private method you can make use of the `Whitebox.invokeMethod(..)` method in PowerMock. For example let's say you have a private method called sum in an instance called `myInstance` which you'd like to test in separation:
```
private int sum(int a, int b) {
	return a+b;
}
```
To do this you simply do:
```
int sum = Whitebox.<Integer> invokeMethod(myInstance, "sum", 1, 2);
```
Printing the sum would then of course display 3.

This will work fine in most situations. However there are cases when PowerMock cannot figure out which method to invoke by using this simple syntax. For example if an overloaded method is using a primitive type and the other method is using a wrapper type such as:
```
...
private int myMethod(int id) {		
	return 2*id;
}

private int myMethod(Integer id) {		
		return 3*id;
}
...
```

This is of course a naive example but you could still use `Whitebox.invokeMethod(..)` to call both these methods. For the `myMethod(int id)` case you'd do like this:
```
int result = Whitebox.<Integer> invokeMethod(myInstance, new Class<?>[]{int.class}, "myMethod", 1);
```

Here we explicitly tell PowerMock to expect the call to the `myMethod` that takes an `int` as parameter. But in the majority of cases you don't need to specify the parameter type since PowerMock will find the correct method automatically.

You can also invoke class-level (static) methods. Imagine that the `sum` method was also static, in this case we could invoke it by:
```
int sum = Whitebox.<Integer> invokeMethod(MyClass.class, "sum", 1, 2);
```

if the method was defined in a class called `MyClass`.

Since PowerMock 1.2.5 you can also invoke methods without specifying a method name which can be good for refactoring purposes. E.g.
```
Whitebox.invokeMethod(myInstance, param1, param2);
```
Note that this only works if PowerMock can locate a unique method based on the parameter types.


### Instantiate a class with a private constructor ###

To instantiate a class with a private constructor such as:
```
public class PrivateConstructorInstantiationDemo {

	private final int state;

	private PrivateConstructorInstantiationDemo(int state) {
		this.state = state;
	}

	public int getState() {
		return state;
	}
}
```

You simply do:
```
PrivateConstructorInstantiationDemo instance =  WhiteBox.invokeConstructor(
				PrivateConstructorInstantiationDemo.class, 43);
```
where 43 is the state parameter.

This will work fine in most situations. However there are cases when PowerMock cannot figure out which constructor to invoke by using this simple syntax. For example if an overloaded constructor is using a primitive type and the other constructor is using a wrapper type such as:

```
public class PrivateConstructorInstantiationDemo {

	private final int state;

	private PrivateConstructorInstantiationDemo(int state) {
		this.state = state;
	}

	private PrivateConstructorInstantiationDemo(Integer state) {
              this.state = state;
              // do something else
	}

	public int getState() {
		return state;
	}
}
```

This is of course a naive example but you could still use `Whitebox.invokeConstructor(..)` to call both these constructors. For the `Integer` parameter constructor case you'd do like this:
```
PrivateConstructorInstantiationDemo instance = Whitebox.invokeConstructor(PrivateConstructorInstantiationDemo.class, new Class<?>[]{Integer.class}, 43);
```

Here we explicitly tell PowerMock to expect the call to the constructor that takes an `Integer` as parameter. But in the majority of cases you don't need to specify the parameter type since PowerMock will find the correct method automatically.

### Note ###
All of these things can be achieved without using PowerMock, this is just normal Java reflection. However reflection requires much boiler-plate code and can be error prone and thus PowerMock provides you with these utility methods instead. PowerMock gives you a choice on whether to refactor your code and add getter/setter methods for checking/altering internal state or whether to use its utility methods to accomplish the same thing without changing the production code. It's up to you!

### References ###
  * [ServiceHolderTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/bypassencapsulation/ServiceHolderTest.java)
  * [ServiceHolder](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/bypassencapsulation/ServiceHolder.java)
  * [ReportGeneratorTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/bypassencapsulation/ReportGeneratorTest.java)
  * [ReportGenerator](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/bypassencapsulation/ReportGenerator.java)
  * [ReportDaoTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/bypassencapsulation/ReportDaoTest.java)
  * [ReportDao](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/bypassencapsulation/ReportDao.java)
  * [WhiteboxTest](https://github.com/jayway/powermock/blob/master/reflect/src/test/java/org/powermock/reflect/WhiteBoxTest.java)