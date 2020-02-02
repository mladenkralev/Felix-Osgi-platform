package org.osgi.plugins

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.osgi.configurations.StaticConfigurationFile
import org.osgi.tasks.impl.CreateP2WrapperTask
import org.osgi.tasks.impl.CreateP2AgentTask

/**
 * Created by mladen on 11/18/2017.
 */

@Slf4j
public class RunEquinoxPlugin implements Plugin<Project> {

    void apply(Project project) {
        StaticConfigurationFile.addDependencies(project);
        log.info("Project properties are: " + project.properties)

        project.tasks.create('createP2Wrapper', CreateP2WrapperTask) {
            project.getConfigurations().each { println(it) }
        }.dependsOn("jar")

        project.tasks.create('createP2Agent', CreateP2AgentTask){
            project.getConfigurations().each { println(it) }
        }.mustRunAfter("createP2Wrapper")
    }
}

