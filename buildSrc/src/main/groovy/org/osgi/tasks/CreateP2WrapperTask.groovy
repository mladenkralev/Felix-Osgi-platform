package org.osgi.tasks

import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.osgi.configurations.DefaultRepositoryHandler
import org.osgi.utils.ExecutableUtil
import org.osgi.utils.PluginConstants
import org.osgi.utils.CopyUtil
import org.osgi.utils.TaskUtil

import java.nio.file.Path
import java.nio.file.Paths
import java.util.jar.Attributes
import java.util.jar.Manifest
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

import static org.osgi.utils.PluginConstants.*

@Slf4j
public class CreateP2WrapperTask extends DefaultTask {

    private DefaultRepositoryHandler repositoryHandler;

    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"

    private static PluginConstants pluginConstants;

    CreateP2WrapperTask() {
        repositoryHandler = new DefaultRepositoryHandler(project)
        pluginConstants = new PluginConstants("$project.buildDir")
    }

    @TaskAction
    public void executeAction() {
        CopyUtil.copyContainerConfigurationsToDirectory(project, "core-ext", CONTAINER_PLUGINS)

        //web container
        TaskUtil.createOsgiInstance(CONTAINER_PLUGINS, CONTAINER_CONFIG_INI, CONTAINER_CONFIGURATIONS);
        ExecutableUtil.createExecutableOsgiContainer(Paths.get(OUTPUT_FOLDER),
                getBundleFromDirectory(CONTAINER_PLUGINS, "org.eclipse.osgi_"))

        createBundlesInfo(CONTAINER_CONFIGURATIONS, CONTAINER_PLUGINS)
        createBundlesInfo(P2_CONFIGURATION, P2_PLUGINS)
    }

    static void createBundlesInfo(String configurationFolder, String pluginsFolder) {
        File bundlesInfo = new File(configurationFolder + File.separator + BUNDLES_INFO.toString())

        bundlesInfo.getParentFile().mkdirs();
        if (!bundlesInfo.exists()) {
            bundlesInfo.createNewFile();
        }

        addConfigurationInFile(bundlesInfo, pluginsFolder)
    }

    private static addConfigurationInFile(File outputFile, String bundlesFolder) {
        def configurationBundles = StringBuilder.newInstance()

        new File(bundlesFolder).eachFile {
            Pattern p = Pattern.compile("([^a-z]+\\Qsource\\E)|(\\Qgroovy\\E)|([^\\.]\\Qgradle\\E)");

            Matcher matcher = p.matcher(it.name);
            boolean findMatch = matcher.find()

            log.info("Entry jar is: " + it.getAbsolutePath())
            if (!findMatch) {
//                println it.absoluteFile.exists()
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

    private static String getBundlesInfoLineFor(String symbolicName, String version, String path, boolean isStarted) {
        symbolicName + "," + version + "," + "file:/" + path + "," + "4" + "," + isStarted + "\n"
    }

    private static String getBundleFromDirectory(String directory, String bundle) {
        return new File(directory).listFiles().find() {
            it.name.contains(bundle)
        }.toString()
    }

    private static Path getPluginBundlesPath(String bundle, String folder) {
        new File(folder).listFiles().find() {
            it.name.contains(bundle)
        }.toPath()
    }



}
