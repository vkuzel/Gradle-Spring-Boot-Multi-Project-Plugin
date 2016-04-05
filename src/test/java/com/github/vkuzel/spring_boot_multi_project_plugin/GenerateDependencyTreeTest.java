package com.github.vkuzel.spring_boot_multi_project_plugin;

import com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree.DependencyTreePluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree.GenerateDependencyTreeTask;
import com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree.Node;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

public class GenerateDependencyTreeTest {

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

        (new DependencyTreePluginFeatures()).apply(testProject);
    }

    @Test
    public void applyPluginTest() {
        Set<Task> tasks = testProject.getTasksByName("generateDependencyTree", false);
        Assert.assertEquals(1, tasks.size());

        Task anyTask = tasks.stream().findAny().orElse(null);
        Assert.assertTrue(GenerateDependencyTreeTask.class.isInstance(anyTask));
    }

    @Test
    public void generateDependencyTreeTest() {
        GenerateDependencyTreeTask task = (GenerateDependencyTreeTask) testProject.getTasksByName("generateDependencyTree", false)
                .stream().findAny().get();

        Node parent = task.generateDependencyTree(testProject);
        Assert.assertEquals("testProject", parent.getProjectName());
        Assert.assertTrue(parent.isRootProject());

        Node firstChild = parent.getChildren().get(0);
        Assert.assertEquals("subproject1", firstChild.getProjectName());
        Assert.assertFalse(firstChild.isRootProject());

        Node secondChild = firstChild.getChildren().get(0);
        Assert.assertEquals("subproject2", secondChild.getProjectName());
        Assert.assertFalse(secondChild.isRootProject());
    }
}
