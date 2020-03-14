import java.nio.file.Path
import java.util.concurrent.TimeUnit

public class ExecuteBatchUtil {
    private static final long TIMEOUT = 10;

    public static void invokeContainer(Path executableBatch) {
        invokeContainer(executableBatch, TIMEOUT)
    }

    public static int invokeContainer(Path executableBatch, long timeout) {
        Path directory = executableBatch.getParent();
        List<String> args = new ArrayList<String>();
        args.add(directory.toAbsolutePath().toString() + File.separator + executableBatch.getFileName().toString());
        args.each { println( it )}

        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(directory.toFile())
        Process p = pb.start();
        p.waitFor(timeout == 0 ? TIMEOUT: timeout, TimeUnit.SECONDS)
        return p.exitValue()
    }
}
