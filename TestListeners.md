## Test Listeners ##

### Quick summary ###

  1. Use the `@RunWith(PowerMockRunner.class)` annotation at the class-level of the test case.
  1. Use the `@PowerMockListener({Listener1.class, Listener2.class})` annotation at the class-level of the test case.

### Example ###

PowerMock 1.1 and above has a notion of Test Listeners. Test listeners can be used to get events from the test framework such as when a test method starts and ends and the result of a test execution. The purpose of these test listeners is to provide a test-framework independent way to get and react to these notifications by implementing the `org.powermock.core.spi.PowerMockTestListener` and pass it to the `PowerMockListener` annotation. PowerMock has some built-in test listeners for you to use.

#### AnnotationEnabler ####
**Note:** Since PowerMock 1.3 you no longer have to specify this listener manually. It's now integrated into the runner and mocks are injected automatically.


This test listener implementation let's you use annotations to create mock objects. For example consider the following piece of code:
```
@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
 public class PersonServiceTest {

 	@Mock
  	private PersonDao personDaoMock;

  	private PersonService classUnderTest;

  	@Before
  	public void setUp() {
  		classUnderTest = new PersonService(personDaoMock);
  	}
   ...
  }
```

Using the @Mock annotation eliminates the need to setup and tear-down mocks manually which minimizes repetitive test code and makes the test more readable. The AnnotationEnabler works for both the EasyMock and Mockito API's. In the EasyMock version you can also supply the names of the methods that you wish to mock if you want to create a partial mock, for example:

```
@RunWith(PowerMockRunner.class)
@PowerMockListener(AnnotationEnabler.class)
 public class PersonServiceTest {

 	@Mock("getPerson")
  	private PersonDao personDaoMock;

  	private PersonService classUnderTest;

  	@Before
  	public void setUp() {
  		classUnderTest = new PersonService(personDaoMock);
  	}
   ...
  }
```
This piece of code will instruct PowerMock to create a partial mock of the `PersonDao` where only the "getPerson" method is mocked. Since EasyMock has support for nice and strict mocks you can use the `@MockNice` and `@MockStrict` annotations to get the benefits of this.

In Mockito you simply use `spy(..)` to partially mock a class or instance.

#### FieldDefaulter ####
This test listener implementation can be used to set the default value for each member-field in a junit test after each test. For many developers it's more or less a standard procedure to create a tearDown method and null out all references when using JUnit (you can read more about this issue [here](http://blogs.atlassian.com/developer/2005/12/reducing_junit_memory_usage.html)). But instead it would be possible to use the FieldDefaulter to accomplish this automatically. As an example let's say you have 5 collaborators that you want to mock in your test and you want to make sure each of them are set to null after each test to allow them to be garbage collected. So instead of doing:
```
@RunWith(PowerMockRunner.class)
public class MyTest {
	
  	private Collaborator1 collaborator1Mock;
  	private Collaborator2 collaborator2Mock;
  	private Collaborator3 collaborator3Mock;
  	private Collaborator4 collaborator4Mock;
  	private Collaborator5 collaborator5Mock;

        ...
  	@After
  	public void tearDown() {
            collaborator1Mock = null;
            collaborator2Mock = null;
            collaborator3Mock = null;
            collaborator4Mock = null;
            collaborator5Mock = null;
  	}
        ...

}
```
you can use the `FieldDefaulter` test listener to get rid of the tear-down method completely:
```
@RunWith(PowerMockRunner.class)
@PowerMockListener(FieldDefaulter.class)
public class MyTest {
	
  	private Collaborator1 collaborator1Mock;
  	private Collaborator2 collaborator2Mock;
  	private Collaborator3 collaborator3Mock;
  	private Collaborator4 collaborator4Mock;
  	private Collaborator5 collaborator5Mock;

        ...
}
```


### References ###
  * [AnnotationDemoWithSetupMethodTest](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/annotationbased/AnnotationDemoWithSetupMethodTest.java)
  * [FinalDemoWithAnnotationInjectionAndFieldDefaulterTest](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/annotationbased/FinalDemoWithAnnotationInjectionAndFieldDefaulterTest.java)