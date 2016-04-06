package com.github.vkuzel.spring_boot_multi_project_plugin.dependencytree;

import com.github.vkuzel.spring_boot_multi_project_plugin.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class GenerateDependencyTreeTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateDependencyTreeTask.class);

    private static final String DEPENDENCY_TREE_PATH_PROPERTY = "dependencyTreePath";
    private static final String DEPENDENCY_TREE_DEFAULT_PATH = "projectDependencyTree.ser";

    @TaskAction
    public void generateAndExport() {
        Project rootProject = getProject().getRootProject();
        Node dependencyTree = generateDependencyTree(rootProject);
        LOGGER.debug("Generated dependency tree: " + dependencyTree.toString());

        // TODO Instance of Path
        String dependencyTreePath = PluginUtils.getExtraProperty(getProject(), DEPENDENCY_TREE_PATH_PROPERTY, DEPENDENCY_TREE_DEFAULT_PATH);
        LOGGER.debug("Serialized dependency tree will be stored in " + dependencyTreePath);
    }

    public Node generateDependencyTree(Project rootProject) {
        Configuration compileConfiguration = rootProject.getConfigurations().getByName("compile");
        ResolutionResult result = compileConfiguration.getIncoming().getResolutionResult();
        return getDependencyNode(result.getRoot());
    }

    private Node getDependencyNode(ResolvedComponentResult componentResult) {
        List<Node> children = componentResult.getDependencies().stream()
                .filter(ResolvedDependencyResult.class::isInstance)
                .map(dr -> ((ResolvedDependencyResult) dr).getSelected())
                .filter(cr -> ProjectComponentIdentifier.class.isInstance(cr.getId()))
                .map(this::getDependencyNode)
                .collect(Collectors.toList());

        ProjectComponentIdentifier projectIdentifier = (ProjectComponentIdentifier) componentResult.getId();
        return new Node(getProject().findProject(projectIdentifier.getProjectPath()), children);
    }
}
