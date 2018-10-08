package org.osgi.container

import groovy.util.logging.Slf4j
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * Created by mladen on 11/18/2017.
 */

@Slf4j
class RunEqunoxPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.tasks.create('createP2Wrapper', CreateP2Wrapper) {
            StaticConfigurationFile.addDependencies(project);
            project.getConfigurations().each { println(it) }
        }.dependsOn("jar")

        project.tasks.create ('p2') {

        }.dependsOn('createP2Wrapper')
    }
}

