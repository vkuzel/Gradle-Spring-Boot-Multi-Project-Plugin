package com.github.vkuzel.spring_boot_multi_project_plugin.projectdependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class DiscoverAndSaveProjectDependenciesTest {

    private Project testProject;

    @Before
    public void prepareTestProject() {
        testProject = ProjectBuilder.builder().withName("testProject").build();
        Project subproject1 = ProjectBuilder.builder().withName("subproject1").withParent(testProject).build();
        Project subproject2 = ProjectBuilder.builder().withName("subproject2").withParent(testProject).build();

        testProject.getPluginManager().apply(JavaPlugin.class);
        subproject1.getPluginManager().apply(JavaPlugin.class);
        subproject2.getPluginManager().apply(JavaPlugin.class);

        testProject.getDependencies().add("compile", subproject1);
        subproject1.getDependencies().add("compile", subproject2);

        (new ProjectDependenciesPluginFeatures()).apply(testProject);
    }

    @Test
    public void applyPluginTest() {
        Set<Task> tasks = testProject.getTasksByName("discoverProjectDependencies", false);
        Assert.assertEquals(1, tasks.size());

        Task anyTask = tasks.stream().findAny().orElse(null);
        Assert.assertTrue(DiscoverAndSaveProjectDependenciesTask.class.isInstance(anyTask));
    }

    @Test
    public void discoverProjectDependenciesTest() {
        DiscoverAndSaveProjectDependenciesTask task = (DiscoverAndSaveProjectDependenciesTask) testProject.getTasksByName("discoverProjectDependencies", false)
                .stream().findAny().get();

        Map<Project, ProjectDependencies> dependenciesMap = new HashMap<>();
        task.findAllDependencies(testProject, dependenciesMap);

        dependenciesMap.values();

        ProjectDependencies testProject = getDependencies("testProject", dependenciesMap);
        Assert.assertEquals("subproject1", testProject.getDependencies().get(0));

        ProjectDependencies subproject1 = getDependencies("subproject1", dependenciesMap);
        Assert.assertEquals("subproject2", subproject1.getDependencies().get(0));

        ProjectDependencies subproject2 = getDependencies("subproject2", dependenciesMap);
        Assert.assertTrue(subproject2.getDependencies().isEmpty());
    }

    private ProjectDependencies getDependencies(String projectName, Map<Project, ProjectDependencies> dependenciesMap) {
        for (Project project : dependenciesMap.keySet()) {
            if (Objects.equals(project.getName(), projectName)) {
                return dependenciesMap.get(project);
            }
        }
        throw new IllegalArgumentException("Project " + projectName + " not found!");
    }
}
