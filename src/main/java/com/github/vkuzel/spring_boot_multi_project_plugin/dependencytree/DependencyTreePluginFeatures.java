package com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree;

import org.gradle.api.Project;
import org.springframework.boot.gradle.PluginFeatures;

public class DependencyTreePluginFeatures implements PluginFeatures {

    private static final String GENERATE_DEPENDENCY_TREE_TASK_NAME = "generateDependencyTree";

    @Override
    public void apply(Project springBootProject) {
        GenerateDependencyTreeTask generate = springBootProject.getTasks().create(GENERATE_DEPENDENCY_TREE_TASK_NAME, GenerateDependencyTreeTask.class);
        String description = "Generates a project dependency tree to a file.";
        description += " File contains java-serialized object of the " + Node.class.getCanonicalName() + " class.";
        generate.setDescription(description);
        generate.setGroup("build");
    }
}
