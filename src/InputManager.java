import java.time.Duration;
import java.time.Instant;

public class InputManager {
    public static long startTime;
    public static void main(String[] args) {
        startTime = System.nanoTime();
        String[] codeLines = args[0].split("\n");
        Interpreter.runStensl(codeLines);
        //System.out.println("FINISHED IN: "+(System.nanoTime()-startTime)/1000000000.0+" secs");
    }
}
