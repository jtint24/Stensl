import java.util.Stack;

public class ErrorManager {
    public static void printError(String str) {
        System.out.println("Error on line "+Interpreter.getLineNumber()+"! Error details: "+str);
        System.out.println("Problematic line:\t\""+Interpreter.getCurrentLine()+"\"");
        System.out.println("Stack Trace:");
        System.out.println("\tFrom line number "+Interpreter.getLineNumber());
        Stack<Integer> stackTrace = Interpreter.getLineNumberStack();
        for (int lineLocation : stackTrace) {
            System.out.println("\tFrom line number "+lineLocation);
        }
        System.exit(0);
    }
}
