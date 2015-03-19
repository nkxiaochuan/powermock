## Suppressing Unwanted Behavior ##

### Quick summary ###
  1. Use the `@RunWith(PowerMockRunner.class)` annotation at the class-level of the test case.
  1. Use the `@PrepareForTest(ClassWithEvilParentConstructor.class)` annotation at the class-level of the test case in combination with `suppress(constructor(EvilParent.class))` to suppress all constructors for the EvilParent class.
  1. Use the `Whitebox.newInstance(ClassWithEvilConstructor.class)` method to instantiate a class without invoking the constructor what so ever.
  1. Use the `@SuppressStaticInitializationFor("org.mycompany.ClassWithEvilStaticInitializer")` annotation to remove the static initializer for the the `org.mycompany.ClassWithEvilStaticInitializer` class.
  1. Use the `@PrepareForTest(ClassWithEvilMethod.class)` annotation at the class-level of the test case in combination with `suppress(method(ClassWithEvilMethod.class, "methodName"))` to suppress the method with name "methodName" in the ClassWithEvilMethod class.
  1. Use the `@PrepareForTest(ClassWithEvilField.class)` annotation at the class-level of the test case in combination with `suppress(field(ClassWithEvilField.class, "fieldName"))` to suppress the field with name "fieldName" in the ClassWithEvilField class.

You'll find the member modification and member matcher methods here:
  * `org.powermock.api.support.membermodification.MemberModifier`
  * `org.powermock.api.support.membermodification.MemberMatcher`

### Example ###
Sometimes you want or even need to suppress the behavior of certain constructors, methods or static initializers in order to unit test your own code. A classic example is when your class need to extend from another class in third-party framework of some kind. The problem arise when this 3rd party class does something in its constructor that prevents you from unit testing your own code. For example the framework may try to load a dll or access the network or file system for some reason. Let's take a look at some examples of this.

#### Suppress super class constructors ####
As an example let's look at a class called `ExampleWithEvilParent`, it's really simple:
```
public class ExampleWithEvilParent extends EvilParent {

	private final String message;

	public ExampleWithEvilParent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
```
This seems like an easy class to unit test (so easy in fact that you should probably not test it, but let's do it anyway for demonstration purposes). But wait, let's look at what the `EvilParent` class looks like:
```
public class EvilParent {

	public EvilParent() {
		System.loadLibrary("evil.dll");
	}
}
```
This parent class tries to load a dll file which will not be present when you run a unit test for the `ExampleWithEvilParent` class. With PowerMock you can just suppress the constructor of the EvilParent so that you can unit test the ExampleWithEvilParent class. This is done by using the `suppress` method of the PowerMock API. In this case we would do:
```
suppress(constructor(EvilParent.class));
```
You must also prepare the ExampleWithEvilParent for testing (because it is from this class that the constructor of the EvilParent is called). You do this by passing the `ExampleWithEvilParent.class` to the `@PrepareForTest` annotation:

```
@PrepareForTest(ExampleWithEvilParent.class)
```

The full test looks like this:

```
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExampleWithEvilParent.class)
public class ExampleWithEvilParentTest {

	@Test
	public void testSuppressConstructorOfEvilParent() throws Exception {
		suppress(constructor(EvilParent.class));
		final String message = "myMessage";
		ExampleWithEvilParent tested = new ExampleWithEvilParent(message);
		assertEquals(message, tested.getMessage());
	}
}
```
If the super class have several constructors it's possible to tell PowerMock to only suppress a specific one. Let's say you have a class called `ClassWithSeveralConstructors` that has one constructor that takes a `String` and another constructor that takes an `int` as an argument and you only want to suppress the `String` constructor. You can do this using the
```
suppress(constructor(ClassWithSeveralConstructors.class, String.class));
```
method.

#### Suppress own constructor ####
The example above works when suppressing superclass constructors and class under test. Another way to achieve to suppress the constructor under test is to use the `Whitebox.newInstance` method. E.g. if your own code does something in its constructor that makes it difficult to unit test. This instantiates the class without invoking the constructor at all. For example let's say you want to unit-test the following class:
```
public class ExampleWithEvilConstructor {

	private final String message;

	public ExampleWithEvilConstructor(String message) {
		System.loadLibrary("evil.dll");
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
```
To instantiate this class we make use of the `Whitebox.newInstance` method:
```
ExampleWithEvilConstructor tested = Whitebox.newInstance(ExampleWithEvilConstructor.class);
```
The full test looks like this:
```
public class ExampleWithEvilConstructorTest {

	@Test
	public void testSuppressOwnConstructor() throws Exception {
		ExampleWithEvilConstructor tested = Whitebox.newInstance(ExampleWithEvilConstructor.class);
		assertNull(tested.getMessage());
	}
}
```
Note that you don't need to use the `@RunWith(..)` annotation or pass the class to the `@PrepareForTest` annotation. It doesn't hurt to do so, but it's not necessary. Another important thing to understand is that since the constructor is never executed the `tested.getMessage()` method returns `null`. You can of course change the state of the message field in the tested instance by [bypassing the encapsulation](BypassEncapsulation.md).

#### Suppress method ####
In some cases you simply want to suppress a method and make it return some default value, in other cases you may _need_ to suppress or mock a method because it does something that prevents you from unit-testing your own class. Look at the following made-up example:
```
public class ExampleWithEvilMethod {

	private final String message;

	public ExampleWithEvilMethod(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message + getEvilMessage();
	}

	private String getEvilMessage() {
		System.loadLibrary("evil.dll");
		return "evil!";
	}
}
```
If the `System.loadLibrary("evil.dll")` statement is executed when testing the `getMessage()` method the test will fail. One simple way to avoid this is to simply suppress the `getEvilMessage` method. You do this by using the `suppress(method(..))` method of the PowerMock API, in this case you would do:
```
suppress(method(ExampleWithEvilMethod.class, "getEvilMessage"));
```
The full test looks like this:
```
@RunWith(PowerMockRunner.class)
@PrepareForTest(ExampleWithEvilMethod.class)
public class ExampleWithEvilMethodTest {

	@Test
	public void testSuppressMethod() throws Exception {
		suppress(method(ExampleWithEvilMethod.class, "getEvilMessage"));
		final String message = "myMessage";
		ExampleWithEvilMethod tested = new ExampleWithEvilMethod(message);
		assertEquals(message, tested.getMessage());
	}
}
```

#### Suppress static initializer ####
Some times a thrid-party class does something in its static initializer (also called static constructor) that prevents you from unit testing your own class. It's also possible that your own class does something in a static initializer which you don't want to happen when you unit test your class. PowerMock can then simply suppress the static initialization of that class. You do this by specifying the `@SuppressStaticInitializationFor` annotation at the class-level or method-level of the test. For example let's say you want to unit-test the following class:
```
public class ExampleWithEvilStaticInitializer {

	static {
		System.loadLibrary("evil.dll");
	}

	private final String message;

	public ExampleWithEvilStaticInitializer(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
```
The problem here is that when the ExampleWithEvilStaticInitializer class is loaded the static code block will be executed and the `System.loadLibrary("evil.dll")` will be executed causing the unit test to fail (if the evil.dll cannot be loaded). To suppress this static initializer we do like this:
```
@SuppressStaticInitializationFor("org.mycompany.ExampleWithEvilStaticInitializer")
```
As you can see we don't pass the `ExampleWithEvilStaticInitializer.class` to the `@SuppressStaticInitializationFor` but instead we give it the fully-qualified name of the class instead. The reason is that if we were to pass `ExampleWithEvilStaticInitializer.class` to the annotation the static initializer would run before the test has even started and thus the test would fail. So when removing a static initializer you have to pass the fully-qualified name to the class instead. The whole test would like like:
```
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("org.mycompany.ExampleWithEvilStaticInitializer")
public class ExampleWithEvilStaticInitializerTest {

	@Test
	public void testSuppressStaticInitializer() throws Exception {
		final String message = "myMessage";
		ExampleWithEvilStaticInitializer tested = new ExampleWithEvilStaticInitializer(message);
		assertEquals(message, tested.getMessage());
	}
}
```

#### Suppress fields ####
You can also suppress fields using `suppress(..)`. For example let's say you have to following class:
```
public class MyClass {
	private MyObject myObject = new MyObject();


	public MyObject getMyObject() {
		return myObject;
	}
}
```

To suppress the `myObject` field above you can do:
```
suppress(field(MyClass.class, "myObject"));
```
Invoking the `getMyObject()` will then return `null` for every instance of `MyClass` when it has been prepared for test.

### References ###
  * [ExampleWithEvilParentTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/suppress/constructor/ExampleWithEvilParentTest.java)
  * [ExampleWithEvilConstructorTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/suppress/constructor/ExampleWithEvilConstructorTest.java)
  * [ExampleWithEvilMethodTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/suppress/method/ExampleWithEvilMethodTest.java)
  * [ExampleWithEvilStaticInitializerTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/suppress/staticinitializer/ExampleWithEvilStaticInitializerTest.java)
  * [ExampleWithEvilParent](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/suppress/constructor/ExampleWithEvilParent.java)
  * [EvilParent](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/suppress/constructor/EvilParent.java)
  * [ExampleWithEvilConstructor](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/suppress/constructor/ExampleWithEvilConstructor.java)
  * [ExampleWithEvilMethod](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/suppress/method/ExampleWithEvilMethod.java)
  * [ExampleWithEvilStaticInitializer](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/suppress/staticinitializer/ExampleWithEvilStaticInitializer.java)