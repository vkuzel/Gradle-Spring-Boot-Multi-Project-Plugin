package com.github.vkuzel.spring_boot_multi_project_plugin.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PluginUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginUtils.class);

    private PluginUtils() {
    }

    public static SourceSet findMainSourceSet(Project project) {
        JavaPluginConvention plugin = project.getConvention()
                .getPlugin(JavaPluginConvention.class);
        if (plugin != null) {
            return plugin.getSourceSets().stream()
                    .filter(sourceSet -> SourceSet.MAIN_SOURCE_SET_NAME.equals(sourceSet.getName()))
                    .findAny()
                    .orElse(null);
        } else {
            throw new IllegalStateException("Project " + project.getName() + " does not have JavaPlugin applied! It's main source set cannot be found!");
        }
    }

    public static String getExtraProperty(Project project, String propertyName, String defaultValue) {
        LOGGER.debug("Getting extra property " + propertyName);
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext != null && ext.has(propertyName)) {
            Object property = ext.get(propertyName);
            if (property != null) {
                if (property instanceof String) {
                    return (String) property;
                } else {
                    LOGGER.warn("Extra property " + propertyName + " is not string. It's value will be ignored!");
                }
            } else {
                LOGGER.debug("Extra property " + propertyName + " is null.");
            }
        } else {
            LOGGER.debug("Extra property " + propertyName + " is not set.");
        }
        return defaultValue;
    }

    public static void ensureDirectoryExists(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new IllegalStateException("Directory " + directory.toString() + " cannot be created!", e);
            }
        }
    }
}
