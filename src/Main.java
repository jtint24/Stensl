public class Main {
    public static void main(String[] args) {
        String[] stenslScript = {
                "func firstClassFunc() {",
                "   println(\"the func ran!\")",
                "   return",
                "}",
                "func firstClassFuncWithArg(int: arg) {",
                "   println(arg)",
                "   return",
                "}",
                "var int myInt = 0",
                "myInt = 5",
                "firstClassFunc()",
                "var ()->void funcTest = firstClassFunc",
                "funcTest()",
                "var ()->void secondFuncTest = firstClassFunc",
                "secondFuncTest()",
                "firstClassFuncWithArg(5)",
                "var (int)->void thirdFuncTest = firstClassFuncWithArg",
                "thirdFuncTest(10)"};
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
