package org.groovy.test

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

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

                printBundles(project)
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
        properties.put("eclipse.ignoreApp", "true")

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
        dependencies.add( 'core' , [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.simpleconfigurator', version: '1.0.400.v20130327-2119'])


        configurations.create 'core-ext'

//        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.app', version: '1.3.100'])
//        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.publisher', version: '1.4.200'])
//        dependencies.add( 'core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.director', version: '2.3.0.v20130526-0335'])
//        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.common', version: '3.6.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.contenttype', version: '3.4.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.jobs', version: '3.5.100'])

        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.8.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.shell', version: '0.8.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.command', version: '0.8.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.preferences', version: '3.4.1'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.registry', version: '3.5.101'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.8.0'])

        configurations.create("kernel")
        dependencies.add('kernel', [group: 'org.eclipse.osgi', name: 'org.eclipse.osgi', version: '3.7.1'])
    }

    void printBundles(Project project) {
        project.getConfigurations().each {
            println("Configuration: $it.name")
            println(it.asPath)
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