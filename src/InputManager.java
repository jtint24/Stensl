import java.time.Duration;
import java.time.Instant;

public class InputManager {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        String[] codeLines = args[0].split("\n");
        Interpreter.runStensl(codeLines);
        System.out.println("FINISHED IN: "+(System.currentTimeMillis()-startTime)/1000.0+" secs");
    }
}
