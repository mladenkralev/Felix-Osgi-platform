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

    public static String OUTPUT_FOLDER;

    public static String CONTAINER_CONFIGURATIONS;
    public static String CONTAINER_PLUGINS;
    public static String CONTAINER_CONFIG_INI;

    public static String P2_PLUGINS;
    public static String P2_CONFIG_INI;

    CreateP2Wrapper() {
        repositoryHandler = new DefaultRepositoryHandler(project);
        OUTPUT_FOLDER= "$project.buildDir" + File.separator + "p2Container"

        CONTAINER_CONFIGURATIONS = "$OUTPUT_FOLDER" + File.separator + "configuration".toString()
        CONTAINER_PLUGINS = "$OUTPUT_FOLDER" + File.separator + "plugins".toString()
        CONTAINER_CONFIG_INI= "$OUTPUT_FOLDER" + File.separator + "configuration" + File.separator + "config.ini"

        P2_CONFIG_INI= "$OUTPUT_FOLDER" + File.separator + "p2" + File.separator + "configuration" + File.separator + "config.ini"
        P2_PLUGINS = "$OUTPUT_FOLDER" + File.separator + "p2" + File.separator + "plugins".toString()
    }

    @TaskAction
    public void executeAction() {
            CopyUtil.copyContainerConfigurationsToDirectory(project, CONTAINER_PLUGINS)
            CopyUtil.copyP2ConfigurationsToDirectory(project, P2_PLUGINS)
            createContainerRuntime()
            createBundlesInfo()
    }

    void createContainerRuntime() {
        log.info("Creating container ...")
        // TODO DUPLICATION
        String containerCoreOsgiBundlePath= new File(CONTAINER_PLUGINS).listFiles().find() {
            it.name.contains("org.eclipse.osgi_")
        }.toString()
        Properties propertiesCore = setDefaultConfigIniProperties(containerCoreOsgiBundlePath)
        createConfigIniFile(propertiesCore, containerCoreOsgiBundlePath);

        // TODO DUPLICATION
        String p2CoreOsgiBundlePath= new File(P2_PLUGINS).listFiles().find() {
            it.name.contains("org.eclipse.osgi_")
        }.toString()
        Properties propertiesP2 = setDefaultConfigIniProperties(p2CoreOsgiBundlePath)
        createConfigIniFile(propertiesP2, p2CoreOsgiBundlePath);

        Path containerFolder = Paths.get(OUTPUT_FOLDER)
        Path osgiCoreBundlePath = getPluginBundlesPath("org.eclipse.osgi_", CONTAINER_PLUGINS)
        ExecutableCreator.createExecutableOsgiContainer(containerFolder, osgiCoreBundlePath)

        Path launcherPath = getPluginBundlesPath("org.eclipse.equinox.launcher", P2_PLUGINS)
        ExecutableCreator.createExecutableP2Provision(containerFolder, launcherPath)
        ExecutableCreator.createExecutableDirector(containerFolder, launcherPath)

        log.info("Container created!")
    }
    // TODO can be generalized and code can be redused, see how osgi core bundle is looked
    private static Path getPluginBundlesPath(String bundle, String folder) {
        new File(folder).listFiles().find() {
            it.name.contains(bundle)
        }.toPath()
    }

    private static void createConfigIniFile(Properties properties, String pathToConfigIni) {
        File configIni = new File(pathToConfigIni)

        configIni.getParentFile().mkdirs();
        if (!configIni.exists()) {
            configIni.createNewFile();
        }

        OutputStream outputStream = new FileOutputStream(configIni)
        properties.store(outputStream, "This is build generated file.")
    }

    private Properties setDefaultConfigIniProperties(String osgiBundlePath) {
        String simpleConfiguratorBundle = getSimpleConfiguratorBundlesPath()

        Properties properties = new Properties()
        properties.put("osgi.bundles", simpleConfiguratorBundle + "@start")
        properties.put("eclipse.consoleLog", "true")
        properties.put("equinox.use.ds", "true")
        properties.put("osgi.framework", osgiBundlePath)

        properties.put("org.eclipse.equinox.simpleconfigurator.configUrl",
                "file:" + CONTAINER_CONFIGURATIONS + File.separator + BUNDLES_INFO.toString())
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
        File bundlesInfo = new File(CONTAINER_CONFIGURATIONS + File.separator + BUNDLES_INFO.toString())

        bundlesInfo.getParentFile().mkdirs();
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        addConfigurationInFile(bundlesInfo,CONTAINER_PLUGINS)
    }

    private static addConfigurationInFile(File outputFile, String bundlesFolder) {
        def configurationBundles = StringBuilder.newInstance()

        new File(bundlesFolder).eachFile {
            Pattern p = Pattern.compile("([^a-z]+\\Qsource\\E)|(\\Qgroovy\\E)|([^\\.]\\Qgradle\\E)");
            println(it.name)
            Matcher matcher = p.matcher(it.path);
            boolean findMatch = matcher.find()

            log.info("Entry jar is: " + it.getAbsolutePath())
            if (!findMatch) {
                println it.absoluteFile.exists()
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
