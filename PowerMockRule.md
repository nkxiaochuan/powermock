# Bootstrapping using a JUnit Rule #
Since version 1.4 it's possible to bootstrap PowerMock using a [JUnit Rule](http://www.infoq.com/news/2009/07/junit-4.7-rules) instead of using the PowerMockRunner and the RunWith annotation. This allows you to use other JUnit runners while still benefiting from PowerMock's functionality. You do this by specifying:

```
   @PrepareForTest(X.class);
   public class MyTest {
        @Rule
        PowerMockRule rule = new PowerMockRule();

        // Tests goes here
        ...
   }
```

## Using PowerMockRule with Maven ##
You need to depend on these projects:
```
<dependency>
	<groupId>org.powermock</groupId>
	<artifactId>powermock-module-junit4-rule</artifactId>
	<version>${powermock.version}</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.powermock</groupId>
	<artifactId>powermock-classloading-xstream</artifactId>
	<version>${powermock.version}</version>
	<scope>test</scope>
</dependency>
```
where `${powermock.version}` is the version of PowerMock you're currently using. You can also replace `powermock-classloading-xstream` with an Objenesis version:
```
<dependency>
	<groupId>org.powermock</groupId>
	<artifactId>powermock-classloading-objenesis</artifactId>
	<version>${powermock.version}</version>
	<scope>test</scope>
</dependency>
```
How ever this version is not as stable as the xstream version but it doesn't require any additional dependencies.
<br>
<b>Warning:</b> Use version PowerMock 1.4 (or later) since prior to this version the rule didn't actually execute any test methods (but it looked like it did).<br>
<br>
<h2>Using PowerMockRule without Maven</h2>
You need to download <a href='http://repo1.maven.org/maven2/org/powermock/powermock-module-junit4-rule/1.6.2/powermock-module-junit4-rule-1.6.2.jar'>powermock-module-junit4-rule</a>, <a href='http://repo1.maven.org/maven2/org/powermock/powermock-classloading-base/1.6.2/powermock-classloading-base-1.6.2.jar'>powermock-classloading-base</a> and one of<br>
<a href='http://repo1.maven.org/maven2/org/powermock/powermock-classloading-xstream/1.6.2/powermock-classloading-xstream-1.6.2.jar'>powermock-classloading-xstream</a> or <a href='http://repo1.maven.org/maven2/org/powermock/powermock-classloading-objenesis/1.6.2/powermock-classloading-objenesis-1.6.2.jar'>powermock-classloading-objenesis</a> and put it in your classpath.<br>
<br>
<h3>References</h3>

Refer to the examples in <a href='https://github.com/jayway/powermock/blob/master/modules/module-test/mockito/junit4-rule-xstream/src/test/java/org/powermock/modules/test/junit4/rule/xstream'>git</a>.<br>
<br>
Or check out the Spring Integration Test with PowerMock and Mockito <a href='https://github.com/jayway/powermock/blob/master/examples/spring-mockito'>example</a>.