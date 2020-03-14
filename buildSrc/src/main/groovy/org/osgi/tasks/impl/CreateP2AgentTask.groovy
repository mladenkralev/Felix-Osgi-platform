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

import java.nio.file.Paths

import static org.osgi.utils.PluginConstants.*;

@Slf4j
public class CreateP2AgentTask extends AbstractP2Task {

    private DefaultRepositoryHandler repositoryHandler;

    private static PluginConstants pluginConstants;

    CreateP2AgentTask() {
        log.info("Initiating CreateP2AgentTask task")
        repositoryHandler = new DefaultRepositoryHandler(project)
        pluginConstants = new PluginConstants("$project.buildDir")
    }

    @TaskAction
    public void executeAction() {
        CopyUtil.copyContainerConfigurationsToDirectory(project, "p2", P2_AGENT_BUNDLES)

        // p2 agent
        TaskUtil.createOsgiInstance(project, P2_AGENT_BUNDLES, P2_AGENT_CONFIG_INI, P2_AGENT_CONFIGURATION);
        String equinoxLauncher = FileUtil.getBundleFromDirectory(P2_AGENT_BUNDLES, "org.eclipse.equinox.launcher")
        String osgiSystemBundle = FileUtil.getBundleFromDirectory(P2_AGENT_BUNDLES, "org.eclipse.osgi_")
        ExecutableUtil.createExecutableP2Provision(P2_AGENT_FOLDER, equinoxLauncher);
        ExecutableUtil.createExecutableDirector(P2_AGENT_FOLDER, equinoxLauncher)
        ExecutableUtil.createExecutableOsgiContainer(Paths.get(P2_AGENT_FOLDER),
                osgiSystemBundle)
        createBundlesInfo(P2_AGENT_CONFIGURATION, P2_AGENT_BUNDLES)
    }
}
