package com.github.vkuzel.spring_boot_multi_project_plugin.dependencygraph;

import com.github.vkuzel.gradle_dependency_graph.Node;
import org.gradle.api.Project;
import org.springframework.boot.gradle.PluginFeatures;

public class DependencyGraphPluginFeatures implements PluginFeatures {

    private static final String GENERATE_DEPENDENCY_GRAPH_TASK_NAME = "generateDependencyGraph";

    @Override
    public void apply(Project springBootProject) {
        GenerateDependencyGraphTask generate = springBootProject.getTasks().create(GENERATE_DEPENDENCY_GRAPH_TASK_NAME, GenerateDependencyGraphTask.class);
        String description = "Generates a project dependency graph and serializes it into a file.";
        description += " Default location of the file is Spring Boot project's resource dir/projectDependencyGraph.ser.";
        description += " Serialized file is of type " + Node.class.getCanonicalName() + ".";
        description += " This class can be found in indenpendent project https://github.com/vkuzel/Gradle-Dependency-Graph";
        generate.setDescription(description);
        generate.setGroup("build");
    }
}
