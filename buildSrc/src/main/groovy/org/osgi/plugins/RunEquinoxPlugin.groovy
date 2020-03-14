package org.osgi.plugins

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.osgi.configurations.StaticConfigurationFile
import org.osgi.tasks.impl.CreateP2Container
import org.osgi.tasks.impl.CreateP2AgentTask

/**
 * Defines two tasks:
 * <ol>
 *     <li>[createP2Container] creates osgi container with jersey inside</li>
 *     <li>[createP2Agent] creates osgi runtime with p2 director, p2 </li>
 * </ol>
 *
 * Created by mladen on 11/18/2017.
 */
@Slf4j
public class RunEquinoxPlugin implements Plugin<Project> {

    void apply(Project project) {
        // inserting all of the configurations and dependecies that are needed.
        StaticConfigurationFile.addDependencies(project);

        log.info("Project properties are: " + project.properties)
        project.getConfigurations().each { log.info(it.toString()) }

        project.tasks.create('createP2Container', CreateP2Container).dependsOn("jar")
        project.tasks.create('createP2Agent', CreateP2AgentTask).mustRunAfter("createP2Container")
    }
}

