package org.osgi.utils

import groovy.util.logging.Slf4j
import org.gradle.api.Project;

@Slf4j
class CopyUtil {
    /**
     * Copy core-ext, kernel, runtime configuration to folder
     * @param folder
     */
    public static void copyContainerConfigurationsToDirectory(Project project, String configuration, String folder) {
        copyConfigurationToLocal(project, configuration, folder)
        // see renameAndCopyConfigurationToLocal doc, only one bundle has a problem
        renameAndCopyConfigurationToLocal(project, "kernel", folder)
    }

    public static copyConfigurationToLocal(Project project, String configuration, String folder) {
        log.info("Copy $configuration configurations to $folder")
        project.copy {
            from project.getConfigurations().getByName(configuration)

            into folder
            include "*.*"
        }
    }
    /**
     * This is needed due the fact, osmorc IDEA plugin needs format org.eclipse.osgi_3.12.50.v0001.jar to create
     * equinox runtime... ( note the underscore )
     * @param project
     * @param configuration
     * @param folder
     * @return
     */
    public static renameAndCopyConfigurationToLocal(Project project, String configuration, String folder) {
        log.info("Copy $configuration configurations to $folder. Substitute jar's  name with follwoing pattern: first '-' -> '_'")
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
