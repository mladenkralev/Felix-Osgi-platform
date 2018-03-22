package org.groovy.test

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
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
    private Set<Task> subProjectsJarTasks = new TreeSet<Task>()

    void apply(Project project) {

        project.task('startContainer') {

            doFirst {
                init(project)

                project.repositories {
                    mavenCentral()
                    maven { url "http://dist.wso2.org/maven2/" }
                    maven { url "http://www.datanucleus.org/downloads/maven2/" }
                }

                addingOSGIDependency()

                getConfiguration(project)

                copyToLocal(project)
            }
        }.dependsOn('build')

    }

    void getConfiguration(Project project) {
        File bundlesInfo = new File(project.buildDir.absolutePath + "\\configuration\\config.ini")

        String allBundles = getAllBundles()

        Properties properties = new Properties()

        properties.put("osgi.bundles", allBundles.toString())
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
    String getAllBundles() {
        def osgiBundles = StringBuilder.newInstance()
        osgiBundles.append(getBundlesForConfiguration("core-ext"))
        osgiBundles.append(getBundlesForConfiguration("runtime"))
        return osgiBundles.toString()
    }
    String getBundlesForConfiguration(String configuration) {
        def configurationBundles = StringBuilder.newInstance()
        configurations.getByName(configuration).findResults { dependencyJar ->
            dependencyJar.withInputStream { inputStream ->
                JarInputStream jarInputStream = new JarInputStream(inputStream)
                Manifest manifest = jarInputStream.getManifest();

                if (manifest != null) {
                    if (manifest.mainAttributes.containsKey(new Attributes.Name('Fragment-Host'))) {
                        configurationBundles <<= removeExtentionFromFile(dependencyJar.path,".jar") + ","
                    } else {
                        configurationBundles <<= removeExtentionFromFile(dependencyJar.path,".jar") + "@start,"
                    }
                }
            }
        }
        return configurationBundles.toString()
    }

    String removeExtentionFromFile(String text, String regex) {
        text.substring(0, text.lastIndexOf(regex));
    }

    void init(Project project) {
        configurations = project.configurations
        dependencies = project.dependencies
    }

    void addingOSGIDependency() {
        configurations.create 'core'

        configurations.create 'osgi-compile'
        configurations.getByName('osgi-compile').extendsFrom(configurations.getByName('compile'))

        configurations.create 'osgi-runtime'
        configurations.getByName('osgi-runtime').extendsFrom(configurations.getByName('runtime'))

        configurations.create 'core-ext', {
            it.transitive = false
        }

        dependencies.add('osgi-runtime', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.ds', version: '1.5.0'])
        dependencies.add('osgi-compile', [group: 'org.osgi', name: 'org.osgi.service.component.annotations', version: '1.3.0'])

        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.shell', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.command', version: '0.12.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.equinox',  name: 'org.eclipse.equinox.console', version: '1.0.0.v20120522-1841'])
        dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.preferences', version: '3.4.1'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.common', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.registry', version: '3.7.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.contenttype', version: '3.4.100'])

        //dependencies.add('core-ext', [group: 'org.eclipse.equinox', name: 'org.eclipse.equinox.preferences', version: '3.2.0'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.repository', version: '2.3.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.metadata', version: '2.3.100'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.director.app', version: '1.0.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.director', version: '2.3.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.app', version: '1.3.400'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.engine', version: '2.4.100'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.operations', version: '2.4.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.jobs', version: '3.8.0'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.supplement', version: '1.6.100'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.runtime', version: '3.12.0'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.security', version: '1.2.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.core', version: '2.4.100'])
        dependencies.add( 'core-ext',[group: 'org.apache.felix', name: 'org.apache.felix.scr', version: '2.0.6'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.metadata.repository', version: '1.2.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.artifact.repository', version: '1.1.600'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.jarprocessor', version: '1.0.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.util', version: '1.0.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.simpleconfigurator', version: '1.1.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.simpleconfigurator.manipulator', version: '2.0.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name:'org.eclipse.equinox.console', version: '1.1.200']) {
            exclude group: 'org.apache.felix'
        }

        dependencies.add( 'core-ext',[group: 'org.osgi', name: 'org.osgi.service.cm', version: '1.5.0'])
        dependencies.add( 'core-ext',[group: 'org.osgi', name: 'org.osgi.service.component', version: '1.3.0'])
        dependencies.add( 'core-ext',[group: 'org.osgi', name: 'org.osgi.util.promise', version: '1.0.0'])
        dependencies.add( 'core-ext', [group: 'org.osgi', name: 'org.osgi.util.function', version: '1.0.0'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.touchpoint.eclipse', version: '2.1.400'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.touchpoint.natives', version: '1.2.100'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.frameworkadmin', version: '2.0.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.frameworkadmin.equinox', version: '1.0.700'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.garbagecollector', version: '1.0.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.osgi.compatibility.state', version: '1.0.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.transport.ecf', version: '1.1.201'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.ecf', name: 'org.eclipse.ecf', version: '3.1.300.v20110531-2218'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.net', version: '1.3.0'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.identity', version: '3.8.0'])
        dependencies.add( 'core-ext',[group: 'org.tukaani', name: 'xz', version: '1.5'])
        dependencies.add( 'core-ext',[group: 'org.eclipse', name: 'org.sat4j.core', version: '2.3.5.v201308161310'])
        dependencies.add( 'core-ext',[group: 'org.eclipse', name: 'org.sat4j.pb', version: '2.3.5.v201308161310'])

        configurations.create("kernel") {
            it.transitive = false
        }
        dependencies.add('kernel', [group: 'org.eclipse', name: 'osgi', version: '3.10.0-v20140606-1445'])
    }

    void copyToLocal(Project project) {
        copyConfgiurationToLocal(project, "core-ext")
        copyConfgiurationToLocal(project, "kernel")
        copyConfgiurationToLocal(project, "core")
        copyConfgiurationToLocal(project, "runtime")
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