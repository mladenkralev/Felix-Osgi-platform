package org.osgi.utils

import groovy.util.logging.Slf4j

@Slf4j
public class PluginConstants {
    public static String buildDirectory
    public static String OUTPUT_FOLDER

    public static String CONTAINER_CONFIGURATIONS
    public static String CONTAINER_PLUGINS
    public static String CONTAINER_CONFIG_INI


    public static String P2_AGENT_FOLDER
    public static String P2_CONFIGURATION
    public static String P2_CONFIG_INI
    public static String P2_PLUGINS

    /**
     * Constants are assigned depending on build directory
     * @param buildDirectory
     */
    public PluginConstants(GString buildDirectory) {
        this.buildDirectory = buildDirectory;
        OUTPUT_FOLDER = "$buildDirectory" + File.separator + "p2Container";
        CONTAINER_CONFIGURATIONS = "$OUTPUT_FOLDER" + File.separator + "configuration"
        CONTAINER_PLUGINS = "$OUTPUT_FOLDER" + File.separator + "plugins".toString()
        CONTAINER_CONFIG_INI = CONTAINER_CONFIGURATIONS + File.separator + "config.ini"

        P2_AGENT_FOLDER = "$OUTPUT_FOLDER" + File.separator + "p2Agent" + File.separator
        P2_CONFIGURATION = P2_AGENT_FOLDER + "configuration"
        P2_CONFIG_INI = P2_CONFIGURATION + File.separator + "config.ini"
        P2_PLUGINS = P2_AGENT_FOLDER + "plugins".toString()
    }
}
