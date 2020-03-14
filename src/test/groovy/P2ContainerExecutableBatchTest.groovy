import org.junit.Test

import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

public class P2ContainerExecutableBatchTest extends GroovyTestCase {
    Logger logger = Logger.getLogger(P2ContainerExecutableBatchTest.class.toString())
    Path rootDir
    Path containerExecutable
    String buildFolderAsString

    @Override
    protected void setUp() throws Exception {
        rootDir = Paths.get(System.getProperty("user.dir"));
        containerExecutable = Paths.get(rootDir.toString(), "build", "p2Container", "start.bat");
        assert containerExecutable != null
    }

    @Test
    public void testContainer() {
        assert ExecuteBatchUtil.invokeContainer(containerExecutable) != 0 , "Container executable returned non-zero status"
    }
}
