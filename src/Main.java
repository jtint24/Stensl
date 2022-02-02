public class Main {
    public static void main(String[] args) {
        /*String[] stenslScript = {
                "func firstClassFunc() {",
                "   println(\"the func ran!\")",
                "   return",
                "}",
                "func firstClassFuncWithArg(int: arg) {",
                "   println(arg)",
                "   return",
                "}",
                "firstClassFuncWithArg(5)",
                "var int myInt = 0",
                "myInt = 5",
                "firstClassFunc()",
                "var ()->void funcTest = firstClassFunc",
                "funcTest()",
                "var ()->void secondFuncTest = funcTest",
                "secondFuncTest()",
                "firstClassFuncWithArg(5)",
                "var (int)->void thirdFuncTest = firstClassFuncWithArg",
                "thirdFuncTest(10+int(\"5\"&\"7\"))"};*/
        /*String[] stenslScript = {
                "func recursiveFunc(int: arg) {",
                "   println(arg)",
                "   recursiveFunc(arg+1)",
                "   return",
                "}",
                "recursiveFunc(0)"};*/
        /*String[] stenslScript = {
                "func runInfinitely(()->void: toRun) {",
                "   toRun()",
                "   runInfinitely(toRun)",
                "   return",
                "}",
                "func printHi() {",
                "   println(\"hi\")",
                "   return",
                "}",
                "runInfinitely | printHi"
        };*/
        String[] stenslScript =  {
                "",
                "func duplicateArgFunc(int: a, int: b) {",
                "   a = 10",
                "   var bool myBool = true",
                "   myBool = false",
                "   println(myBool)",
                "   println(a+a)",
                "   return",
                "}",
                "duplicateArgFunc(5,10)",
        };
        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}

