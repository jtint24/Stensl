public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"testing the function\")",
                "func printTwice (string: bla) {",
                "println(bla)",
                "println(bla)",
                "return",
                "}",
                "println(\"function testing...\")",
                "printTwice(\"hello\")",
                "println(\"test completed!\")"};
        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}
