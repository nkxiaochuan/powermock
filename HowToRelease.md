This is an example of what needs to be done to release PowerMock (version 0.9 is used as an example)

  1. Use Java 6 to test ObjenesisDeepClonerTest and easymock/junit4-test (some tests have been disabled for Java 8 due to JVM crash or byte-code issues)
  1. Update the changelog.txt and add the date when 0.9 was released to the header.
  1. Switch to JDK 8
  1. `mvn release:prepare -Prelease -DautoVersionSubmodules=true -DdevelopmentVersion=1.0-SNAPSHOT`
> > If it fails saying that pom.xml file already exists this is because of a bug in the maven release or SVN tool.
> > A work-around is to do:
      1. mvn install -Dmaven.test.skip=true -o
      1. mvn release:prepare -Dresume
  1. Copy the powermock-0.9-full.jar, powermock-0.9-src.zip and powermock-0.9-with-dependencies.zip to a new temp folder.
  1. mvn release:perform
  1. Log in to [Sonatype](https://oss.sonatype.org)
    1. Delete the release projects so that they're not synced to maven central
    1. Close and then release
  1. Check out the tag that was just created to a new folder, e.g. powermock-0.9
  1. Checkout the docs folder from svn into a new folder called docs, i.e. checkout svn/docs.
  1. Go back to the powermock-0.9 folder (switch to Java 8) and type "mvn javadoc:aggregate -Dmaven.javadoc.failOnError=false"
  1. Copy everything from powermock-0.9\target\site\apidocs to the docs\powermock-0.9\apidocs (create this folder) folder you checked out.
  1. Do `svn add * --force` in the docs folder
  1. Now we need to change the mime-type for the html files so that they are readable from the web-site. Goto the parent folder of the docs folder and:
    * If windows copy the following line to a new bat file:
> > > `@for /f %%a IN ('dir docs\powermock-0.9\*.html /s/b') do svn propset svn:mime-type text/html %%a`, then execute this file.
    * If unix/linux/mac:
> > > `find . -name '*.html' | xargs svn propset svn:mime-type text/html` && `find . -name '*.css' | xargs svn propset svn:mime-type text/css`
  1. Step into the docs folder again and do svn ci -m "Releasing PowerMock 0.9"
  1. At the powermock website, upload the artifacts copied in step 3 and write an appropriate description. Don't forget to add powermock-0.9-full.jar and powermock-0.9-src.zip as featured.
  1. Update the documentation on the wiki! Don't forget updating all the links, news etc.
  1. Send an e-mail to the google group mailing list (powermock@googlegroups.com) announcing the new release.