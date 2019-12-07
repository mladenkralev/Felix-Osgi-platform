package org.osgi.plugins

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.osgi.configurations.StaticConfigurationFile
import org.osgi.tasks.CreateP2WrapperTask

/**
 * Created by mladen on 11/18/2017.
 */

@Slf4j
public class RunEquinoxPlugin implements Plugin<Project> {

    void apply(Project project) {

        project.tasks.create('createP2Wrapper', CreateP2WrapperTask) {
            println project.properties
            StaticConfigurationFile.addDependencies(project);
            project.getConfigurations().each { println(it) }
        }.dependsOn("jar")

        project.tasks.create('p2') {

        }.dependsOn('createP2Wrapper')
    }
}

