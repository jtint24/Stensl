public class InputManager {
    public static void main(String[] args) {
        String[] codeLines = args[0].split("\n");
        Interpreter.runStensl(codeLines);
    }
}
