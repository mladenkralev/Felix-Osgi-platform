package org.osgi.utils

import groovy.util.logging.Slf4j

@Slf4j
public class PluginConstants {
    public static String buildDirectory
    public static String CONTAINER_FOLDER

    public static String CONTAINER_CONFIGURATIONS
    public static String CONTAINER_BUNDLES
    public static String CONTAINER_CONFIG_INI

    public static String P2_AGENT_FOLDER
    public static String P2_AGENT_CONFIGURATION
    public static String P2_AGENT_CONFIG_INI
    public static String P2_AGENT_BUNDLES

    /**
     * Constants are assigned depending on build directory
     * @param buildDirectory
     */
    public PluginConstants(GString buildDirectory) {
        this.buildDirectory = buildDirectory;
        CONTAINER_FOLDER = "$buildDirectory" + File.separator + "p2Container";
        CONTAINER_CONFIGURATIONS = "$CONTAINER_FOLDER" + File.separator + "configuration"
        CONTAINER_BUNDLES = "$CONTAINER_FOLDER" + File.separator + "plugins".toString()
        CONTAINER_CONFIG_INI = CONTAINER_CONFIGURATIONS + File.separator + "config.ini"

        P2_AGENT_FOLDER = "$buildDirectory" + File.separator + "p2Agent" + File.separator
        P2_AGENT_CONFIGURATION = P2_AGENT_FOLDER + "configuration"
        P2_AGENT_CONFIG_INI = P2_AGENT_CONFIGURATION + File.separator + "config.ini"
        P2_AGENT_BUNDLES = P2_AGENT_FOLDER + "plugins".toString()
//
//        Map dummy = this.class.declaredFields.findAll { !it.synthetic }.collectEntries {
//            [ (it.name):this."$it.name" ]
//        }
    }
}
