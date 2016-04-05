package com.github.vkuzel.spring_boot_multi_project_plugin.springboot;

import com.github.vkuzel.spring_boot_multi_project_plugin.utils.MultiProjectPluginFeatures;
import com.github.vkuzel.spring_boot_multi_project_plugin.utils.PluginUtils;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.springframework.boot.gradle.SpringBootPlugin;
import org.springframework.boot.gradle.run.FindMainClassTask;

public class SpringBootPluginFeatures implements MultiProjectPluginFeatures {

    private static final String FIND_MAIN_CLASS_TASK_NAME = "findMainClass";

    @Override
    public void apply(Project rootProject, Project coreProject) {
        rootProject.getPlugins().apply(SpringBootPlugin.class);

        // SpringBoot plugin is applied on rootProject but we need to search
        // for mainClass in coreProject. So find mainClass task's source set
        // is going to be changed to coreProject's source set.
        rootProject.getTasksByName(FIND_MAIN_CLASS_TASK_NAME, false).forEach(task -> {
            FindMainClassTask findMainClassTask = (FindMainClassTask) task;
            // Java plugin has to be applied on core-project before this method is called!
            SourceSet mainSourceSet = PluginUtils.findMainSourceSet(coreProject);
            if (mainSourceSet != null) {
                findMainClassTask.setMainClassSourceSetOutput(mainSourceSet.getOutput());
            }
        });
    }
}
