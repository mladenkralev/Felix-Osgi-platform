package org.groovy.test

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.tasks.Copy

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Created by mladen on 11/18/2017.
 */
/*
   What happens behind the curtain is that the abstract syntax tree (AST) is analysed and transformed at compile time.
   With the groovy.util.logging.Slf4j annotation we declare a dependency on a SLF4J logger instance which is
   then available through the log variable.
 */

@Slf4j
class RunEqunoxPlugin implements Plugin<Project> {

    private ConfigurationContainer configurations
    private DependencyHandler dependencies

    void apply(Project project) {
        project.task('startContainer') {
            doLast {
                init(project)

                addingOSGIDependency()

                getConfiguration(project)

                copyToLocal(project)

//                printBundles(project)
            }
        }.dependsOn("build")
    }

    void getConfiguration(Project project) {
        ConfigurationContainer configContainer = project.getConfigurations()
        File bundlesInfo= new File(project.buildDir.absolutePath + "\\configuration\\config.ini")
        String osgiBundles = configurations.getByName("core-ext").asPath.replace(";","@1:start,").replaceAll(".jar","")


        Properties properties = new Properties()

        properties.put("osgi.bundles", osgiBundles )
        properties.put("eclipse.ignoreApp", "true")
        properties.put("eclipse.consoleLog", "true")
        properties.put("osgi.noShutdown", "true")

        bundlesInfo.getParentFile().mkdirs(); // correct!
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(bundlesInfo)
        properties.store(outputStream, "This is build generated file.")

        createBatScript(project)

    }

    void init(Project project) {
        configurations = project.configurations
        dependencies = project.dependencies
    }

    void addingOSGIDependency() {
        configurations.create 'core'

        configurations.create 'core-ext', {
                it.transitive = false
        }

        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.shell', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.command', version: '0.12.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.console', version: '1.0.0.v20120522-1841' ])

        configurations.create("kernel") {
            it.transitive = false
        }
        dependencies.add('kernel', [group: 'org.eclipse', name: 'osgi', version: '3.10.0-v20140606-1445'])
    }

    void printBundles(Project project) {
        project.getConfigurations().each {
            println("Configuration: $it.name")
            println(it.asPath)
        }
    }

    void copyToLocal(Project project) {
        copyConfgiurationToLocal(project, "core-ext")
        copyConfgiurationToLocal(project, "kernel")
        copyConfgiurationToLocal(project, "core")
    }

    private copyConfgiurationToLocal(Project project, String configuration) {
        project.copy {
            it.from configurations.getByName(configuration)

            it.into "$project.buildDir\\libs\\osgi\\$configuration"
            it.include "*.*"
        }
    }


    boolean createBatScript(Project project) {
        Path folderRuntimePath = Paths.get(
                "${project.rootDir}" +
                        "${File.separator}build")

        File directoryBatFile = new File(folderRuntimePath.toString())

        directoryBatFile.mkdir()

        Path batFilePath = Paths.get(
                "${project.rootDir}" +
                        "${File.separator}build" +
                        "${File.separator}start.bat")

        File batFile = new File(batFilePath.toString())
        batFile.createNewFile()
        assert batFile.exists(), "$batFile wasn't created!"

        String osgiCoreBundlePath = configurations.getByName("kernel").asPath

        batFile.setText("java -jar $osgiCoreBundlePath -console -configuration configuration".toString())
    }
}