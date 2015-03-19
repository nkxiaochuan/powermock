## Maven setup for the EasyMock API with JUnit ##
Add the following to your pom.xml if you're using JUnit 4.4 or above:

```
<properties>
    <powermock.version>1.6.2</powermock.version>
</properties>
<dependencies>
   <dependency>
      <groupId>org.powermock</groupId>
      <artifactId>powermock-module-junit4</artifactId>
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

> Add the following to your pom.xml if you're using JUnit 4.0-4.3:

```
<properties>
    <powermock.version>1.6.2</powermock.version>
</properties>
<dependencies>
   <dependency>
      <groupId>org.powermock</groupId>
      <artifactId>powermock-module-junit4-legacy</artifactId>
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

> Add the following to your pom.xml if you're using JUnit 3:

```
<properties>
    <powermock.version>1.6.2</powermock.version>
</properties>
<dependencies>
   <dependency>
      <groupId>org.powermock</groupId>
      <artifactId>powermock-module-junit3</artifactId>
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

If you're looking for the setup for a version prior to PowerMock 1.4.6 you have to look [here](EasyMock_maven_legacy.md).