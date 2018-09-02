package org.osgi.container

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile;

@Slf4j
public class CreateP2Wrapper extends DefaultTask {
    private RunEquinoxWrapper wrapper;
    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"

    CreateP2Wrapper() {
        wrapper = new RunEquinoxWrapper(project);
    }

    @TaskAction
    public void executeAction() {
            copyToLocal()

            createContainerRuntime()

            createBundlesInfo()

    }
    void createContainerRuntime() {
        log.info("Creating container ...")

        File configIni = new File(project.buildDir.absolutePath +
                "\\configuration\\config.ini")

        String simpleConfiguratorBundle = project.getConfigurations().getByName("core-ext").find {
            if(it.name.contains("org.eclipse.equinox.simpleconfigurator-1.1.200")) {
                return it.name
            }
        }

        println(simpleConfiguratorBundle)
        Properties properties = new Properties()
        properties.put("osgi.bundles",simpleConfiguratorBundle+"@start")
        properties.put("eclipse.consoleLog", "true")
        properties.put("equinox.use.ds", "true")
        properties.put("osgi.noShutdown", "true")
        properties.put("osgi.framework", new File(RunEquinoxWrapper.PLUGINS).listFiles().find() {
            it.name.contains("org.eclipse.osgi")
        }.toString())

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

        addConfigurationInFile(bundlesInfo,RunEquinoxWrapper.PLUGINS)
    }

    private addConfigurationInFile(File outputFile, String bundlesFolder) {
        def configurationBundles = StringBuilder.newInstance()

        new File(bundlesFolder).eachFile {
            Pattern p = Pattern.compile("([^a-z]+\\Qsource\\E)|(\\Qgroovy\\E)|([^\\.]\\Qgradle\\E)");

            Matcher matcher = p.matcher(it.path);
            boolean findMatch = matcher.find()

            if (!findMatch) {
                ZipFile zipFile = new ZipFile(it.path)

                Enumeration<? extends ZipEntry> entries = zipFile.entries();

                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {

                        InputStream stream = zipFile.getInputStream(entry);

                        Manifest manifest = new Manifest(stream)
                        String bundleSymbolicName = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-SymbolicName"))
                        String bundleVersion = manifest.mainAttributes.getValue(new Attributes.Name("Bundle-Version"))
                        boolean isFragment = manifest.getAttributes('Fragment-Host')
                        String path = it.path

                        int indexBadChar = bundleSymbolicName.indexOf(";")
                        if (indexBadChar != -1) {
                            bundleSymbolicName = bundleSymbolicName.substring(0, indexBadChar)
                        }

                        if (bundleSymbolicName != null && bundleVersion != null) {
                            if (isFragment) {
                                println bundleSymbolicName
                                configurationBundles << getBundlesInfoLineFor(bundleSymbolicName, bundleVersion, path, false)
                            } else {
                                configurationBundles << getBundlesInfoLineFor(bundleSymbolicName, bundleVersion, path, true)
                            }
                        }
                    }
                }
            }
        }
        outputFile.append(configurationBundles.toString())
    }

    public String getBundlesInfoLineFor(String symbolicName, String version, String path, boolean isStarted) {
        symbolicName + "," + version + "," + "file:/" + path + "," + "4" + "," + isStarted + "\n"
    }

    void copyToLocal() {
        copyConfgurationToLocal("core-ext")
        copyConfgurationToLocal("kernel")
        copyConfgurationToLocal("runtime")
    }

    private copyConfgurationToLocal(String configuration) {
        this.project.copy {
            from project.getConfigurations().getByName(configuration)

            into RunEquinoxWrapper.PLUGINS
            include "*.*"
        }
    }

    boolean createBatScript() {
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

        String osgiCoreBundlePath = new File(RunEquinoxWrapper.PLUGINS).listFiles().find() {
            it.name.contains("org.eclipse.osgi")
        }

        batFile.setText("java -jar $osgiCoreBundlePath -console -configuration configuration".toString())
    }
}