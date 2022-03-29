import java.time.Duration;
import java.time.Instant;

public class InputManager {
    public static long startTime;
    /**
     * main
     *
     * Runs the Stensl interpreter with code from a file
     *
     * @param args The arguments that the input manager was called with from the command line
     * */
    public static void main(String[] args) {
        startTime = System.nanoTime();
        String[] codeLines = args[0].split("\n");
        Interpreter.runStensl(codeLines);
        //System.out.println("FINISHED IN: "+(System.nanoTime()-startTime)/1000000000.0+" secs");
    }
}
