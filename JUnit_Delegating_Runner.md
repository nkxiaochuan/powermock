# Using PowerMock with JUnit Delegation Runner #
Since version 1.6.0 PowerMock has support for delegating the test execution to another JUnit runner without using a JUnit Rule. This leaves the actual test-execution to or another runner of your choice. For example tests can delegate to "SpringJUnit4ClassRunner", "Parameterized" or the "Enclosed" runner. Usage example:

```
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Parameterized.class)
@PrepareForTest({FinalDemo.class, PrivateFinal.class})
public class FinalDemoTest {

    @Parameterized.Parameter(0)
    public String expected;

    @Parameterized.Parameters(name = "expected={0}")
    public static Collection<?> expections() {
        return java.util.Arrays.asList(new Object[][]{
            {"Hello altered World"}, {"something"}, {"test"}
        });
    }

    @Test
    public void assertMockFinalWithExpectationsWorks() throws Exception {
        final String argument = "hello";

        FinalDemo tested = mock(FinalDemo.class);

        when(tested.say(argument)).thenReturn(expected);

        final String actual = "" + tested.say(argument);

        verify(tested).say(argument);

        assertEquals("Expected and actual did not match", expected, actual);
    }
}
```

See examples at <a href='https://github.com/jayway/powermock/tree/master/modules/module-test/mockito/junit4-delegate/src/test/java/powermock/modules/test/mockito/junit4/delegate'>github</a> for more details and examples. Also read the <a href='http://www.jayway.com/2014/11/29/using-another-junit-runner-with-powermock/'>blog post</a>  at the <a href='http://www.jayway.com/sharing-knowledge/blog/'>Jayway Blog</a>.