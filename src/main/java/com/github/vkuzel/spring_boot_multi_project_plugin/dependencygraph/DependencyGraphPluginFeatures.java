package com.github.vkuzel.spring_boot_multi_project_plugin.dependencygraph;

import org.gradle.api.Project;
import org.springframework.boot.gradle.PluginFeatures;

public class DependencyGraphPluginFeatures implements PluginFeatures {

    private static final String GENERATE_DEPENDENCY_GRAPH_TASK_NAME = "generateDependencyGraph";

    @Override
    public void apply(Project springBootProject) {
        GenerateDependencyGraphTask generate = springBootProject.getTasks().create(GENERATE_DEPENDENCY_GRAPH_TASK_NAME, GenerateDependencyGraphTask.class);
        String description = "Generates a project dependency graph to a file.";
        description += " File contains java-serialized object of the " + Node.class.getCanonicalName() + " class.";
        description += " Object's class is included in indenpendent project https://github.com/vkuzel/Gradle-Dependency-Graph";
        generate.setDescription(description);
        generate.setGroup("build");
    }
}
