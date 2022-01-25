public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"testing running over bracketed code\")",
                "func myFunction() {",
                "{",
                "}",
                "}",
                "println(\"test complete!\")"};
        Interpreter.runStensl(stenslScript);
    }
}
