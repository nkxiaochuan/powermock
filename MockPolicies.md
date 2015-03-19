## Mock Policies ##

### Quick summary ###

  1. Use the `@RunWith(PowerMockRunner.class)` annotation at the class-level of the test case.
  1. Use the `@MockPolicy(MyMockPolicy.class)` annotation at the class-level of the test case.

### Example ###

A Mock Policy can be used to make it easier to unit test some code with PowerMock in isolation from a certain framework. A mock policy implementation can for example suppress some methods, suppress static initializers or intercept method calls and change their return value (for example to return a mock object) for a certain framework or set of classes or interfaces. A mock policy can for example be implemented to avoid writing repetitive setup code for your tests. Say that you're using a framework X and that in order for you to test it requires that certain methods should always return a mock implementation. Perhaps some static initializers must be suppressed as well. Instead of copying this code between tests it would be a good idea to write a reusable mock policy.

PowerMock 1.1 provides three mock policies out of the box for mocking slf4j, java common-logging and log4j. Let's pick slf4j as an example and let's say you have a class that looks like this:
```
public class Slf4jUser {
	private static final Logger log = LoggerFactory.getLogger(Slf4jUser.class);

	public final String getMessage() {
		log.debug("getMessage!");
		return "log4j user";
	}
}
```

Here we have a problem because the logger gets instantiated in the static initializer of the Slf4jUser class. Sometimes this leads to problems depending on the log configuration so what you want to do in a unit-test is to stub out the log instance. This is fully doable without using mock policies. One way to do this is to start by suppressing the static initializer of the Slf4jUser class from our test. We can then create a stub or nice mock of the Logger class and inject it to the Slf4jUser instance. But this is not enough, imagine that we've configured slf4j to use log4j as logging back-end then we'll get the following error printed in the console while running the test:

> log4j:ERROR A "org.apache.log4j.RollingFileAppender" object is not assignable to a org.apache.log4j.Appender" variable.
> log4j:ERROR The class "org.apache.log4j.Appender" was loaded by
> log4j:ERROR [org.powermock.core.classloader.MockClassLoader@aa9835] whereas object of  type
> log4j:ERROR "org.apache.log4j.RollingFileAppender" was loaded by [sun.misc.Launcher$AppClassLoader@11b86e7].
> log4j:ERROR Could not instantiate appender named "R".

To avoid this error message we need to prepare the `org.apache.log4j.Appender` for testing. The full test setup would look like this:
```
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor("org.myapp.Slf4jUser")
@PrepareForTest( Appender.class)
public class MyTest {

  @Before
  public void setUp() {
      Logger loggerMock = createNiceMock(Logger.class);
      Whitebox.setInternalState(Slf4jUser.class, loggerMock);
      ...
  }
  ...
}
```
This setup behavior would have to be copied to all test classes dealing with slf4j. Instead you could use the Slf4j mock policy that take care of doing this setup for you. Your test would then look like:
```
@RunWith(PowerMockRunner.class)
@MockPolicy(Slf4jMockPolicy.class)
public class Slf4jUserTest {
     ...
}
```
Note that we don't have do any setup at all to mock slf4j, the `Slf4jMockPolicy` takes care of this.

Mock policies can also be chained or nested like this:
```
@RunWith(PowerMockRunner.class)
@MockPolicy({MockPolicyX.class, MockPolicyY.class})
public class MyTest {
    ...
}
```

Note that subsequent mock policies in the chain can override the behavior of the preceding policies. In this example this means that `MockPolicyY` could potentially override behavior defined by `MockPolicyX`. If writing custom mock policies it's important to keep this in mind.

### Creating custom mock policies ###
It's possible to create custom mock policies to deal with similar setup and mock behavior. To do this you need to create a class that implements the [org.powermock.core.spi.PowerMockPolicy](https://github.com/jayway/powermock/blob/master/core/src/main/java/org/powermock/core/spi/PowerMockPolicy.java) interface. It contains two methods:
```
void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings);
```
and
```
void applyInterceptionPolicy(MockPolicyInterceptionSettings settings);
```
A mock policy implementation can for example suppress some methods, suppress static initializers or intercept method calls and change their return value (for example to return a mock object). A mock policy implementation must be stateless. The reason why there are two methods for applying settings is that PowerMock needs to know which classes that should be modified by the mock class loader <i>before</i> these classes have loaded. The first method tells PowerMock which classes that should be loaded and then the second is called from the mock class-loader itself. This means you can create mocks for e.g. final and static methods in the `applyInterceptionPolicy` method which would not have been possible otherwise.

Since mock policies can be chained subsequent policies can override behavior of a previous policy. To avoid accidental overrides it's recommended _add_ behavior instead of _setting_ behavior since the latter overrides all previous configurations.

Let's clear things up with an example. Imagine that we have a class that looks like this:
```
public class Dependency {

	private final DataObject dataObject;

	public Dependency(String data) {
		dataObject = new DataObject(data);
	}

	public DataObject getData() {
		return dataObject;
	}
}
```
Let's say that we'd like to return a custom `DataObject` each time the `getData` method is called, i.e. we want to intercept the call to `getData` and make it return our custom object. To create a reusable mock policy that does exactly this we start by creating a new class called `MyCustomMockPolicy` that implements the `org.powermock.core.spi.PowerMockPolicy` interface. The full code for this can be seen below:
```
public class MyCustomMockPolicy implements PowerMockPolicy {

	/**
	 * Add the {@link Dependency} to the list of classes that should be loaded
	 * by the mock class-loader.
	 */
	public void applyClassLoadingPolicy(MockPolicyClassLoadingSettings settings) {
		settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(Dependency.class.getName());
	}

	/**
	 * Every time the {@link Dependency#getData()} method is invoked we return a
	 * custom instance of a {@link DataObject}.
	 */
	public void applyInterceptionPolicy(MockPolicyInterceptionSettings settings) {
		final Method getDataMethod = Whitebox.getMethod(Dependency.class);
		final DataObject dataObject = new DataObject("Policy generated data object");
		settings.addSubtituteReturnValue(getDataMethod, dataObject);
	}
}
```
Let's explain it in a bit more detail. Since we want to intercept the `getData()` method call of the `Dependency` class we must prepare it for testing by having the class loaded by the mock class-loader. Thus we tell PowerMock to do so by invoking the `settings.addFullyQualifiedNamesOfClassesToLoadByMockClassloader(..)` method from the `applyClassLoadingPolicy` method by passing in the fully-qualified name of the `Dependency` class. If we like we could pass in a package name here as well if we want all classes from a specific package (and sub-packages) to be loaded by the mock classloader. In the next step we tell PowerMock to do the actual interception. First we get the method that we want to intercept (in this case the getData method) by using `Whitebox.getMethod(..)`. Then we create our custom DataObject that we wish to return and then we instruct PowerMock to return this object by invoking the `settings.addSubtituteReturnValue(..)` method. That's it!

So let's say we have a class that actually uses our dependency that looks like this:
```
public class DependencyUser {

	public DataObject getDependencyData() {
		return new Dependency("some data").getData();
	}
}
```

If we apply our mock policy to a test of the `DependencyUser` instance the `DataObject` returned by the `Dependency` instance will be the one we created in the `MyCustomMockPolicy#applyInterceptionPolicy(..)` method. Here's a simple test to prove that this really happens:
```
@RunWith(PowerMockRunner.class)
@MockPolicy(MyCustomMockPolicy.class)
public class DependencyUserTest {

	@Test
	public void assertThatMyFirstMockPolicyWork() throws Exception {
		DataObject dependencyData = new DependencyUser().getDependencyData();
		assertEquals("Policy generated data object", dependencyData.getData());
	}
}
```

If the mock policy wouldn't have been applied the `dependencyData.getData()` would returned "some data".

### References ###
  * [MyCustomMockPolicy](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/mockpolicy/policy/MyCustomMockPolicy.java)
  * [DependencyUserTest](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/test/java/powermock/examples/mockpolicy/DependencyUserTest.java)
  * [DependencyUser](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/mockpolicy/DependencyUser.java)
  * [Dependency](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/mockpolicy/nontest/Dependency.java)
  * [DataObject](https://github.com/jayway/powermock/blob/master/examples/DocumentationExamples/src/main/java/powermock/examples/mockpolicy/nontest/domain/DataObject.java)