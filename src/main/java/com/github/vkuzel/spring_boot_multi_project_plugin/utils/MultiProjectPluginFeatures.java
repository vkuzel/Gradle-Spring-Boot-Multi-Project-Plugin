package com.github.vkuzel.spring_boot_multi_project_plugin.utils;

import org.gradle.api.Project;

public interface MultiProjectPluginFeatures {
    void apply(Project rootProject, Project coreProject);
}
