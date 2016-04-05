package com.github.vkuzel.spring_boot_multi_project_plugin;

import com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree.DependencyTreePluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.springboot.SpringBootPluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.utils.PluginUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.springframework.boot.gradle.dependencymanagement.DependencyManagementPluginFeatures;

import java.util.Set;

public class SpringBootMultiProjectPlugin implements Plugin<Project> {

    private static final String CORE_PROJECT_NAME_PROPERTY = "coreProject";
    private static final String CORE_PROJECT_DEFAULT_NAME = "core-module"; // TODO Different default name of core project (maybe just a core).

    @Override
    public void apply(Project rootProject) {
        Set<Project> allProjects = rootProject.getAllprojects();
        allProjects.forEach(project -> project.getRepositories().mavenCentral());

        Set<Project> subProjects = rootProject.getSubprojects();
        subProjects.forEach(project -> {
            new DependencyManagementPluginFeatures().apply(project);
            project.getPlugins().apply(JavaPlugin.class);
        });

        String coreProjectName = PluginUtils.getExtraProperty(rootProject, CORE_PROJECT_NAME_PROPERTY, CORE_PROJECT_DEFAULT_NAME);
        Project coreProject = rootProject.findProject(coreProjectName);

        new DependencyTreePluginFeatures().apply(coreProject);
        new SpringBootPluginFeatures().apply(rootProject, coreProject);
    }
}
