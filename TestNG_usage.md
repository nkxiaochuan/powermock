# Using PowerMock with TestNG #
Since version 1.3.5 PowerMock has basic support for TestNG. Supported versions are:
<table>
<blockquote><tr><th align='left'> <b><u>TestNG</u></b></th><th align='left'><b><u>PowerMock</u></b></th></tr>
<tr><td>5.13.1+</td><td> </td><td>1.4.5+</td></tr>
<tr><td>5.12.1</td><td> </td><td>1.3.8 - 1.4</td></tr>
<tr><td>5.11</td><td> </td><td>1.3.6 & 1.3.7</td></tr>
</table></blockquote>

## Using PowerMock 1.4.9 and 1.4.10 without Maven ##
There was a bug in the release process which excluded the PowerMockTestCase from the full release build. Please download the missing dependency [here](http://search.maven.org/remotecontent?filepath=org/powermock/powermock-module-testng-common/1.4.11/powermock-module-testng-common-1.4.11.jar) ([sources](http://search.maven.org/remotecontent?filepath=org/powermock/powermock-module-testng-common/1.4.11/powermock-module-testng-common-1.4.11-sources.jar), [javadoc](http://search.maven.org/remotecontent?filepath=org/powermock/powermock-module-testng-common/1.4.11/powermock-module-testng-common-1.4.11-javadoc.jar)).

## How to write tests ##
Just as with the JUnit runners you need to prepare the classes that are normally not mockable by using the `@PrepareForTest` annotation. A full example using the Mockito API extension:

Class under test:
```
public void methodToTest() {
   ..
   final long id = IdGenerator.generateNewId();
   ..
}
```

The test with TestNG:
```
@PrepareForTest(IdGenerator.class)
public class MyTestClass {
    @Test
    public void demoStaticMethodMocking() throws Exception {
	mockStatic(IdGenerator.class);
     
	when(IdGenerator.generateNewId()).thenReturn(2L);		
 
	new ClassUnderTest().methodToTest();
 
	// Optionally verify that the static method was actually called
	verifyStatic();
	IdGenerator.generateNewId();
    }
}
```

For this to work you need to tell TestNG to use the PowerMock object factory as seen below:

## Configure TestNG to use the PowerMock object factory ##
### Using suite.xml ###
In your `suite.xml` add the following in the suite tag:
```
object-factory="org.powermock.modules.testng.PowerMockObjectFactory"
```

e.g.
```
<suite name="dgf" verbose="10" object-factory="org.powermock.modules.testng.PowerMockObjectFactory">
    <test name="dgf">
        <classes>
            <class name="com.mycompany.Test1"/>
            <class name="com.mycompany.Test2"/>
        </classes>
    </test>
</suite>
```

If you're using Maven you may need to point out the file to Surefire:
```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-surefire-plugin</artifactId>
	<configuration>
	    <suiteXmlFiles>
		<suiteXmlFile>suite.xml</suiteXmlFile>
	    </suiteXmlFiles>
	</configuration>
</plugin>
```

### Programmatically ###
Add a method like this to your test class:
```
@ObjectFactory
public IObjectFactory getObjectFactory() {
    return new org.powermock.modules.testng.PowerMockObjectFactory();
}
```

or to be on the safe side you can also extend from the `PowerMockTestCase`:
```
@PrepareForTest(IdGenerator.class)
public class MyTestClass extends PowerMockTestCase {
   ...
}
```

For an example of this check out [the example](http://svn.xp-dev.com/svn/Powermock-Examples/trunk/powermock-examples/testng/src/test/java/me/scrobble/example/powermock/testng/ObjectFactoryExample.java) made by Adrian Moerchen.

### Mock initialization ###
Be aware that you can only create mocks in non-static "before" and "test" methods when you use PowerMock and TestNG. You cannot create mocks during field initialization. For example the following is not guaranteed to work:
```
public class SomeTest extends PowerMockTestCase{
    
    MyClass myClassMock = mock(MyClass.class); // This is not guaranteed to work!
    ...
}
```

It's also currently not possible to create mocks inside static before methods, e.g.

```
@BeforeMethod
public static void beforeMethod() throws Exception {

        MyClass myClassMock = mock(MyClass.class); // This is not guaranteed to work!
       
 }
```