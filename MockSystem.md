## Mocking system classes ##
Note: The same technique applies to the Mocktio API extension as well although all examples shown here are made with the EasyMock API extension.

### Quick summary ###

  1. Use the `@RunWith(PowerMockRunner.class)` annotation at the class-level of the test case.
  1. Use the `@PrepareForTest({ClassThatCallsTheSystemClass.class})` annotation at the class-level of the test case.
  1. Use `mockStatic(SystemClass.class)` to mock the system class then setup the expectations as normally.
  1. EasyMock only: Use `PowerMock.replayAll()` to change to replay mode.
  1. EasyMock only: Use `PowerMock.verifyAll()` to change to verify mode.

### Example ###

PowerMock 1.2.5 and above supports mocking methods in Java system classes such as those located in java.lang and java.net etc. This works without modifying your JVM or IDE settings! The way to go about mocking these classes are a bit different than usual though. Normally you would prepare the class that contains the static methods (let's call it X) you like to mock but because it's impossible for PowerMock to prepare a system class for testing so another approach has to be taken. So instead of preparing X you prepare the class that calls the static methods in X! Let's look at a simple example:


```
public class SystemClassUser {

	public String performEncode() throws UnsupportedEncodingException {
		return URLEncoder.encode("string", "enc");
	}
}
```

Here we'd like to mock the static method call to `java.net.URLEncoder#encode(..)` which is normally not possible. Since the URLEncoder class is a system class we should prepare the `SystemClassUser` for test since this is the class calling the encode method of the `URLEncoder`. I.e.
```
@RunWith(PowerMockRunner.class)
@PrepareForTest( { SystemClassUser.class })
public class SystemClassUserTest {

	@Test
	public void assertThatMockingOfNonFinalSystemClassesWorks() throws Exception {
		mockStatic(URLEncoder.class);

		expect(URLEncoder.encode("string", "enc")).andReturn("something");
		replayAll();

		assertEquals("something", new SystemClassUser().performEncode());

		verifyAll();
	}
}
```


### References ###
  * [SystemClassUserTest](https://github.com/jayway/powermock/blob/master/modules/module-test/easymock/junit4-test/src/test/java/samples/junit4/system/SystemClassUserTest.java)
  * [SystemClassUser](https://github.com/jayway/powermock/blob/master/tests/utils/src/main/java/samples/system/SystemClassUser.java)