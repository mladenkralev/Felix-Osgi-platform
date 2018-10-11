package org.osgi.container

import org.gradle.api.Project;

class CopyUtil {
    /**
     * Copy core-ext, kernel, runtime configuration to folder
     * @param folder
     */
    public static void copyConfigurationsToDirectory(Project project, String folder) {
        copyConfigurationToLocal(project,"core-ext", folder)
        copyConfigurationToLocal(project,"kernel", folder)
    }

    public static copyConfigurationToLocal(Project project, String configuration, String folder) {
        project.copy {
            from project.getConfigurations().getByName(configuration)

            into folder
            include "*.*"
        }
    }

}
