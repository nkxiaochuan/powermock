# Using PowerMock with Mockito #
### Introduction ###
Basically, PowerMock provides a class called "PowerMockito" for creating mock/object/class and initiating verification, and expecations, everything else you can still use Mockito to setup and verify expectation (e.g. times(), anyInt()).

All usages require `@RunWith(PowerMockRunner.class)` and `@PrepareForTest` annotated at class level.

### Supported versions ###
<table>
<blockquote><tr><th align='left'> <b><u>Mockito</u></b></th><th align='left'><b><u>PowerMock</u></b></th></tr>
<tr><td>1.10.8+</td><td> </td><td>1.6.2+</td></tr>
<tr><td>1.9.5-rc1 - 1.9.5</td><td> </td><td>1.5.0 - 1.5.6</td></tr>
<tr><td>1.9.0-rc1 & 1.9.0</td><td> </td><td>1.4.10 - 1.4.12</td></tr>
<tr><td>1.8.5</td><td> </td><td>1.3.9 - 1.4.9</td></tr>
<tr><td>1.8.4</td><td> </td><td>1.3.7 & 1.3.8</td></tr>
<tr><td>1.8.3</td><td> </td><td>1.3.6</td></tr>
<tr><td>1.8.1 & 1.8.2</td><td> </td><td>1.3.5</td></tr>
<tr><td>1.8 </td><td> </td><td>1.3</td></tr>
<tr><td>1.7 </td><td> </td><td>1.2.5</td></tr>
</table></blockquote>

## Note ##
In the examples below we don't use static imports for the method in the Mockito or PowerMockito API for better understanding of where the methods are located. However we strongly encourage you to statically import the methods in your real test cases for improved readability.

## Mocking Static Method ##
How to mock and stub:
  1. Add `@PrepareForTest` at class level.
```
@PrepareForTest(Static.class) // Static.class contains static methods
```
  1. Call `PowerMockito.mockStatic()` to mock a static class (use `PowerMockito.spy(class)` to mock a specific method):
```
PowerMockito.mockStatic(Static.class);
```
  1. Just use Mockito.when() to setup your expectation:
```
Mockito.when(Static.firstStaticMethod(param)).thenReturn(value);
```

Note: If you need to mock classes loaded by the java system/bootstrap classloader (those defined in the java.lang or java.net etc) you need to use [this](MockSystem.md) approach.

### How to verify behavior ###
Verification of a static method is done in two steps.
  1. First call `PowerMockito.verifyStatic() `to start verifying behavior and then
  1. Call the static method you want to verify.
For example:
```
PowerMockito.verifyStatic(); // 1
Static.firstStaticMethod(param); // 2
```
Important: You need to call `verifyStatic()` per method verification.

### How to use argument matchers ###
Mockito matchers are may still applied to a PowerMock mock.  For example, using custom argument matchers per mocked static method:
```
PowerMockito.verifyStatic();
Static.thirdStaticMethod(Mockito.anyInt());
```

### How to verify exact number of calls ###
You can still use Mockito.VerificationMode (e.g Mockito.times(x)) with `PowerMockito.verifyStatic(Mockito.times(2))`:
```
PowerMockito.verifyStatic(Mockito.times(1));
```

### How to stub void static method to throw exception ###
If not private do:
```
PowerMockito.doThrow(new ArrayStoreException("Mock error")).when(StaticService.class);
StaticService.executeMethod();
```
Note that you can do the same for final classes/methods:
```
PowerMockito.doThrow(new ArrayStoreException("Mock error")).when(myFinalMock).myFinalMethod();
```
For private methods use PowerMockito.when, e.g.:
```
when(tested, "methodToExpect", argument).thenReturn(myReturnValue);
```


### A full example for mocking, stubbing & verifying static method ###
```
@RunWith(PowerMockRunner.class)
@PrepareForTest(Static.class)
public class YourTestCase {
    @Test
    public void testMethodThatCallsStaticMethod() {
        // mock all the static methods in a class called "Static"
        PowerMockito.mockStatic(Static.class);
        // use Mockito to set up your expectation
        Mockito.when(Static.firstStaticMethod(param)).thenReturn(value);
        Mockito.when(Static.secondStaticMethod()).thenReturn(123);

        // execute your test
        classCallStaticMethodObj.execute();

        // Different from Mockito, always use PowerMockito.verifyStatic() first
        PowerMockito.verifyStatic(Mockito.times(2));
        // Use EasyMock-like verification semantic per static method invocation
        Static.firstStaticMethod(param);

        // Remember to call verifyStatic() again
        PowerMockito.verifyStatic(); // default times is once
        Static.secondStaticMethod();

        // Again, remember to call verifyStatic()
        PowerMockito.verifyStatic(Mockito.never());
        Static.thirdStaticMethod();
    }
}
```

## Partial Mocking ##
You can use PowerMockito to partially mock a method using PowerMockito.spy. Be careful (the following is taken from the Mockito docs and applies to PowerMockito as well):

Sometimes it's impossible to use the standard `when(..)` method for stubbing spies. Example:

```
List list = new LinkedList();
List spy = spy(list);
//Impossible: real method is called so spy.get(0) throws IndexOutOfBoundsException (the list is yet empty)
when(spy.get(0)).thenReturn("foo");

//You have to use doReturn() for stubbing
doReturn("foo").when(spy).get(0);
```

## How to verify behavior ##

Just use Mockito.vertify() for standard verification:
```
Mockito.verify(mockObj, times(2)).methodToMock();
```

## How to verify private behavior ##
Use PowerMockito.verifyPrivate(), e.g.
```
verifyPrivate(tested).invoke("privateMethodName", argument1);
```
This also works for private static methods.

## How to mock construction of new objects ##
Use PowerMockito.whenNew, e.g.
```
whenNew(MyClass.class).withNoArguments().thenThrow(new IOException("error message"));
```
Note that you must prepare the class _creating_ the new instance of `MyClass` for test, not the `MyClass` itself. E.g. if the class doing `new MyClass()` is called X then you'd have to do `@PrepareForTest(X.class)` in order for `whenNew` to work:

```
@RunWith(PowerMockRunner.class)
@PrepareForTest(X.class)
public class XTest {
        @Test
        public void test() {
                whenNew(MyClass.class).withNoArguments().thenThrow(new IOException("error message"));

                X x = new X();
                x.y(); // y is the method doing "new MyClass()"
               
                ..
        }
}
```

## How to verify construction of new objects ##
Use PowerMockito.verifyNew, e.g.
```
verifyNew(MyClass.class).withNoArguments();
```

## How to use argument matchers ##
Mockito matchers are may still applied to a PowerMock mock:
```
Mockito.verify(mockObj).methodToMock(Mockito.anyInt());  
```


## A full example of spying ##
```
@RunWith(PowerMockRunner.class)
// We prepare PartialMockClass for test because it's final or we need to mock private or static methods
@PrepareForTest(PartialMockClass.class)
public class YourTestCase {
    @Test
    public void spyingWithPowerMock() {        
        PartialMockClass classUnderTest = PowerMockito.spy(new PartialMockClass());

        // use Mockito to set up your expectation
        Mockito.when(classUnderTest.methodToMock()).thenReturn(value);

        // execute your test
        classUnderTest.execute();

        // Use Mockito.verify() to verify result
        Mockito.verify(mockObj, times(2)).methodToMock();
    }
}
```

## A full example of partial mocking of a private method ##
(Available in PowerMock version 1.3.6+)
```
@RunWith(PowerMockRunner.class)
// We prepare PartialMockClass for test because it's final or we need to mock private or static methods
@PrepareForTest(PartialMockClass.class)
public class YourTestCase {
    @Test
    public void privatePartialMockingWithPowerMock() {        
        PartialMockClass classUnderTest = PowerMockito.spy(new PartialMockClass());

        // use PowerMockito to set up your expectation
        PowerMockito.doReturn(value).when(classUnderTest, "methodToMock", "parameter1");

        // execute your test
        classUnderTest.execute();

        // Use PowerMockito.verify() to verify result
        PowerMockito.verifyPrivate(classUnderTest, times(2)).invoke("methodToMock", "parameter1");
    }
}
```

## Further information ##
Have a look at the [source](https://github.com/jayway/powermock/blob/master/modules/module-test/mockito/junit4/src/test/java/samples/powermockito/junit4) in subversion for examples. Also read the PowerMockito related [blog](http://blog.jayway.com/2009/10/28/untestable-code-with-mockito-and-powermock/) at the [Jayway team blog](http://blog.jayway.com/).