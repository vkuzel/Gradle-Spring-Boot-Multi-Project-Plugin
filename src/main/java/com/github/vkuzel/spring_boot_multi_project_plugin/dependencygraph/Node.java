package com.github.vkuzel.spring_boot_multi_project_plugin.dependencygraph;

import org.gradle.api.Project;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class Node implements Serializable {
    private final String projectName;
    private final boolean rootProject;
    private final String projectDir;
    private final List<Node> children;

    public Node(Project project, List<Node> children) {
        this.projectName = project.getName();
        this.rootProject = project.getDepth() == 0;
        this.projectDir = project.getProjectDir().getName();
        this.children = children;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isRootProject() {
        return rootProject;
    }

    public List<Node> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return "Node{" +
                "projectName='" + projectName + '\'' +
                ", rootProject=" + rootProject +
                ", projectDir='" + projectDir + '\'' +
                ", children=" + children.stream().map(Node::toString).collect(Collectors.joining(", ")) +
                '}';
    }
}
