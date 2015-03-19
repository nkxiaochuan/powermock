## Maven setup for the EasyMock API with TestNG ##
Add the following to your pom.xml

```
<properties>
    <powermock.version>1.6.2</powermock.version>
</properties>
<dependencies>
   <dependency>
      <groupId>org.powermock</groupId>
      <artifactId>powermock-module-testng</artifactId>
      <version>${powermock.version}</version>
      <scope>test</scope>
   </dependency>
   <dependency>
      <groupId>org.powermock</groupId>
      <artifactId>powermock-api-easymock</artifactId>
      <version>${powermock.version}</version>
      <scope>test</scope>
   </dependency>  
</dependencies>
```

If you're looking for the setup for a version prior to PowerMock 1.4.6 you have to look [here](EasyMock_testng_maven_legacy.md).