# Spring Boot Multi-Project Gradle Plugin

[![](https://jitpack.io/v/vkuzel/Gradle-Spring-Boot-Multi-Project-Plugin.svg)](https://jitpack.io/#vkuzel/Gradle-Spring-Boot-Multi-Project-Plugin)

This plugin wraps Spring Boot Gradle Plugin and makes it easier to use in a multi-project build.
Will be useful if you need to work with project dependencies in your application.
Plugin has been designed with following project structure in mind.

````
root project <-- Here's applied the plugin
|
+--- spring boot project <-- Sub-project with Spring Boot application
|
\--- some other subproject
````

See the [Gradle Multi Project Development Template](https://github.com/vkuzel/Gradle-Multi-Project-Development-Template) for more details.

## Features

* Adds [Maven Central Repository](http://search.maven.org) and [JitPack Repository](https://jitpack.io) to all projects in the multi-project build.
* Applies [Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-gradle-plugin.html) to a root project and preserves `findMainClass` task functionality.
* Adds new `discoverProjectDependencies` task that stores serialized version of project dependencies to file. This can be used while application's runtime to determine which sub-project should execute first, etc.
* Support for new `testFixtures` source set to store common test classes.
Heavily inspired by [testFixtures dependencies in Gradle project](https://github.com/gradle/gradle/blob/master/gradle/testFixtures.gradle).
Other projects of multi-project application can rely on this source set by declaring proper dependency.

  ````groovy
  dependencies {
      // Notice the name of configuration!
      testCompile project(path: ":core-module", configuration: "testFixturesUsageCompile")
      // Or you can depend on runtime configuration.
      testRuntime project(path: ":core-module", configuration: "testFixturesUsageRuntime")
  }
  ````
  The source set can depend on other projects by declaring `testFixturesCompile` or `testFixturesRuntime` dependencies.
  ````groovy
  dependencies {
      testFixturesCompile "some:library:1.0.0"
      testFixturesRuntime "other:library:1.0.0"
  }
  ````

## Getting Started

Add plugin dependency into the root project's `build.gradle` file. Then configure name of the Spring Boot sub-project and finally apply the plugin.

````groovy
buildscript {
    repositories {
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath "com.github.vkuzel:Gradle-Spring-Boot-Multi-Project-Plugin:2.2.0"
    }
}

ext {
    // This property is used by plugin to find Spring Boot subproject and to
    // fix the behaviour of findMainClass task.
    springBootProject = "spring-boot-subproject" // Default value is "core"
}

apply plugin: "spring-boot-multi-project"
````

In Spring Boot sub-project's `build.gradle` add path to the serialized form of project dependencies.
This is path relative to the **root-project's** resources directory.

````groovy
ext {
    // Task discoverProjectDependencies serializes project dependencies into
    // the serializedProjectDependenciesPath file. Path is relatiove to each
    // project's resources directory in multi-project build.
    serializedProjectDependenciesPath = "MyProject.ser" // Default value is "projectDependencies.ser"

    // Parameter mainClassName allows you to set Spring Boot's main class
    // explicitly and to suppress findMainClass task.
    mainClassName = "your.class.Name"
}
````

In root project run the `discoverProjectDependencies` to store dependencies to files.
Class used to preserve dependencies can be found in [Gradle Project Dependencies](https://github.com/vkuzel/Gradle-Project-Dependencies).

````bash
gradle discoverProjectDependencies
````

Start the application.

````bash
gradle bootRun
````
