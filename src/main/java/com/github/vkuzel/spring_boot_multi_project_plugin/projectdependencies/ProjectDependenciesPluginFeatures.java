package com.github.vkuzel.spring_boot_multi_project_plugin.projectdependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import org.gradle.api.Project;
import org.springframework.boot.gradle.PluginFeatures;

public class ProjectDependenciesPluginFeatures implements PluginFeatures {

    @Override
    public void apply(Project springBootProject) {
        DiscoverAndSaveProjectDependenciesTask generate = springBootProject.getTasks().create(DiscoverAndSaveProjectDependenciesTask.DISCOVER_PROJECT_DEPENDENCIES_TASK_NAME, DiscoverAndSaveProjectDependenciesTask.class);
        String description = "Discovers project dependencies and serializes it into a file.";
        description += " Default location of the file is project's resource dir/projectDependencies.ser.";
        description += " Serialized file is of type " + ProjectDependencies.class.getCanonicalName() + ".";
        description += " The serialized file class can be found in independent project https://github.com/vkuzel/Gradle-Project-Dependencies";
        generate.setDescription(description);
        generate.setGroup("build");
    }
}
