package org.osgi.configurations

import groovy.util.logging.Slf4j
import org.gradle.api.Project

@Slf4j
class DefaultRepositoryHandler {

    public DefaultRepositoryHandler(Project project) {
        log.info("Defining repositories...")

        project.repositories {
            mavenCentral()
            maven { url = 'http://download.eclipse.org/gemini/mvn/' }

            maven { url = 'http://repository.springsource.com/maven/bundles/external' }
            maven { url = 'http://dist.wso2.org/maven2/' }
            maven { url = 'http://www.datanucleus.org/downloads/maven2/' }
            maven { url = 'http://www.jabylon.org/maven/' }

            ivy { url = 'http://build.eclipse.org/rt/virgo/ivy/bundles/release' }
            ivy { url = 'http://build.eclipse.org/rt/virgo/ivy/bundles/milestone' }
            jcenter()
        }

        log.debug("Defined repositories are: $project.getRepositories()")
    }
}
