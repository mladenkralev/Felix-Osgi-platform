package org.osgi.container

import org.gradle.api.Project

import java.nio.file.Path
import java.nio.file.Paths

class ExecutableCreator {
    static void createExecutableOsgiContainer(Path exeDestinationFolder, Path osgiCoreBundlePath) {
        File batFile = createBatFile(exeDestinationFolder, "start.bat")
        batFile.setText("java -jar $osgiCoreBundlePath -console -configuration configuration".toString())
    }

    static void createExecutableP2Provision(Path exeDestinationFolder, Path osgiLauncherPath) {
        File batFile = createBatFile(exeDestinationFolder, "p2Provision.bat")
        batFile.setText("@echo off\n" +
                "\n" +
                "for /f \"tokens=1-3*\" %%a in (\"%*\") do (\n" +
                "    set par1=%%a\n" +
                "    set par2=%%b\n" +
                ")\n" +
                "\n" +
                "echo the script is %0\n" +
                "echo Source of bundles %par1%\n" +
                "echo Artifactory and Metadata source is %par2%\n" +
                "\n" +
                "java -jar $osgiLauncherPath " +
                "-application org.eclipse.equinox.p2.publisher.FeaturesAndBundlesPublisher " +
                "-artifactRepository %par2% " +
                "-metadataRepository %par2% " +
                "-source %par1% -publishArtifacts"
        )
    }

    static void createExecutableDirector(Path exeDestinationFolder, Path osgiLauncherPath) {
        File batFile = createBatFile(exeDestinationFolder, "p2Director.bat")
        batFile.setText("@echo off\n" +
                "\n" +
                "for /f \"tokens=1-3*\" %%a in (\"%*\") do (\n" +
                "    set par1=%%a\n" +
                "    set par2=%%b\n" +
                "\tset par3=%%c\n" +
                ")\n" +
                "\n" +
                "echo the script is %0\n" +
                "echo Installable unit is %par1%\n" +
                "echo Source repository is %par2%\n" +
                "echo Destination is %par2%\n" +
                "\n" +
                "java -jar $osgiLauncherPath " +
                "-application org.eclipse.equinox.p2.director " +
                "-installIU %par1% " +
                "-repository %par2% " +
                "-destination %par3%")
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
