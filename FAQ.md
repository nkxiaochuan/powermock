# Frequently asked questions #

<ol>
<blockquote><li>PowerMockRunner throws a<br>
<pre><code>  java.lang.NoClassDefFoundError: org/junit/internal/runners/BeforeAndAfterRunner<br>
</code></pre>
or<br>
<pre><code>java.lang.SecurityException: class "org.junit.internal.runners.TestClass"'s signer information does not match signer information of other classes in the same package<br>
</code></pre>
exception. What's wrong?</blockquote>

<blockquote>You're probably using the wrong PowerMockRunner. There's one runner made for JUnit 4.4 and above and a second runner made for JUnit 4.0-4.3 (although the latter also works for some older minor versions of JUnit 4.4). Try switching from the <code>org.powermock.modules.junit4.PowerMockRunner</code> to <code>org.powermock.modules.junit4.legacy.PowerMockRunner</code> or vice versa. Look at the <a href='GettingStarted.md'>getting started</a> guide to see how to configure this in maven.</li></blockquote>

<li>Cobertura gives me errors or produces strange results when running PowerMock tests in Maven, how do I solve this?<br>
<blockquote>Either:<br>
</blockquote><ol><li>Upgrade to Cobertura 2.4+ or,<br>
</li><li>Follow the instructions on <a href='http://www.jsfblog.info/2010/02/cobertura-code-coverage-with-maven-and-powermock/'>this blog</a> or,<br>
</li><li>Add the following to your pom.xml file:<br>
<pre><code> &lt;build&gt;<br>
  &lt;plugins&gt;<br>
     &lt;plugin&gt;<br>
        &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;<br>
        &lt;configuration&gt;<br>
            &lt;forkMode&gt;pertest&lt;/forkMode&gt; <br>
        &lt;/configuration&gt;<br>
      &lt;/plugin&gt;<br>
     &lt;/plugins&gt;<br>
  &lt;/build&gt;<br>
</code></pre>
</li>
<li>I get a ClassCastException from DocumentBuilderFactory, SaxParserFactory or other XML related classes<br>
<blockquote>The reason is that the XML framework tries to instantiate classes using reflection and does this from the thread context classloader (PowerMock's classloader) but then tries to assign the created object to a field not loaded by the same classloader. When this happens you need to make use of the @PowerMockIgnore annotation to tell PowerMock to defer the loading of a certain package to the system classloader. What you need to ignore is case specific but usually it's the XML framework or some packages that interact with it. E.g. <code>@PowerMockIgnore({"org.xml.*", "javax.xml.*"})</code>. Another option would be to try to bootstrap using our <a href='PowerMockAgent.md'>Java Agent</a>.<br>
</blockquote></li></ol><ol><li>I cannot mock classes in from <code>java.lang</code>, <code>java.net</code>, <code>java.io</code> or other system classes, why?<br>
<blockquote>This is because they're loaded by Java's bootstrap classloader and cannot be byte-code manipulated by PowerMock's classloader. Since PowerMock 1.2.5 there's a work-around, please have a look at <a href='http://code.google.com/p/powermock/source/browse/trunk/modules/module-test/mockito/junit4/src/test/java/samples/powermockito/junit4/system/SystemClassUserTest.java'>this</a> simple example to see how it's done.<br>
</li>
<li>When mocking Hibernate you get an error similar to:<br>
<pre><code>java.lang.ClassCastException: org.hibernate.ejb.HibernatePersistence cannot be cast to javax.persistence.spi.PersistenceProvider<br>
    at javax.persistence.Persistence.findAllProviders(Persistence.java:80)<br>
    at javax.persistence.Persistence.createEntityManagerFactory(Persistence.java:49)<br>
    at javax.persistence.Persistence.createEntityManagerFactory(Persistence.java:34)<br>
    ...<br>
</code></pre>
Solution: Upgrade to PowerMock 1.3+ or use <code>@PowerMockIgnore("javax.persistence.*")</code> at the class-level of your test.<br>
</li>
<li>When running a PowerMock test log4j gives me the following (or something similar) error, what now?<br>
<pre><code>log4j:ERROR A "org.apache.log4j.xml.DOMConfigurator" object is not<br>
assignable to a "org.apache.log4j.spi.Configurator" variable.<br>
log4j:ERROR The class "org.apache.log4j.spi.Configurator" was loaded<br>
by<br>
log4j:ERROR [org.powermock.core.classloader.MockClassLoader@14a55f2]<br>
whereas object of type<br>
log4j:ERROR "org.apache.log4j.xml.DOMConfigurator" was loaded by<br>
[sun.misc.Launcher$AppClassLoader@92e78c].<br>
log4j:ERROR Could not instantiate configurator<br>
[org.apache.log4j.xml.DOMConfigurator].<br>
</code></pre>
or<br>
<pre><code>Caused by: org.apache.commons.logging.LogConfigurationException:<br>
Invalid class loader hierarchy.  You have more than one version of<br>
'org.apache.commons.logging.Log' visible, which is not allowed.<br>
</code></pre></blockquote></li></ol>

There are a couple of different solutions to this:<br>
<ol><li>Upgrade to PowerMock 1.3+<br>
</li><li>Make use of the @PowerMockIgnore annotation at the class-level of the test. For example if using log4j, use <code>@PowerMockIgnore("org.apache.log4j.*")</code> if using commons logging, use <code>@PowerMockIgnore("org.apache.commons.logging.*")</code>.<br>
</li><li>Add <code>-Dlog4j.ignoreTCL=true</code> as VM-Arguments to your test-run-configuration.<br>
</li><li>If you're using PowerMock 1.1 or above you should use the <code>@MockPolicy</code> annotation and specify a mock policy. For example if you're using slf4j in combination with log4j use <code>@MockPolicy(Slf4jMockPolicy.class)</code> or if you're using Log4j stand-alone use <code>@MockPolicy(Log4jMockPolicy.class)</code>. This is the recommended way. For example:<br>
<pre><code>@RunWith(PowerMockRunner.class)<br>
@MockPolicy(Log4jMockPolicy.class)<br>
public class MyTest {<br>
<br>
}<br>
</code></pre>
</li><li>Create a nice mock of the Logger class and set the the Logger field to this instance. If the field is static suppress the static initializer for the class (using the <code>@SuppressStaticInitializerFor</code> annotation) and then set the logger field to the mock you just created. Next prepare the <code>org.apache.log4j.Appender</code> for testing using the @PrepareForTest annotation. For example:<br>
<pre><code>@RunWith(PowerMockRunner.class)<br>
@SuppressStaticInitializationFor("org.myapp.MyClassUsingLog4J")<br>
@PrepareForTest( { Appender.class })<br>
public class MyTest {<br>
<br>
  @Before<br>
  public void setUp() {<br>
      Logger loggerMock = createNiceMock(Logger.class);<br>
      Whitebox.setInternalState(MyClassUsingLog4J.class, loggerMock);<br>
      ...<br>
  }<br>
  ...<br>
}<br>
</code></pre>
</li><li>Follow the same procedure as in the previous step but instead of adding the <code>org.apache.log4j.Appender</code> class to the <code>@PrepareForTest</code> annotation you add <code>"org.apache.log4j.LogManager"</code> to the <code>@SuppressStaticInitializerFor</code> annotation. For example:<br>
<pre><code>@RunWith(PowerMockRunner.class)<br>
@SuppressStaticInitializationFor( {<br>
		"org.myapp.MyClassUsingLog4J",<br>
		"org.apache.log4j.LogManager" })<br>
public class MyTest {<br>
<br>
  @Before<br>
  public void setUp() {<br>
      Logger loggerMock = createNiceMock(Logger.class);<br>
      Whitebox.setInternalState(MyClassUsingLog4J.class, loggerMock);<br>
      ...<br>
  }<br>
  ...<br>
}<br>
</code></pre>
</li><li>You could try using the <code>@PrepareEverythingForTest</code> annotation (not recommended).<br>
</li>
<li>Does PowerMock work with TestNG?</li></ol>

Yes, since version 1.3.5 PowerMock does have basic TestNG support.<br>
</li>
<li>Is PowerMock a fork of EasyMock?<br>
<br>
No. PowerMock extends other mock frameworks such as EasyMock with powerful capabilities such as static mocking.<br>
</li>
<li>Can you use PowerMock with other frameworks that uses a JUnit runner?<br>
<br>
Yes, you can make use of the <a href='http://code.google.com/p/powermock/wiki/PowerMockRule'>PowerMockRule</a>.<br>
</li>
<li>I'm using the Java Agent and Java 7 run into errors like "Unable to load Java agent", what to do?<br>
<br>
You can try the following work-around:<br>
<pre><code> &lt;plugin&gt;<br>
        &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;<br>
        &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;<br>
        &lt;version&gt;2.14&lt;/version&gt;<br>
        &lt;configuration&gt;<br>
            &lt;argLine&gt;-javaagent:${settings.localRepository}/org/powermock/powermock-module-javaagent/${powermock.version}/powermock-module-javaagent-${powermock.version}.jar -XX:-UseSplitVerifier&lt;/argLine&gt;<br>
        &lt;/configuration&gt;<br>
&lt;/plugin&gt; <br>
</code></pre>
</li>
</ol>