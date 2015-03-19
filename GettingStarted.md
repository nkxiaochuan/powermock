## API's ##
PowerMock consists of two extension API's. One for EasyMock and one for Mockito. To use PowerMock you need to depend on one of these API's as well as a test framework. Currently PowerMock supports JUnit and TestNG. There are three different JUnit test executors available, one for JUnit 4.4+, one for JUnit 4.0-4.3 and one for JUnit 3. There's one test executor for TestNG which requires version 5.11+ depending on which version of PowerMock you use.

## Writing tests ##

Write a test like this:

```
@RunWith(PowerMockRunner.class)
@PrepareForTest( { YourClassWithEgStaticMethod.class })
public class YourTestCase {
...
}
```

Sample code is available in git:
  * [Mocking static methods](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/singleton/MockStaticTest.java)
  * [Expect new instance](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/expectnew/ExpectNewDemoTest.java)
  * [Suppress constructor](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/suppressconstructor/SuppressConstructorDemoTest.java)

JUnit 3 requires a different setup by creating a suite, since test runners are not available. Please view some examples:
  * [Mocking private methods with JUnit 3](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit3-test/src/test/java/samples/junit3/privateandfinal/PrivateFinalTest.java)


## Maven setup ##
### JUnit ###
  * [EasyMock JUnit Maven setup](EasyMock_maven.md)
  * [Mockito JUnit Maven setup](Mockito_maven.md)
### TestNG ###
  * [EasyMock TestNG Maven setup](EasyMock_testng_maven.md)
  * [Mockito TestNG Maven setup](Mockito_testng_maven.md)
## Non maven users ##

**EasyMock**

JUnit:
Download the [EasyMock](http://dl.bintray.com/johanhaleby/generic/powermock-easymock-junit-1.6.2.zip) zip-file with PowerMock and all its dependencies and add those to your project.

TestNG:
Download the [EasyMock](http://dl.bintray.com/johanhaleby/generic/powermock-easymock-testng-1.6.2.zip) zip-file with PowerMock and all its dependencies and add those to your project.

**Mockito**

JUnit:
Download the [Mockito](http://dl.bintray.com/johanhaleby/generic/powermock-mockito-junit-1.6.2.zip) zip-file with PowerMock and all its dependencies and add those to your project.

TestNG:
Download the [Mockito](http://dl.bintray.com/johanhaleby/generic/powermock-mockito-testng-1.6.2.zip) zip-file with PowerMock and all its dependencies and add those to your project.

**Or**

You could also download the [EasyMock jar-file](http://dl.bintray.com/johanhaleby/generic/powermock-easymock-1.6.2-full.jar) or [Mockito jar-file](http://dl.bintray.com/johanhaleby/generic/powermock-mockito-1.6.2-full.jar) in a single file and then download the dependencies separately.

## Need to bootstrap using a JUnit rule? ##
  * The [PowerMockRule](PowerMockRule.md) makes this happen

## Java agent based bootstrapping ##
Use the [PowerMock Java Agent](PowerMockAgent.md) if you're having classloading problems when using PowerMock.