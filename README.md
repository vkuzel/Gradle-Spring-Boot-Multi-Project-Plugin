# Spring Boot Multi-Project Gradle Plugin

This is wrapper of Spring Boot Gradle Plugin so it's easier to use with multi-project configuration.

TODO Details about this plugin and it's setup.

````groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath "org.springframework.boot:spring-boot-gradle-plugin:1.3.3.RELEASE"
        classpath files('lib/spring-boot-multi-project-gradle-plugin-0.0.1-SNAPSHOT.jar')
    }
}

apply plugin: 'spring-boot-multi-project'
````