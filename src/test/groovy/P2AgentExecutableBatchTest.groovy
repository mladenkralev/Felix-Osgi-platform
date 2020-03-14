import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.logging.Logger;

public class P2AgentExecutableBatchTest extends GroovyTestCase {
    Logger logger = Logger.getLogger(P2AgentExecutableBatchTest.class.toString())
    Path tempDirectoryFile
    Path rootDir
    Path buildFolder
    String buildFolderAsString
    String uriOfTempDirectory

    @Override
    protected void setUp() throws Exception {
        tempDirectoryFile = Files.createTempDirectory(System.currentTimeMillis().toString())
        rootDir = Paths.get(System.getProperty("user.dir"));

        buildFolderAsString = rootDir.toAbsolutePath().toString() + File.separator + "build" + File.separator + "p2Agent"
        buildFolder = Paths.get(buildFolderAsString);
        assert buildFolder != null

        uriOfTempDirectory = tempDirectoryFile.toUri().toString()
    }

    @Override
    protected void tearDown() throws Exception {
        tempDirectoryFile.deleteDir()
    }

    public void testP2Provision() {
        invokeP2Provision(buildFolderAsString, uriOfTempDirectory, buildFolder)

        if(tempDirectoryFile.toFile().isDirectory() && tempDirectoryFile.toFile().list().length == 0) {
            logger.info("Directory $tempDirectoryFile is empty");
            assert false
        } else {
            // Expected to be non-empty. Happy case
            logger.info("Directory $tempDirectoryFile is not empty");
            assert true
        }
    }

    public void testP2Director() {
        invokeP2Provision(buildFolderAsString, uriOfTempDirectory, buildFolder)
        String destinationFolder = uriOfTempDirectory + File.separator + "profile"

        List<String> args = new ArrayList<String>();
        args.add(buildFolderAsString + File.separator + "p2Director.bat"); // command name
        args.add("org.apache.felix.gogo.runtime"); // IU for install
        args.add(uriOfTempDirectory); // source
        args.add(destinationFolder); // destination

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(buildFolder.toFile())
        Process p = pb.start();
        p.waitFor(10, TimeUnit.SECONDS)

        File profilesDir = new File(destinationFolder);
        if(profilesDir.isDirectory() && profilesDir.list().length == 0) {
            logger.info("Directory $profilesDir is empty");
            assert false
        } else {
            // Expected to be non-empty. Happy case
            logger.info("Directory $profilesDir is not empty");
            assert true
        }
    }

    private void invokeP2Provision(String buildFolderAsString, String uriOfTempDirectory, Path buildFolder) {
        List<String> args = new ArrayList<String>();
        args.add(buildFolderAsString + File.separator + "p2Provision.bat"); // command name
        args.add(buildFolderAsString); // -source parameter
        args.add(uriOfTempDirectory); // url of repository

        args.each { println( it )}

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(buildFolder.toFile())
        Process p = pb.start();
        p.waitFor(10, TimeUnit.SECONDS)
    }
}
