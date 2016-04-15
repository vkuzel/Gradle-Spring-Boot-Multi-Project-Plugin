# Spring Boot Multi-Project Gradle Plugin

[![](https://jitpack.io/v/vkuzel/Gradle-Spring-Boot-Multi-Project-Plugin.svg)](https://jitpack.io/#vkuzel/Gradle-Spring-Boot-Multi-Project-Plugin)

This plugin wraps Spring Boot Gradle Plugin and makes it easier to use in a multi-project build.
Will be especially useful if you have Spring Boot project as a sub-project as it is shown in following diagram.

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
* Adds new `generateDependencyGraph` task that stores serialized version of project dependencies into file.
This comes handy in the Spring Boot application when you need to perform some actions in certain order during application's initial phase.
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
        classpath "com.github.vkuzel:Gradle-Spring-Boot-Multi-Project-Plugin:1.2.1"
    }
}

ext {
    // This property is used by plugin to find Spring Boot subproject and to
    // fix the behaviour of findMainClass task.
    springBootProject = "spring-boot-subproject" // Default value is "core"
}

apply plugin: "spring-boot-multi-project"
````

In Spring Boot sub-project's `build.gradle` add path to the serialized form of dependency graph.
This is path relative to the **root-project's** resources directory.

````groovy
ext {
    // When executing the generateDependencyGraph task serialized of dependency
    // graph will be stored into this file.
    dependencyGraphPath = "MyGraph.ser" // Default value is "projectDependencyGraph.ser"

    // Possibility to suppress Spring Boot plugin's findMainClass task and to
    // set the main class explicitly.
    mainClassName = "your.class.Name"
}
````

In root project run the `generateDependencyGraph` to generate dependency graph to file.
Class used to preserve the graph can be found in [Gradle Dependency Graph](https://github.com/vkuzel/Gradle-Dependency-Graph) project.

````bash
gradle generateDependencyGraph
````

Start the application.

````bash
gradle bootRun
````
