package com.github.vkuzel.spring_boot_multi_project_plugin.dependencygraph;

import com.github.vkuzel.gradle_dependency_graph.Node;
import com.github.vkuzel.spring_boot_multi_project_plugin.utils.PluginUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.component.ProjectComponentIdentifier;
import org.gradle.api.artifacts.result.ResolutionResult;
import org.gradle.api.artifacts.result.ResolvedComponentResult;
import org.gradle.api.artifacts.result.ResolvedDependencyResult;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class GenerateDependencyGraphTask extends DefaultTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateDependencyGraphTask.class);

    private static final String DEPENDENCY_GRAPH_PATH_PROPERTY = "dependencyGraphPath";
    private static final String DEPENDENCY_GRAPH_DEFAULT_PATH = "projectDependencyGraph.ser";

    @TaskAction
    public void generateAndExport() {
        Project rootProject = getProject().getRootProject();
        Node dependencyGraph = generateDependencyGraph(rootProject);
        LOGGER.debug("Generated dependency graph: " + dependencyGraph.toString());

        String dependencyGraphPath = PluginUtils.getExtraProperty(getProject(), DEPENDENCY_GRAPH_PATH_PROPERTY, DEPENDENCY_GRAPH_DEFAULT_PATH);
        Path path = getResourcesDir(rootProject).resolve(dependencyGraphPath);
        PluginUtils.ensureDirectoryExists(path.getParent());
        LOGGER.warn("Serialized dependency graph will be stored in " + path.toString());

        try (
                OutputStream outputStream = Files.newOutputStream(path);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(dependencyGraph);
        } catch (IOException e) {
            throw new IllegalStateException("Dependency graph cannot be written into file!", e);
        }
    }

    private Path getResourcesDir(Project project) {
        SourceSet mainSourceSet = PluginUtils.findMainSourceSet(project);
        File resourcesDir = null;
        for (File dir : mainSourceSet.getResources().getSrcDirs()) {
            if (resourcesDir != null) {
                LOGGER.warn("Project " + project.getName() + " has more than one resource dirs!" +
                        "This " + resourcesDir.getAbsolutePath() + " will be used to store serialized dependency graph.");
                break;
            }
            resourcesDir = dir;
        }
        if (resourcesDir == null) {
            throw new IllegalStateException("There has been no resources dir found for project " + project.getName());
        }
        return resourcesDir.toPath();
    }

    public Node generateDependencyGraph(Project rootProject) {
        Configuration compileConfiguration = rootProject.getConfigurations().getByName("compile");
        ResolutionResult result = compileConfiguration.getIncoming().getResolutionResult();
        return getNode(result.getRoot());
    }

    private Node getNode(ResolvedComponentResult componentResult) {
        List<Node> children = componentResult.getDependencies().stream()
                .filter(ResolvedDependencyResult.class::isInstance)
                .map(dr -> ((ResolvedDependencyResult) dr).getSelected())
                .filter(cr -> ProjectComponentIdentifier.class.isInstance(cr.getId()))
                .map(this::getNode)
                .collect(Collectors.toList());

        ProjectComponentIdentifier projectIdentifier = (ProjectComponentIdentifier) componentResult.getId();
        return buildNode(getProject().findProject(projectIdentifier.getProjectPath()), children);
    }

    private Node buildNode(Project project, List<Node> children) {
        return new Node(
                project.getName(),
                project.getProjectDir().getName(),
                children
        );
    }
}
