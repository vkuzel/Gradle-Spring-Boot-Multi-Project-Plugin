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

See the [Gradle Multi Project Development Template](https://github.com/vkuzel/Gradle-Multi-Project-Development-Template) for more details about usage details.

## Features

* Adds [Maven Central Repository](http://search.maven.org) and [JitPack Repository](https://jitpack.io) to all projects in the multi-project build.
* Applies [Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-gradle-plugin.html) to a root project and preserves `findMainClass` task functionality.
* Adds new `discoverProjectDependencies` task that stores serialized version of project dependencies into file.
This comes handy in the Spring Boot application when you need to perform some actions in certain order during application's initial phase.
You can use project dependencies to decide which script should be executed first.
For example executing database scripts.

## Getting Started

Add plugin dependency into the root project's `build.gradle` file. Then configure name of the Spring Boot sub-project and finally apply the plugin.

````groovy
buildscript {
    repositories {
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
    dependencies {
        classpath "com.github.vkuzel:Gradle-Spring-Boot-Multi-Project-Plugin:2.0.0"
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

In root project run the `discoverProjectDependencies` to generate project dependencies into files.
Class used to preserve the dependencies can be found in [Gradle Project Dependencies](https://github.com/vkuzel/Gradle-Project-Dependencies).

````bash
gradle discoverProjectDependencies
````

Start the application.

````bash
gradle bootRun
````
