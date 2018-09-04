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
    private DefaultRepositoryHandler repositoryHandler;
    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"

    public static String CONFIGURATIONS;
    public static String PLUGINS;

    CreateP2Wrapper() {
        repositoryHandler = new DefaultRepositoryHandler(project);
        CONFIGURATIONS ="$project.buildDir\\configuration".toString()
        PLUGINS = "$project.buildDir\\plugins".toString()
    }

    @TaskAction
    public void executeAction() {
            CopyUtil.copyConfigurationsToDirectory(project, PLUGINS)
            createContainerRuntime()
            createBundlesInfo()
    }

    void createContainerRuntime() {
        log.info("Creating container ...")

        Properties properties = setDefaultConfigIniProperties()

        createConfigIniFile(properties)

        Path destinationFolder = Paths.get("${project.rootDir}" + "${File.separator}build")
        Path osgiCoreBundlePath = getPluginBundlesPath("org.eclipse.osgi")
        ExecutableCreator.createExecutableOsgiContainer(destinationFolder, osgiCoreBundlePath)

        Path launcherPath =getPluginBundlesPath("org.eclipse.equinox.launcher")
        ExecutableCreator.createExecutableP2Provision(destinationFolder, launcherPath)
        ExecutableCreator.createExecutableDirector(destinationFolder, launcherPath)

        log.info("Container created!")
    }

    private Path getPluginBundlesPath(String bundle) {
        new File(PLUGINS).listFiles().find() {
            it.name.contains(bundle)
        }.toPath()
    }

    private void createConfigIniFile(Properties properties) {
        File configIni = new File(project.buildDir.absolutePath +
                "\\configuration\\config.ini")

        configIni.getParentFile().mkdirs();
        if (!configIni.exists()) {
            configIni.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(configIni)
        properties.store(outputStream, "This is build generated file.")
    }

    private Properties setDefaultConfigIniProperties() {
        String simpleConfiguratorBundle = getSimpleConfiguratorBundlesPath()

        Properties properties = new Properties()
        properties.put("osgi.bundles", simpleConfiguratorBundle + "@start")
        properties.put("eclipse.consoleLog", "true")
        properties.put("equinox.use.ds", "true")
        properties.put("osgi.framework", new File(PLUGINS).listFiles().find() {
            it.name.contains("org.eclipse.osgi")
        }.toString())

        properties.put("org.eclipse.equinox.simpleconfigurator.configUrl",
                "file:" + CONFIGURATIONS + File.separator + BUNDLES_INFO.toString())
        properties
    }

    private Object getSimpleConfiguratorBundlesPath() {
        project.getConfigurations().getByName("core-ext").find {
            if (it.name.contains("org.eclipse.equinox.simpleconfigurator-1.1.200")) {
                return it.name
            }
        }
    }

    static void createBundlesInfo() {
        File bundlesInfo = new File(CONFIGURATIONS + File.separator + BUNDLES_INFO.toString())

        bundlesInfo.getParentFile().mkdirs();
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        addConfigurationInFile(bundlesInfo,PLUGINS)
    }

    private static addConfigurationInFile(File outputFile, String bundlesFolder) {
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

    static String getBundlesInfoLineFor(String symbolicName, String version, String path, boolean isStarted) {
        symbolicName + "," + version + "," + "file:/" + path + "," + "4" + "," + isStarted + "\n"
    }


}
