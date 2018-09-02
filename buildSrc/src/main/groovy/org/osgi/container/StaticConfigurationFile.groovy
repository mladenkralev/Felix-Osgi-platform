package org.osgi.container

import org.gradle.api.Project

class StaticConfigurationFile {
    private static Project project;

    private static void addPluginConfigurations() {
        def configurations = project.getConfigurations()

        // OSGI system bundle
        configurations.create("kernel") {
            it.transitive = false
        }

        // Added for all other bundles
        configurations.create 'core-ext', {
            it.transitive = false
        }
    }

    public static void addDependencies(Project project) {
        def dependencies = project.dependencies
        StaticConfigurationFile.project = project

        addPluginConfigurations()
                                       // GOGO Shell + equinox console
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.shell', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.command', version: '0.12.0'])
        dependencies.add('core-ext', [group: 'org.apache.felix', name: 'org.apache.felix.scr', version: '2.1.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.contenttype', version: '3.4.100'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.jobs', version: '3.8.0'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.runtime', version: '3.12.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.app', version: '1.3.400'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.common', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.console', version: '1.1.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.console', version: '1.1.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.registry', version: '3.7.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.security', version: '1.2.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.preferences', version: '3.7.100'])

        dependencies.add('core-ext', [group: 'org.eclipse.osgi', name: 'util', version:'3.1.100-v20060601'])
        dependencies.add('core-ext', [group: 'org.eclipse.osgi', name: 'org.eclipse.osgi.services', version: '3.2.100.v20100503'])

        // DS
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.ds', version: '1.5.0'])

        //Simple Configurator
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.simpleconfigurator', version: '1.1.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.simpleconfigurator.manipulator', version: '2.0.200'])

        // p2
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.publisher', version: '1.4.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.publisher.eclipse', version: '1.2.201'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.director.app', version: '1.0.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.director', version: '2.3.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.repository', version: '2.3.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.repository.tools', version: '2.1.400'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.metadata', version: '2.3.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.engine', version: '2.5.0'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.operations', version: '2.4.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.core', version: '2.4.101'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.touchpoint.eclipse', version: '2.1.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.touchpoint.natives', version: '1.2.200'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.garbagecollector', version: '1.0.300'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.metadata.repository', version: '1.2.401'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.artifact.repository', version: '1.1.650'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.jarprocessor', version: '1.0.500'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.updatesite', version: '1.0.600'])

        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.osgi.compatibility.state', version: '1.0.200'])

        dependencies.add('core-ext', [group: 'org.tukaani', name: 'xz', version: '1.5'])
        dependencies.add('core-ext', [group: 'org.eclipse', name: 'org.sat4j.core', version: '2.3.5.v201308161310'])
        dependencies.add('core-ext', [group: 'org.eclipse', name: 'org.sat4j.pb', version: '2.3.5.v201308161310'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.frameworkadmin', version: '2.0.300'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.frameworkadmin.equinox', version: '1.0.700'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.launcher', version: '1.5.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.util', version: '1.0.500'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.p2.transport.ecf', version: '1.1.300'])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.filetransfer', version: '5.0.0.v20130604-1622'])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.provider.filetransfer', version: '3.2.0.v20130604-1622'])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.identity', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.concurrent', version: '1.1.100'])

        dependencies.add('kernel', [group: 'org.eclipse.platform', name: 'org.eclipse.osgi', version: '3.11.2'])
    }
}
