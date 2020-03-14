package org.osgi.tasks.impl

import groovy.util.logging.Slf4j
import org.gradle.api.tasks.TaskAction
import org.osgi.configurations.DefaultRepositoryHandler
import org.osgi.tasks.AbstractP2Task
import org.osgi.utils.ExecutableUtil
import org.osgi.utils.FileUtil
import org.osgi.utils.PluginConstants
import org.osgi.utils.CopyUtil
import org.osgi.utils.TaskUtil

import java.nio.file.Paths

import static org.osgi.utils.PluginConstants.*

@Slf4j
public class CreateP2Container extends AbstractP2Task{

    private DefaultRepositoryHandler repositoryHandler;

    private static PluginConstants pluginConstants;

    CreateP2Container() {
        repositoryHandler = new DefaultRepositoryHandler(project)
        pluginConstants = new PluginConstants("$project.buildDir")
    }

    @TaskAction
    public void executeAction() {
        CopyUtil.copyContainerConfigurationsToDirectory(project, "core-ext", CONTAINER_BUNDLES)

        //web container
        TaskUtil.createOsgiInstance(project, CONTAINER_BUNDLES, CONTAINER_CONFIG_INI, CONTAINER_CONFIGURATIONS);
        String osgiSystemBundle = FileUtil.getBundleFromDirectory(CONTAINER_BUNDLES, "org.eclipse.osgi_")
        ExecutableUtil.createExecutableOsgiContainer(Paths.get(CONTAINER_FOLDER),
                osgiSystemBundle)

        createBundlesInfo(CONTAINER_CONFIGURATIONS, CONTAINER_BUNDLES)
    }
}
