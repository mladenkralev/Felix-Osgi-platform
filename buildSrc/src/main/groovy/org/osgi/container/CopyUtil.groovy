package org.osgi.container

import org.gradle.api.Project;

class CopyUtil {
    /**
     * Copy core-ext, kernel, runtime configuration to folder
     * @param folder
     */
    public static void copyConfigurationsToDirectory(Project project, String folder) {
        copyConfigurationToLocal(project,"core-ext", folder)
        renameAndCopyConfigurationToLocal(project,"kernel", folder)
    }

    public static copyConfigurationToLocal(Project project, String configuration, String folder) {
        project.copy {
            from project.getConfigurations().getByName(configuration)

            into folder
            include "*.*"
        }
    }
    /**
     * This is needed due the fact, osmorc IDEA plugin needs format org.eclipse.osgi_3.12.50.v0001.jar to create
     * equinox runtime...
     * @param project
     * @param configuration
     * @param folder
     * @return
     */
    public static renameAndCopyConfigurationToLocal(Project project, String configuration, String folder) {
        project.copy {
            from project.getConfigurations().getByName(configuration)

            rename { String fileName ->
                String extension = ".jar"
                String name = fileName.substring(0, fileName.indexOf(extension))
                fileName.replaceFirst("-", "_")
            }

            into folder
            include "*.*"
        }
    }

}
