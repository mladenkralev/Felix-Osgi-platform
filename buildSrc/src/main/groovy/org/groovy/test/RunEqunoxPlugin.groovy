package org.groovy.test

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Created by mladen on 11/18/2017.
 */
/*
   What happens behind the curtain is that the abstract syntax tree (AST) is analysed and transformed at compile time.
   With the groovy.util.logging.Slf4j annotation we declare a dependency on a SLF4J logger instance which is
   then available through the log variable.
 */
@Slf4j
class RunEqunoxPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.task('startContainer') {
            doLast {
                List<File> listOfFilesInBinaryFolder  = getFilesInBinaryFolder(project)

                createContainerFolder(project)

                copyToBuildDir(listOfFilesInBinaryFolder, project)

                createBatScript(project)
            }
        }.dependsOn("build")
    }

    List<File> getFilesInBinaryFolder(Project project) {
        Path containerFolderPath = Paths.get("" +
                "${project.rootDir}" +
                "${File.separator}binary")

        File binaryFolder = new File(containerFolderPath.toString())

        log.info("Binary folder found: $binaryFolder ")
        assert binaryFolder.exists() , "Binary folder doesn't exist!"

        log.info("Binary folder files:")

        def listOfFilesInBinaryFolder = []
        binaryFolder.eachFileRecurse(FileType.FILES) { file ->
            listOfFilesInBinaryFolder << file
            log.info("Added in list: $file ")
        }

        log.info("Items in folder: $listOfFilesInBinaryFolder.size")
        assert listOfFilesInBinaryFolder.size() == 11, "Missing files in binary directory"

        return listOfFilesInBinaryFolder
    }

    boolean copyToBuildDir(List<File> listOfFilesInBinaryFolder, Project project) {
        Path containerFolderPath = Paths.get("${project.rootDir}" +
                "${File.separator}build" +
                "${File.separator}libs" +
                "${File.separator}container")

        listOfFilesInBinaryFolder.each {file ->
            Files.copy(file.toPath(), containerFolderPath.resolve(file.getName()))
            log.info("File copied from $file.path to $containerFolderPath")
        }
    }

    boolean createContainerFolder(Project project) {
        Path containerFolderPath = Paths.get("${project.rootDir}" +
                "${File.separator}build" +
                "${File.separator}libs" +
                "${File.separator}container")

        File destinationFolder = new File(containerFolderPath.toString())

        assert !destinationFolder.exists() , "Destination folder is already created!"

        destinationFolder.mkdir()

        assert destinationFolder.exists() , "Destination folder wasn't created!"
    }

    boolean createBatScript(Project project) {
        Path folderRuntimePath = Paths.get(
                "${project.rootDir}" +
                "${File.separator}build" +
                "${File.separator}runtime")

        File directoryBatFile = new File(folderRuntimePath.toString())
        assert !directoryBatFile.exists() , "$directoryBatFile already exists!"

        directoryBatFile.mkdir()
        assert directoryBatFile.exists() , "$directoryBatFile dir wasn't created!"

        Path batFilePath = Paths.get(
                "${project.rootDir}" +
                "${File.separator}build" +
                "${File.separator}runtime" +
                "${File.separator}start.bat")

        File batFile = new File(batFilePath.toString())
        batFile.createNewFile()
        assert batFile.exists() , "$batFile wasn't created!"
    }
}