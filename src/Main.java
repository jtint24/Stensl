public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "println(\"testing the function\")",
                "var string tester = \"hello\"",
                "tester=\"mello\" //blah blah",
                "println(\"hopefully this says mello: \"&tester)",
                "func printTwice (string: firstThing, string: secondThing) {",
                "   println(firstThing)",
                "   println(secondThing)",
                "   println(tester) //this is a comment",
                "   return",
                "}",
                "/* this is a valid comment */",
                "/* this should",
                "also be a valid comment*/",
                "println(\"function testing...\")",
                "printTwice(\"hello\", \"blonge\")",
                "println(\"test completed!\")"};
        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}
/*
* var myVar: String = "string"
* string myVar = string
* func funcName(arg: int) -> int {}
* func int funcName(int arg) {}
* var string mVar = "string"
* var myVar = "string"
* return
* return()
*
* */
