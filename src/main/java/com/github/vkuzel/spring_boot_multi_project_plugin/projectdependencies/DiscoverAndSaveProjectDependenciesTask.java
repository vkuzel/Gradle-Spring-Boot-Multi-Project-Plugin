package com.github.vkuzel.spring_boot_multi_project_plugin.projectdependencies;

import com.github.vkuzel.gradle_project_dependencies.ProjectDependencies;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DiscoverAndSaveProjectDependenciesTask extends DefaultTask {

    public static final String TASK_NAME = "discoverProjectDependencies";

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoverAndSaveProjectDependenciesTask.class);

    private static final String PROJECT_DEPENDENCIES_PATH_PROPERTY = "serializedProjectDependenciesPath";
    private static final String PROJECT_DEPENDENCIES_DEFAULT_PATH = "projectDependencies.ser";

    @TaskAction
    public void discoverAndSave() {
        Project rootProject = getProject().getRootProject();
        Map<Project, ProjectDependencies> dependenciesMap = new HashMap<>();
        findAllDependencies(rootProject, dependenciesMap);

        dependenciesMap.forEach(this::save);
    }

    void findAllDependencies(Project project, Map<Project, ProjectDependencies> dependenciesMap) {
        Configuration compileConfiguration = project.getConfigurations().getByName("compile");
        ResolutionResult result = compileConfiguration.getIncoming().getResolutionResult();
        ResolvedComponentResult componentResult = result.getRoot();

        List<Project> children = componentResult.getDependencies().stream()
                .filter(ResolvedDependencyResult.class::isInstance)
                .map(dr -> ((ResolvedDependencyResult) dr).getSelected())
                .filter(cr -> ProjectComponentIdentifier.class.isInstance(cr.getId()))
                .map(cr -> {
                    ProjectComponentIdentifier projectIdentifier = (ProjectComponentIdentifier) cr.getId();
                    return project.findProject(projectIdentifier.getProjectPath());
                })
                .collect(Collectors.toList());

        ProjectDependencies projectDependencies = createProjectDependencies(project, children);
        dependenciesMap.put(project, projectDependencies);
        LOGGER.debug("Discovered project dependencies: {}", projectDependencies.toString());

        children.forEach(child -> {
            if (!dependenciesMap.containsKey(child)) {
                findAllDependencies(child, dependenciesMap);
            }
        });
    }

    private ProjectDependencies createProjectDependencies(Project project, List<Project> children) {
        return new ProjectDependencies(
                project.getName(),
                project.getProjectDir().getName(),
                project.getDepth() == 0,
                children.stream().map(Project::getName).collect(Collectors.toList())
        );
    }

    private void save(Project project, ProjectDependencies projectDependencies) {
        String projectDependenciesPath = PluginUtils.getExtraProperty(getProject(), PROJECT_DEPENDENCIES_PATH_PROPERTY, PROJECT_DEPENDENCIES_DEFAULT_PATH);
        Path path = getResourcesDir(project).resolve(projectDependenciesPath);
        PluginUtils.ensureDirectoryExists(path.getParent());
        LOGGER.info("Serialized project dependencies will be stored in {}", path.toString());

        try (
                OutputStream outputStream = Files.newOutputStream(path);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(projectDependencies);
        } catch (IOException e) {
            throw new IllegalStateException("Project dependencies cannot be written into file!", e);
        }
    }

    private Path getResourcesDir(Project project) {
        SourceSet mainSourceSet = PluginUtils.getSourceSets(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME);
        File resourcesDir = null;
        for (File dir : mainSourceSet.getResources().getSrcDirs()) {
            if (resourcesDir != null) {
                LOGGER.warn("Project {} has more than one resource dirs! This {} will be used to store serialized project dependencies.",
                        project.getName(), resourcesDir.getAbsolutePath());
                break;
            }
            resourcesDir = dir;
        }
        if (resourcesDir == null) {
            throw new IllegalStateException("There has been no resource dir found for project " + project.getName());
        }
        return resourcesDir.toPath();
    }
}
