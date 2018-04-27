package org.osgi.container

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler

@Slf4j
class RunEquinoxWrapper {
    private static ConfigurationContainer configurations
    private static DependencyHandler dependencies
    private static Project currentProject;

    public static String LIBS_OSGI
    public static String CONFIGURATIONS

    public RunEquinoxWrapper(Project project) {
        log.info("Initial setup started ...")

        currentProject = project
        configurations = project.configurations
        dependencies = project.dependencies



        LIBS_OSGI = "$currentProject.buildDir\\libs\\osgi" . toString()
        CONFIGURATIONS = "$currentProject.buildDir\\configuration".toString()


        project.repositories {
            mavenCentral()
            maven { url "http://dist.wso2.org/maven2/" }
            maven { url "http://www.datanucleus.org/downloads/maven2/" }
            jcenter()
        }

        log.info("Initial setup ended...")
    }

    ConfigurationContainer getConfigurations() {
        return configurations
    }

    DependencyHandler getDependencies() {
        return dependencies
    }

    Project getCurrentProject() {
        return currentProject
    }


}
