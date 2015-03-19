## Maven setup for the Mockito API with TestNG for PowerMock up to 1.4.5 ##
Add the following to your pom.xml

```
<repositories>
   <repository>
      <id>powermock-repo</id>
      <url>http://powermock.googlecode.com/svn/repo/</url>
   </repository>
</repositories>
<properties>
    <powermock.version>1.4.5</powermock.version>
</properties>
<dependencies>
   <dependency>
      <groupId>org.powermock.modules</groupId>
      <artifactId>powermock-module-testng</artifactId>
      <version>${powermock.version}</version>
      <scope>test</scope>
   </dependency>
   <dependency>
      <groupId>org.powermock.api</groupId>
      <artifactId>powermock-api-mockito</artifactId>
      <version>${powermock.version}</version>
      <scope>test</scope>
   </dependency>  
</dependencies>
```