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


        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.contenttype', version: '3.4.100'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.jobs', version: '3.8.0'])
        dependencies.add( 'core-ext',[group: 'org.eclipse.platform', name: 'org.eclipse.core.runtime', version: '3.12.0'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.app', version: '1.3.400'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.common', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.registry', version: '3.7.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.security', version: '1.2.200'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.preferences', version: '3.7.100'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.osgi.util', version:'3.5.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.osgi.services', version: '3.7.0'])

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
        dependencies.add( 'core-ext',[group: 'org.eclipse.equinox.p2', name: 'ql', version: '2.0.100'])


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
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.identity', version: '3.9.0'])
        dependencies.add('core-ext', [group: 'org.eclipse.ecf', name: 'org.eclipse.ecf.provider.filetransfer', version: '3.2.0.v20130604-1622'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.concurrent', version: '1.1.100'])

        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.core.net.linux.x86_64', version: '1.2.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.platform', name: 'org.eclipse.core.net.win32.x86_64', version: '1.1.100'])
        dependencies.add('core-ext', [group: 'org.eclipse.core', name: 'org.eclipse.core.net', version: '1.2.0.I20090522-1010'])

        // Extra gson
        dependencies.add('core-ext', [group: 'com.google.code.gson', name: 'gson', version: '2.8.5'])

        dependencies.add('core-ext', [group: 'org.glassfish.jersey.core', name: 'jersey-server', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.media', name: 'jersey-media-sse', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.media', name: 'jersey-media-json-binding', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.media', name: 'jersey-media-jaxb', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.inject', name: 'jersey-hk2', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.containers', name: 'jersey-container-servlet', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.containers', name: 'jersey-container-servlet-core', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.core', name: 'jersey-common', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.core', name: 'jersey-client', version: '2.26' ])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.containers', name: 'jersey-container-grizzly2-http', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.glassfish.jersey.media', name: 'jersey-media-multipart', version: '2.26'])
        dependencies.add('core-ext', [group: 'org.jvnet.mimepull', name: 'mimepull', version: '1.9.11'])

        dependencies.add('core-ext', [group: 'org.eclipse', name: 'yasson', version: '1.0.3'])
        dependencies.add('core-ext', [group: 'javax.validation', name: 'validation-api', version: '2.0.1.Final'])
        dependencies.add('core-ext', [group: 'org.glassfish.hk2', name: 'osgi-resource-locator', version: '1.0.3'])
        dependencies.add('core-ext', [group: 'org.javassist', name: 'javassist', version: '3.22.0-GA'])
        dependencies.add('core-ext', [group: 'org.glassfish.hk2.external', name: 'jakarta.inject', version: '2.5.0'])
        dependencies.add('core-ext', [group: 'jakarta.annotation', name: 'jakarta.annotation-api', version: '1.3.4'])
        dependencies.add('core-ext', [group: 'jakarta.activation', name: 'jakarta.activation-api', version: '1.2.1'])
        dependencies.add('core-ext', [group: 'org.glassfish.hk2.external', name: 'aopalliance-repackaged', version: '2.5.0'])
        dependencies.add('core-ext', [group: 'com.sun.activation', name: 'javax.activation', version: '1.2.0'])
        dependencies.add('core-ext', [group: 'javax.ws.rs', name: 'javax.ws.rs-api', version: '2.1.1'])
        dependencies.add('core-ext', [group: 'javax.annotation', name: 'javax.annotation-api', version: '1.3.2'])


        dependencies.add('core-ext', [group: 'org.glassfish.grizzly', name: 'grizzly-http-servlet', version: '2.4.4'])
        dependencies.add('core-ext', [group: 'org.glassfish.grizzly', name: 'grizzly-http-server', version: '2.4.4'])
        dependencies.add('core-ext', [group: 'org.glassfish.grizzly', name: 'grizzly-framework', version: '2.4.4'])
        dependencies.add('core-ext', [group: 'org.glassfish.grizzly.osgi', name: 'grizzly-httpservice-bundle', version: '2.4.4'])
        dependencies.add('core-ext', [group: 'org.glassfish.grizzly', name: 'grizzly-comet', version: '2.4.4'])

        dependencies.add('core-ext', [group: 'jakarta.xml.bind', name: 'jakarta.xml.bind-api', version: '2.3.2'])
        dependencies.add('core-ext', [group: 'jakarta.ws.rs', name: 'jakarta.ws.rs-api', version: '2.1.5'])
        dependencies.add('core-ext', [group: 'jakarta.servlet', name: 'jakarta.servlet-api', version: '4.0.2'])
        dependencies.add('core-ext', [group: 'jakarta.persistence', name: 'jakarta.persistence-api', version: '2.2.2'])
        dependencies.add('core-ext', [group: 'jakarta.json.bind', name: 'jakarta.json.bind-api', version: '1.0.1'])
        dependencies.add('core-ext', [group: 'jakarta.json', name: 'jakarta.json-api', version: '1.1.5'])

        dependencies.add('core-ext', [group: 'org.glassfish.hk2', name: 'hk2-locator', version: '2.5.0'])
        dependencies.add('core-ext', [group: 'org.glassfish.hk2', name: 'hk2-api', version: '2.5.0'])
        dependencies.add('core-ext', [group: 'org.glassfish.hk2', name: 'hk2-utils', version: '2.5.0'])

        // GOGO Shell + equinox console + osgi runtime + eclipse console
        dependencies.add('kernel', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.runtime', version: '0.12.0'])
        dependencies.add('kernel', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.shell', version: '0.12.0'])
        dependencies.add('kernel', [group: 'org.apache.felix', name: 'org.apache.felix.gogo.command', version: '0.12.0'])
        dependencies.add('kernel', [group: 'org.apache.felix', name: 'org.apache.felix.scr', version: '2.1.0'])
        dependencies.add('kernel', [group: 'org.eclipse.platform', name: 'org.eclipse.equinox.console', version: '1.1.200'])
        dependencies.add('kernel', [group: 'org.eclipse.platform', name:'org.eclipse.osgi', version: '3.13.200'])
    }
}
