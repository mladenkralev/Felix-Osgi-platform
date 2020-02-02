package org.osgi.utils

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import java.io.File

@Slf4j
public class TaskUtil {
    public static final String BUNDLES_INFO = "org.eclipse.equinox.simpleconfigurator\\bundles.info"

    private static Project project;

    public static void createOsgiInstance(Project project, String pluginsDirectory, String osgiInstanceConfigIni, String configuration) {
        log.info("Creating web container ...")
        this.project = project;

        Properties propertiesCore = setDefaultConfigIniProperties(FileUtil.getBundleFromDirectory(pluginsDirectory, "org.eclipse.osgi_"), configuration)
        createConfigIniFile(propertiesCore, osgiInstanceConfigIni);

        log.info("Container created!")
    }

    private static void createConfigIniFile(Properties properties, String pathToConfigIni) {
        File configIni = new File(pathToConfigIni)

        configIni.getParentFile().mkdirs();
        if (!configIni.exists()) {
            configIni.createNewFile();
        }

        log.info("Properties " + properties + ". ConfigIni" + configIni)
        OutputStream outputStream = new FileOutputStream(configIni)
        properties.store(outputStream, "This is build generated file.")
    }

    private static Properties setDefaultConfigIniProperties(String osgiBundlePath, String configuration) {
        String simpleConfiguratorBundle = getSimpleConfiguratorBundlesPath()

        Properties properties = new Properties()
        properties.put("osgi.bundles", simpleConfiguratorBundle + "@start")
        properties.put("eclipse.consoleLog", "true")
        properties.put("equinox.use.ds", "true")
        properties.put("osgi.framework", osgiBundlePath)

        properties.put("org.eclipse.equinox.simpleconfigurator.configUrl",
                "file:" + configuration + File.separator + BUNDLES_INFO.toString())
        return properties
    }

    private static Object getSimpleConfiguratorBundlesPath() {
        project.getConfigurations().getByName("core-ext").find {
            if (it.name.contains("org.eclipse.equinox.simpleconfigurator-1.1.200")) {
                return it.name
            }
        }
    }
}
