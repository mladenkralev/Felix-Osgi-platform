package org.osgi.tasks;

import groovy.util.logging.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.osgi.configurations.DefaultRepositoryHandler;
import org.osgi.utils.CopyUtil
import org.osgi.utils.ExecutableUtil;
import org.osgi.utils.PluginConstants

import static org.osgi.utils.PluginConstants.P2_AGENT_FOLDER
import static org.osgi.utils.PluginConstants.P2_CONFIGURATION
import static org.osgi.utils.PluginConstants.P2_CONFIG_INI;
import static org.osgi.utils.PluginConstants.P2_PLUGINS;

@Slf4j
public class CreateP2AgentTask extends DefaultTask {

    private DefaultRepositoryHandler repositoryHandler;


    private static PluginConstants pluginConstants;

    CreateP2AgentTask() {
        log.debug("Entering CreateP2Agent()")
        repositoryHandler = new DefaultRepositoryHandler(project)
        pluginConstants = new PluginConstants("$project.buildDir")
    }

    @TaskAction
    public void executeAction() {
        CopyUtil.copyContainerConfigurationsToDirectory(project, "p2", P2_PLUGINS)

        // p2 agent
        createOsgiInstance(P2_PLUGINS, P2_CONFIG_INI, P2_CONFIGURATION);
        String p2Launcher = getBundleFromDirectory(P2_PLUGINS, "org.eclipse.equinox.launcher")
        ExecutableUtil.createExecutableP2Provision(P2_AGENT_FOLDER, p2Launcher);
        ExecutableUtil.createExecutableDirector(P2_AGENT_FOLDER, p2Launcher)
    }
}
