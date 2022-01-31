public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "func firstClassFunc() {",
                "   println(\"the func ran!\")",
                "   return",
                "}",
                "var int myInt = 0",
                "myInt = 5",
                "var ()->void funcTest = firstClassFunc",
                "funcTest()"};
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
