package org.osgi.tasks.impl;

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.TaskAction;
import org.osgi.configurations.DefaultRepositoryHandler
import org.osgi.tasks.AbstractP2Task;
import org.osgi.utils.CopyUtil
import org.osgi.utils.ExecutableUtil
import org.osgi.utils.FileUtil;
import org.osgi.utils.PluginConstants
import org.osgi.utils.TaskUtil

import static org.osgi.utils.PluginConstants.*;


@Slf4j
public class CreateP2AgentTask extends AbstractP2Task {

    private DefaultRepositoryHandler repositoryHandler;

    private static PluginConstants pluginConstants;

    CreateP2AgentTask() {
        log.debug("Entering CreateP2Agent()")
        repositoryHandler = new DefaultRepositoryHandler(project)
        pluginConstants = new PluginConstants("$project.buildDir")
    }

    @TaskAction
    public void executeAction() {
        CopyUtil.copyContainerConfigurationsToDirectory(project, "p2", P2_AGENT_BUNDLES)

        // p2 agent
        TaskUtil.createOsgiInstance(project, P2_AGENT_BUNDLES, P2_AGENT_CONFIG_INI, P2_AGENT_CONFIGURATION);
        String p2Launcher = FileUtil.getBundleFromDirectory(P2_AGENT_BUNDLES, "org.eclipse.equinox.launcher")
        ExecutableUtil.createExecutableP2Provision(P2_AGENT_FOLDER, p2Launcher);
        ExecutableUtil.createExecutableDirector(P2_AGENT_FOLDER, p2Launcher)
        createBundlesInfo(P2_AGENT_CONFIGURATION, P2_AGENT_BUNDLES)
    }
}
