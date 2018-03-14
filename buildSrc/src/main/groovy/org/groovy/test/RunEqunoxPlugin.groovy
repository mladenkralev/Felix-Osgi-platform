package org.groovy.test

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler

import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.Attributes
import java.util.jar.JarInputStream
import java.util.jar.Manifest

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

            }
        }.dependsOn("build")
    }

    void getConfiguration(Project project) {
        File bundlesInfo = new File(project.buildDir.absolutePath + "\\configuration\\config.ini")

        String osgiBundles = getCoreBundles()

        Properties properties = new Properties()

        properties.put("osgi.bundles", osgiBundles.toString())
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

    String getCoreBundles() {
        def osgiBundles = StringBuilder.newInstance()
        configurations.getByName("core-ext").findResults { dependencyJar ->
            dependencyJar.withInputStream { inputStream ->
                JarInputStream jarInputStream = new JarInputStream(inputStream)
                Manifest manifest = jarInputStream.getManifest();

                if (manifest != null) {
                    if (manifest.mainAttributes.containsKey(new Attributes.Name('Fragment-Host'))) {
                        osgiBundles <<= replaceLast(dependencyJar.path,".+\\.jar", "")
                        println("I have found fragment!" + dependencyJar.path)
                    } else {
                        println(replaceLast(dependencyJar.path,".+\\.jar", ""))
                        osgiBundles <<= replaceLast(dependencyJar.path,".+\\.jar", "")
                    }
                }
            }
        }
        return osgiBundles.toString()
    }

    String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, '$1' + replacement);
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

        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.console', version: '1.0.0.v20120522-1841'])

        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.artifact.repository', version: '1.1.200.v20130515-2028'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.console', version: '1.0.300.v20130327-2119'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.core', version: '2.3.0.v20130327-2119'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.director.app', version: '1.0.300.v20130819-1621'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.director', version: '2.3.0.v20130526-0335'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.directorywatcher', version: '1.0.300.v20130327-2119'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.discovery.compatibility', version: '1.0.201'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.discovery', version: '1.0.400' ])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.engine', version: '2.3.0.v20130526-2122' ])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.extensionlocation', version: '1.2.100.v20130327-2119'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.garbagecollector', version: '1.0.200.v20130327-2119'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.jarprocessor', version: '1.0.500'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.metadata.repository', version: '1.2.300'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.metadata', version: '2.1.0.v20110510'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.operations', version: '2.4.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.publisher.eclipse', version: '1.0.0.v20110511'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.publisher', version: '1.2.0.v20110511'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.reconciler.dropins', version: '1.1.100.v20110510'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.repository.tools', version: '2.0.100.v20110512-1320'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.repository', version: '2.1.0.v20110601'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.touchpoint.eclipse', version: '2.1.0.v20110511'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.touchpoint.natives', version: '1.0.300.v20110502-1955'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.transport.ecf', version: '1.1.300' ])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf', version: '3.8.0' ])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui.discovery', version: '1.0.201'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui.importexport', version: '1.1.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui.importexport', version: '1.1.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui.sdk.scheduler', version: '1.3.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui.sdk', version: '1.0.400'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.ui', version: '2.4.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.updatechecker', version: '1.1.300'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.p2.updatesite', version: '1.0.300.v20110510'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.security', version: '1.2.0.v20130424-1801'])
        dependencies.add('core-ext', [group: 'com.ibm.icu', name: 'icu4j', version: '4.0.1'])
        dependencies.add('core-ext', [ group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.frameworkadmin', version: '2.0.100.v20130327-2119'])

        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.runtime', version: '3.6.0.v20100505'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.registry', version: '3.5.101'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.preferences', version: '3.4.1'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.contenttype', version: '3.4.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.runtime.compatibility.auth', version: '3.2.200.v20100517'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.app', version: '1.3.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'jobs', version: '3.6.0-v20140424-0053'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.common', version: '3.6.0'])


        configurations.getByName('core-ext').each {
            println(it)
        }



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