package com.github.vkuzel.spring_boot_multi_project_plugin;

import com.github.vkuzel.spring_boot_multi_project_plugin.dependencygraph.DependencyGraphPluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.springboot.SpringBootPluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.utils.PluginUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.gradle.dependencymanagement.DependencyManagementPluginFeatures;

import java.util.Set;

public class SpringBootMultiProjectPlugin implements Plugin<Project> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootMultiProjectPlugin.class);

    private static final String SPRING_BOOT_PROJECT_NAME_PROPERTY = "springBootProject";
    private static final String SPRING_BOOT_PROJECT_DEFAULT_NAME = "core";
    private static final String JITPACK_REPOSITORY = "https://jitpack.io";
    private static final String DEPENDENCY_GRAPH_DEPENDENCY = "com.github.vkuzel:Gradle-Dependency-Graph:1.0.0";

    @Override
    public void apply(Project rootProject) {
        Set<Project> allProjects = rootProject.getAllprojects();
        allProjects.forEach(project -> {
            project.getRepositories().mavenCentral();
            project.getRepositories().maven(mavenArtifactRepository -> {
                mavenArtifactRepository.setUrl(JITPACK_REPOSITORY);
            });
        });

        Set<Project> subProjects = rootProject.getSubprojects();
        subProjects.forEach(project -> {
            new DependencyManagementPluginFeatures().apply(project);
            project.getPlugins().apply(JavaPlugin.class);
        });

        String springBootProjectName = PluginUtils.getExtraProperty(rootProject, SPRING_BOOT_PROJECT_NAME_PROPERTY, SPRING_BOOT_PROJECT_DEFAULT_NAME);
        LOGGER.debug("Spring Boot project name: " + springBootProjectName);
        Project springBootProject = rootProject.findProject(springBootProjectName);
        if (springBootProject == null) {
            throw new IllegalArgumentException("Spring Boot project " + springBootProjectName + " was not found! Make sure you configured the " + SPRING_BOOT_PROJECT_NAME_PROPERTY + " correctly.");
        }

        if (rootProject.equals(springBootProject)) {
            LOGGER.warn("Spring Boot project is same as root project which does not make too much sense!");
        }

        springBootProject.getDependencies().add("compile", DEPENDENCY_GRAPH_DEPENDENCY);
        new DependencyGraphPluginFeatures().apply(springBootProject);
        new SpringBootPluginFeatures().apply(rootProject, springBootProject);
    }
}
