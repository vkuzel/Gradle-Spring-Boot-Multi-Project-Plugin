package com.github.vkuzel.spring_boot_multi_project_plugin.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSetContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PluginUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginUtils.class);

    private PluginUtils() {
    }

    public static SourceSetContainer getSourceSets(Project project) {
        JavaPluginConvention plugin = project.getConvention()
                .getPlugin(JavaPluginConvention.class);
        if (plugin != null) {
            return plugin.getSourceSets();
        } else {
            throw new IllegalStateException("Project " + project.getName() + " does not have JavaPlugin applied! It's main source set cannot be found!");
        }
    }

    public static String getExtraProperty(Project project, String propertyName, String defaultValue) {
        LOGGER.debug("Getting extra property " + propertyName + " from project " + project.getName());
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

    public static void setExtraProperty(Project project, String propertyName, String propertyValue) {
        LOGGER.debug("Setting extra property " + propertyName + "=" + propertyValue + " to project " + project.getName());
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext != null) {
            if (ext.has(propertyName)) {
                LOGGER.warn("Extra property " + propertyName + " is already set it will be overwritten.");
            }

            ext.set(propertyName, propertyValue);
        } else {
            LOGGER.debug("Extra properties in project " + project.getName() + " are not available.");
        }
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
