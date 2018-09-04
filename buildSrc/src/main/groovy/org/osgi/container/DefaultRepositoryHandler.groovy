package org.osgi.container

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler

@Slf4j
class RepositoryHandler {

    public RepositoryHandler(Project project) {
        log.info("Initial setup started ...")
        
        project.repositories {
            mavenCentral()
            maven { url = 'http://download.eclipse.org/gemini/mvn/' }

            maven { url = 'http://repository.springsource.com/maven/bundles/external' }
            maven { url = 'http://dist.wso2.org/maven2/' }
            maven { url = 'http://www.datanucleus.org/downloads/maven2/' }

            ivy { url = 'http://build.eclipse.org/rt/virgo/ivy/bundles/release' }
            ivy { url = 'http://build.eclipse.org/rt/virgo/ivy/bundles/milestone' }
            jcenter()
        }

        log.info("Initial setup ended...")
    }
}
