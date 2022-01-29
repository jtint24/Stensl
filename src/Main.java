public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"testing the function\")",
                "var string tester = \"hello\"",
                "tester=\"mello\"",
                "println(\"hopefully this says mello: \"&tester)",
                "func printTwice (string: bla) {",
                "println(bla)",
                "bla = \"mello\"",
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
