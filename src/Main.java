public class Main {
    public static void main(String[] args) {
       /* String[] stenslScript = {
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
                "func printBye() {",
                "   println(\"bye\")",
                "   return",
                "}",
                "var ()->void alsoPrintHi = printHi",
                "alsoPrintHi = printBye",
                "printHi()",
                "alsoPrintHi()"
        };*/
        /*String[] stenslScript =  {
                "",
                "func duplicateArgFunc(int: a, int: b) {",
                "   a = 10",
                "   var bool myBool = true",
                "   myBool = false",
                "   println(myBool)",
                "   println(a+a+b)",
                "   return",
                "}",
                "duplicateArgFunc(5,10)",
        };*/
        /*String[] stenslScript = {
                "",
                "if (false) {",
                "   println(\"this shouldn't run\"",
                "}",
                "if (true) {",
                "   println(\"this should run\")",
                "}"
        };*/

        /*String[] stenslScript = {
                "func void forLoop(int: imin, int: imax, (int)->void: toRun) {",
                "   if (imin<imax) {",
                "       toRun(imin)",
                "       forLoop(imin+1, imax, toRun)",
                "   }",
                "   return ()",
                "}",
                "func void printI(int: i) {",
                "   println(i)",
                "   return ()",
                "}",
                "forLoop(0, 5, printI)",
                "if (5<4) {",
                "   println(\":(\")",
                "} else {",
                "   println(\":)\")",
                "}",
                "",
                "for (1,3) { (int index)",
                "   for (1,3) { (int index2)",
                "       println(index+index2)",
                "   }",
                "}",
                "func int addTwo(int: input) {",
                "   println(input)",
                "   return (input+2)",
                "}",
                "println(addTwo(7))"
        };*/

        /*String[] stenslScript = {
                "func string exclaimed(string: instr) {",
                "   var string withExclamationPoint = instr&\"!\"",
                "   return (withExclamationPoint&\" \"&withExclamationPoint)",
                "}",
                "func string addDialogTag(string: instr) {",
                "   var string withDialog = \"This guy said \"&instr&\" the first word of which is \"&(instr$0)",
                "   return (withDialog)",
                "}",
                "println(addDialogTag | exclaimed | \"hello\")"
        };*/
        System.out.println(TypeChecker.isCompatible("[int]", "[any]"));

        String[] stenslScript = {
                "var [int] myInt = [1,2,3]",
                "println(myInt[0])",
                "println(myInt[1])",
                "println(myInt[2])",
                "var [[int]] my2DIntArray = [[1,2],[2,3],[4,5]]",
                "println(my2DIntArray[1][1])",
                "myInt[0] = 0",
                "println(myInt[0])",
                "my2DIntArray[2][1] = 3",
                "println(my2DIntArray[2][1])"
                //"println(int(myInt[0][0]))"
        };


        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}

