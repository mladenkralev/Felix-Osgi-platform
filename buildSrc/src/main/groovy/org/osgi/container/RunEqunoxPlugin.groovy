package org.osgi.container

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

@Slf4j
class RunEqunoxPlugin implements Plugin<Project> {
    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"
    private RunEquinoxWrapper wrapper;

    void apply(Project project) {

        project.task('startContainer') {

            doFirst {
                wrapper = new RunEquinoxWrapper(project);

                StaticConfigurationFile.addingContainerDependencies(wrapper)

                createContainerRuntime()

                copyToLocal()

                createBundlesInfo()
            }
        }.dependsOn('build')

    }

    void createContainerRuntime() {
        log.info("Creating container ...")

        File configIni = new File(wrapper.getCurrentProject().buildDir.absolutePath +
                "\\configuration\\config.ini")

        String simpleConfiguratorBundle = wrapper.getConfigurations().getByName("core-ext").find {
            if(it.name.contains("org.eclipse.equinox.simpleconfigurator-1.1.200")) {
                return it.name
            }
        }

        println(simpleConfiguratorBundle)
        Properties properties = new Properties()
        properties.put("osgi.bundles",simpleConfiguratorBundle+"@start")
        properties.put("eclipse.ignoreApp", "true")
        properties.put("eclipse.consoleLog", "true")
        properties.put("osgi.noShutdown", "true")

        properties.put("org.eclipse.equinox.simpleconfigurator.configUrl",
                "file:" + RunEquinoxWrapper.CONFIGURATIONS + File.separator +BUNDLES_INFO.toString())

        configIni.getParentFile().mkdirs();
        if (!configIni.exists()) {
            configIni.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(configIni)
        properties.store(outputStream, "This is build generated file.")

        createBatScript()
        log.info("Container created!")

    }

    void createBundlesInfo() {
        File bundlesInfo = new File(RunEquinoxWrapper.CONFIGURATIONS + File.separator + BUNDLES_INFO.toString())

        bundlesInfo.getParentFile().mkdirs();
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        addConfigurationInFile(bundlesInfo,"core-ext")

    }

    private addConfigurationInFile(File outputFile, String configuration) {
        def configurationBundles = StringBuilder.newInstance()

        wrapper.getConfigurations().getByName(configuration).findResults { dependencyJar ->
            dependencyJar.withInputStream { inputStream ->
                JarInputStream jarInputStream = new JarInputStream(inputStream)
                Manifest manifest = jarInputStream.getManifest();

                String symbolicName = getSymbolicName(manifest)

                String version = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-Version"))

                //assuming all of the bundles will have symbolic and version attributes
                if (manifest != null) {
                    if (manifest.mainAttributes.containsKey(new Attributes.Name('Fragment-Host'))) {
                        configurationBundles << symbolicName + "," + version + "," + "file:/" + dependencyJar.path + "," + "4" + "," + "false" + "\n"
                    } else {
                        configurationBundles << symbolicName + "," + version + "," + "file:/" + dependencyJar.path + "," + "4" + "," + "true" + "\n"
                    }
                }
            }
        }
        outputFile.setText(configurationBundles.toString())
    }

    private String getSymbolicName(Manifest manifest) {
        String symbolicName = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-SymbolicName"))
        int indexToCrop = symbolicName.indexOf(';')

        if(indexToCrop != -1) {
            symbolicName = symbolicName.substring(0, indexToCrop)
        }

        return symbolicName
    }

    void copyToLocal() {
        copyConfgiurationToLocal("core-ext")
        copyConfgiurationToLocal("kernel")
        copyConfgiurationToLocal("runtime")
    }

    private copyConfgiurationToLocal(String configuration) {
        wrapper.getCurrentProject().copy {
            it.from wrapper.getConfigurations().getByName(configuration)

            it.into RunEquinoxWrapper.LIBS_OSGI + "\\$configuration"
            it.include "*.*"
        }
    }

    boolean createBatScript() {
        def currentProject = wrapper.getCurrentProject()

        Path folderRuntimePath = Paths.get(
                "${currentProject.rootDir}" +
                        "${File.separator}build")

        File directoryBatFile = new File(folderRuntimePath.toString())

        directoryBatFile.mkdir()

        Path batFilePath = Paths.get(
                "${currentProject.rootDir}" +
                        "${File.separator}build" +
                        "${File.separator}start.bat")

        File batFile = new File(batFilePath.toString())
        batFile.createNewFile()
        assert batFile.exists(), "$batFile wasn't created!"

        String osgiCoreBundlePath = wrapper.getConfigurations().getByName("kernel").asPath

        batFile.setText("java -jar $osgiCoreBundlePath -console -configuration configuration".toString())
    }
}