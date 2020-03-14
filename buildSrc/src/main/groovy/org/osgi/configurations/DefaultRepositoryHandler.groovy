package org.osgi.configurations

import groovy.util.logging.Slf4j
import org.gradle.api.Project

/**
 * Defines project repositories that are used by the plugin
 *
 */
@Slf4j
class DefaultRepositoryHandler {

    public DefaultRepositoryHandler(Project project) {
        log.info("Defining repositories...")

        project.repositories {
            mavenCentral()
        }

        log.info("Defined repositories are: $project.getRepositories()")
    }
}
