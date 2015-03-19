![http://powermock.googlecode.com/svn/trunk/src/site/resources/images/logos/powermock.png](http://powermock.googlecode.com/svn/trunk/src/site/resources/images/logos/powermock.png)

Writing unit tests can be hard and sometimes good design has to be sacrificed for the sole purpose of testability. Often testability corresponds to good design, but this is not always the case. For example final classes and methods cannot be used, private methods sometimes need to be protected or unnecessarily moved to a collaborator, static methods should be avoided completely and so on simply because of the limitations of existing frameworks.

PowerMock is a framework that extend other mock libraries such as EasyMock with more powerful capabilities. PowerMock uses a custom classloader and bytecode manipulation to enable mocking of static methods, constructors, final classes and methods, private methods, removal of static initializers and more. By using a custom classloader no changes need to be done to the IDE or continuous integration servers which simplifies adoption.

Developers familiar with the supported mock frameworks will find PowerMock easy to use, since the entire expectation API is the same, both for static methods and constructors. PowerMock aims to extend the existing API's with a small number of methods and annotations to enable the extra features. Currently PowerMock supports EasyMock and Mockito.

When writing unit tests it is often useful to bypass encapsulation and therefore PowerMock includes several features that simplifies reflection specifically useful for testing. This allows easy access to internal state, but also simplifies partial and private mocking.

Please note that PowerMock is mainly intended for people with expert knowledge in unit testing. Putting it in the hands of junior developers may cause more harm than good.

# News #
  * 2015-03-16: PowerMock 1.6.2 has been released. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
  * 2015-01-03: PowerMock 1.6.1 has been released with support for JUnit 4.12. See [change log](https://raw.githubusercontent.com/jayway/powermock/master/changelog.txt) for details.
  * 2014-11-29: Johan blogs about the new ability to delegate to other JUnit Runners in PowerMock 1.6.0 at the <a href='http://www.jayway.com/2014/11/29/using-another-junit-runner-with-powermock/'>Jayway Blog</a>.
[older news](OldNews.md)

# Documentation #

  * [Getting started](GettingStarted.md)
  * [Motivation](Motivation.md)
  * [Javadoc](http://powermock.googlecode.com/svn/docs/powermock-1.6.2/apidocs/index.html)
  * [Javadoc EasyMock API extension](http://powermock.googlecode.com/svn/docs/powermock-1.6.2/apidocs/org/powermock/api/easymock/PowerMock.html)
  * [Javadoc Mockito API extension](http://powermock.googlecode.com/svn/docs/powermock-1.6.2/apidocs/org/powermock/api/mockito/PowerMockito.html)
  * [Javadoc Whitebox class](http://powermock.googlecode.com/svn/docs/powermock-1.6.2/apidocs/org/powermock/reflect/Whitebox.html)

## Usage ##
### EasyMock extension ###
  1. [Mocking static methods](MockStatic.md)
  1. [Mocking final methods or classes](MockFinal.md)
  1. [Mocking private methods](MockPrivate.md)
  1. [Mock construction of new objects](MockConstructor.md)
  1. [Partial Mocking](MockPartial.md)
  1. [Replay and verify all](ReplayAllAndVerifyAll.md)
  1. [Mock Policies](MockPolicies.md)
  1. [Test listeners](TestListeners.md)
  1. More to come...
### Mockito extension ###
  1. [Mockito 1.8+](MockitoUsage13.md) usage (requires PowerMock 1.3+)
  1. [Mockito 1.7](MockitoUsage.md) usage (requires PowerMock 1.2.5)
### Common ###
  1. [Bypass encapsulation](BypassEncapsulation.md)
  1. [Suppress unwanted behavior](SuppressUnwantedBehavior.md)
  1. [Test listeners](TestListeners.md)
  1. [Mock Policies](MockPolicies.md) (examples are made with EasyMock extension API)
  1. [Mock system classes](MockSystem.md) (examples are made with EasyMock extension API)

### TestNG ###
  1. [TestNG usage](TestNG_usage.md)

### JUnit ###
  1. [Delegate to another JUnit Runner](JUnit_Delegating_Runner.md)

## Tutorial ##
  1. [PowerMock tutorial](PowerMock_tutorial.md)

## Experimental ##
  1. Bootstrap using a [JUnit Rule](PowerMockRule.md)
  1. Bootstrap using a [Java Agent](PowerMockAgent.md)

## OSGi ##
  1. Using PowerMock in [OSGi](osgi.md)

# FAQ #
  * [FAQ](FAQ.md)

For questions and support please join our [Google Group](http://groups.google.com/group/powermock).


---

## Founded by: ##
[![](http://www.arctiquator.com/oppenkallkod/assets/images/jayway_logo.png)](http://www.jayway.com)
## Other open source projects: ##
[![](http://rest-assured.googlecode.com/svn/trunk/rest-assured-logo-green.png)](http://rest-assured.googlecode.com)<br>
<a href='http://code.google.com/p/awaitility'><img src='http://github.com/jayway/awaitility/raw/master/resources/Awaitility_logo_red_small.png' width='237' height='80' />
</a>