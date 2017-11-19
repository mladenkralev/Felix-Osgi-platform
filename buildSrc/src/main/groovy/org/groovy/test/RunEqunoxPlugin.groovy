package org.groovy.test

import groovy.io.FileType
import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger

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
            }
        }.dependsOn("build")
    }

    List<File> getFilesInBinaryFolder(Project project) {
        String binaryFolderName = 'binary'

        File binaryFolder = new File("${project.rootDir}${File.separator}${binaryFolderName}")
        log.info("Binary folder found: $binaryFolder ")
        assert binaryFolder.exists() , "Binary folder doesn't exist!"

        def listOfFilesInBinaryFolder = []

        log.info("Binary folder files:")
        binaryFolder.eachFileRecurse(FileType.FILES) { file ->
            listOfFilesInBinaryFolder << file
            log.info("Added in list: $file ")
        }

        log.info("Items in folder: $listOfFilesInBinaryFolder.size")
        assert listOfFilesInBinaryFolder.size() == 11, "Missing files in binary directory"

        return listOfFilesInBinaryFolder
    }

    boolean copyToBuildDir(List<File> listOfFilesInBinaryFolder, Project project) {
        String destinationAsString = "${project.rootDir}" +
                                     "${File.separator}build" +
                                     "${File.separator}libs" +
                                     "${File.separator}container"

        Path destination = Paths.get(destinationAsString)

        listOfFilesInBinaryFolder.each {file ->
            Files.copy(file.toPath(), destination.resolve(file.getName()))
            log.info("File copied from $file.path to $destination")
        }
    }

    boolean createContainerFolder(Project project) {
        String folderDestinationAsString = "${project.rootDir}" +
                "${File.separator}build" +
                "${File.separator}libs" +
                "${File.separator}container"
        File destinationFolder = new File(folderDestinationAsString)

        assert !destinationFolder.exists() , "Destination folder is already created!"

        destinationFolder.mkdir()

        assert destinationFolder.exists() , "Destination folder wasn't created!"
    }
}