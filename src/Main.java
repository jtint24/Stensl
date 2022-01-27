public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"testing functions\")",
                "func myFunction {",
                "println(\"my function ran!\")",
                "myFunction()",
                "return",
                "}",
                "myFunction(\"test complete!\")",
                "println(\"test complete!\")"};
        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}
