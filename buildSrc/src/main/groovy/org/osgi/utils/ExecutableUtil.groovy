package org.osgi.utils

import groovy.text.SimpleTemplateEngine
import groovy.util.logging.Slf4j

import java.nio.file.Path
import java.nio.file.Paths

@Slf4j
class ExecutableUtil {
    ExecutableUtil() {
        throw new IllegalArgumentException("ExecutableCreator is a utility class")
    }

    static void createExecutableOsgiContainer(Path exeDestinationFolder, String osgiCoreBundlePath) {
        File batFile = createBatFile(exeDestinationFolder, "start.bat")
        batFile.setText("java -jar $osgiCoreBundlePath -console -configuration configuration".toString())
    }

    static void createExecutableP2Provision(String exeDestinationFolder, String osgiLauncherPath) {
        String template = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("templates/p2Provision.bat").getText()

        def binding = ["launcherPath": "$osgiLauncherPath"]
        def result = resolveTemplate(template, binding)

        log.info("Creating file: {}", exeDestinationFolder)
        File batFile = createBatFile(Paths.get(exeDestinationFolder), "p2Provision.bat")

        batFile.setText(result.toString())
    }

    static void createExecutableDirector(String exeDestinationFolder, String osgiLauncherPath) {
        String template = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("templates/p2Director.bat").getText()

        def binding = ["launcherPath": "$osgiLauncherPath"]
        def result = resolveTemplate(template, binding)

        log.info("Creating file: {}", exeDestinationFolder)
        File batFile = createBatFile(Paths.get(exeDestinationFolder), "p2Director.bat")

        batFile.setText(result.toString())
    }

    private static def resolveTemplate(String template, Map binding) {
        def engine = new SimpleTemplateEngine()
        return engine.createTemplate(template).make(binding)
    }

    private static File createBatFile(Path exeDestinationFolder, String nameOfFile) {
        File directoryBatFile = new File(exeDestinationFolder.toString())
        directoryBatFile.mkdir()

        Path batFilePath = Paths.get("$exeDestinationFolder${File.separator}$nameOfFile")
        File batFile = new File(batFilePath.toString())

        batFile.createNewFile()
        assert batFile.exists(), "$batFile wasn't created!"
        return batFile
    }
}
