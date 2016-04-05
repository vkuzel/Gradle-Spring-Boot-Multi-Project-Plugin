package com.github.vkuzel.spring_boot_multi_project_plugin.utils;

import org.gradle.api.Project;
import org.gradle.api.plugins.ExtraPropertiesExtension;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public final class PluginUtils {

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
        }
        return null;
    }

    public static String getExtraProperty(Project project, String propertyName, String defaultValue) {
        ExtraPropertiesExtension ext = project.getExtensions().getExtraProperties();
        if (ext != null && ext.has(propertyName)) {
            Object property = ext.get(propertyName);
            if (property != null && property instanceof String) { // TODO Log if property is not string, etc.
                return (String) property;
            }
        }
        return defaultValue;
    }
}
