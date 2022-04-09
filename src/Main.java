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
                "func void duplicateArgFunc(int: a, int: b) {",
                "   a = 10",
                "   var bool myBool = true",
                "   myBool = false",
                "   println(myBool)",
                "   println(a+a+b)",
                "   return ()",
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
                "   println(str(imin)&\" is running\")",
                "   if (imin<imax) {",
                "       toRun(imin)",
                "       forLoop(imin+1, imax, toRun)",
                "   }",
                "   return ()",
                "}",
                "func void printI(int: i) {",
                "   println(i)",
                "   println(\"helo\")",
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
                "   var string withDialog = \"This guy said \"&instr&\" the first letter of which is \"&(instr$0)",
                "   return (withDialog)",
                "}",
                "println(addDialogTag | exclaimed | \"hello\")"
        };*/
        /*String[] stenslScript = {
                "var [int] myInt = [1,2,3]",
                "println(myInt[0])",
                "println(myInt[1])",
                "println(myInt[2])",
                "var [[int]] my2DIntArray = [[1,2],[2,3],[4,5]]",
                "println(my2DIntArray[1][1])",
                "myInt[0] = 0",
                "println(myInt[0])",
                "my2DIntArray[2][1] = 3",
                "println(my2DIntArray[2][1])",
                "myInt.add(5)",
                "println(myInt[3])",
                "println(myInt.length())",
                "myInt.remove(2)",
                "println(\"items removed\")",
                "for (0,myInt.length()-1) {(int i)",
                "   print(str(myInt[i])&\",\")",
                "}",
                "println(\"\")",
                "myInt.rotate(1)",
                "for (0,myInt.length()-1) {(int i)",
                "   print(str(myInt[i])&\",\")",
                "}",
                //"println(int(myInt[0][0]))"
        };*/
        /*String[] stenslScript = {
                "var [int] myInts = [0, 1, 2, 3, 4, 3, 2,1,0,9]",
                "for (0,9) { (int i)",
                "   println(myInts[i])",
                "}"
        };*/
        /*String[] stenslScript = {
                "var bool myBool = 5>=10",
                "println(\"myBool should be false and it is: \"&str(myBool))",
                "if (!myBool) {",
                "   println(\"Congrats!\")",
                "} else {",
                "   println(\"Ah shit\")",
                "}"
        };*/
        /*String[] stenslScript = {
                "5*10*trace(20/5+3)",
                "assert(5==5)",
                "var const string theWordHello = \"hello\"",
                "println(ascii(34))",
                "println(ascii(75))",
                "println(ascii(123))",
                "assert(theWordHello == \"hello\")",
                "assert(false)"
        };*/
        /*String[] stenslScript = {
        "var string quote = ascii(34)",
        "var string rightSquareBracket = ascii(93)",
        "var string commaSpace = ascii(44)&ascii(32)",
        "var [string] lines = [\"var string quote = ascii(34)\", \"var string rightSquareBracket = ascii(93)\", \"var string commaSpace = ascii(44)&ascii(32)\", \"var [string] lines = [\", \"for (0,2) {(int i)\", \"     println(lines[i])\", \"}\", \"print(lines[3])\", \"for (0, lines.length()-2 {(int i)\", \"     print(quote&lines[i]&quote&commaSpace)\",\"}\",\"println(quote&lines[14]&quote&rightSquareBracket)\",\"for (4, lines.length()-1) {(int i)\", \"     println(lines[i])\",\"}\"]",
        "for (0,2) {(int i)",
        "     println(lines[i])",
        "}",
        "print(lines[3])",
        "for (0, lines.length()-2) {(int i)",
        "     print(quote&lines[i]&quote&commaSpace)",
        "}",
        "println(quote&lines[14]&quote&rightSquareBracket)",
        "for (4, lines.length()-1) {(int i)",
        "     println(lines[i])",
        "}"};*/

        /*String[] stenslScript = {
                "class MyFirstClass {",
                "   var int val",
                "   var const private int defaultVal = 0",
                "   var const public string stringThing = \"hello there\"",
                "   var const MyFirstClass,MyFirstClass [string] messages = [\"hello\", \"there\"]",
                "   func void myMethod(int: param, string: secParam) {",
                "       println(\"my method ran! \"&secParam&str(param))",
                "       return ()",
                "   }",
                "   func MyFirstClass getCopy() {",
                "       var MyFirstClass thisCopy = this",
                "       return (this)",
                "   }",
                "   func int getSeven() {",
                "       return (this.defaultVal)",
                "   }",
                "   func void mySimpleMethod() {",
                "       println(\"simplemethod runs like a charm!\")",
                "       println(str(this.defaultVal)&\" is val\")",
                "       return ()",
                "   }",
                "   func [int] arrayMethod() {",
                "       return ([1,2,3])",
                "   }",
                "}",
                "var MyFirstClass myFirstObject = MyFirstClass()",
                "myFirstObject.myMethod(5, \"hello\")",
                "myFirstObject.mySimpleMethod()",
                "println(myFirstObject.getCopy().stringThing)",
                "println(myFirstObject.defaultVal)"
        };*/
        /*String[] stenslScript = {
                "class ErrorClass {",
                "   var int val = 0",
                "   func ErrorClass returnClassTest() {",
                "       var ErrorClass test = this",
                "       return (test)",
                "   }",
                "   var int val2 = 2",
                "}",
                "var ErrorClass errorObj = ErrorClass()",
                "println(errorObj.val)",
                "println(errorObj.returnClassTest().val)"
        };*/
        /*String[] stenslScript = {
                "println(typeof(5))",
                "println(typeof(5.0))",
                "println(typeof(\"5\"))",
                "println(typeof(true))",
                "func void someFunc(int: arg1, string: arg2) {",
                "   return ()",
                "}",
                "class SomeClass {",
                "   var int val = 5",
                "}",
                "println(typeof(SomeClass()))",
                "println(typeof(someFunc))"
        };*/
        /*String[] stenslScript = {
                "func int intReturningFunc() {",
                "   return (5)",
                "}",
                "class TestObj {",
                "   var int val = 0",
                "}",
                "func TestObj objReturningFunc() {",
                "   var TestObj tobj = TestObj()",
                "   return (tobj)",
                "}",
                "println(intReturningFunc())",
                "println(objReturningFunc())"
        };*/
        /*String[] stenslScript = {
                "class Bundle {",
                "   var int zeroInt = 0",
                "   var const private int privInt = 1",
                "   var const Bundle,Legal int reservedInt = 2",
                "   var public int pubInt = 3",
                "   var public Bundle subBundle",
                "   func int getZInt() {",
                "       return (zeroInt)",
                "   }",
                "   func public int getPrvInt() {",
                "       return (privInt)",
                "   }",
                "}",
                "class Legal {",
                "   func int getReservedInt() {",
                "       return (myBundle.reservedInt)",
                "   }",
                "}",
                "var Bundle myBundle = Bundle()",
                "var Legal myLegal = Legal()",
                "println(myBundle.zeroInt) //0",
                "println(myBundle.getPrvInt()) //1",
                "println(myLegal.getReservedInt()) //2",
                "println(myBundle.reservedInt)    // ERROR!"
        };*/
        /*String[] stenslScript = {
                "func float constantProb(float: x) {",
                "   return (.1)",
                "}",
                "println(constantProb(0))"
        };*/

        /*String[] stenslScript = {
                "func int myFunc() {",
                "   return (6)",
                "}",
                "func int myFunc2() {",
                "   return (7)",
                "}",
                "var ()->int myInt = myFunc",
                "myInt = myFunc2",
                "println(myInt())"
        };*/

        String[] stenslScript = {
                "func void forLoop(int imin, int imax) {",
                "   println(imin)",
                "   //println(imin+1)",
                "   if (imin>=imax) {",
                "       return",
                "   }",
                "   forLoop(imin+1, imax)",
                "   return",
                "}",
                "forLoop(1,5)",
                "func int returnExpression(float f) {",
                "   return int(f*2+2)%4",
                "}",
                "throw(\"Error!\")",
                "println(returnExpression(4.0))",
                "for 1+1 +1,  10*2 {(int k)",
                "   print(k)",
                "}"
        };

        /*String[] stenslScript = {
                "for (1,5) {(float i)",
                "   print(i)",
                "}",
                "func void errorFunc() {",
                "   println(0/0)",
                "}",
                "errorFunc()"
        };*/

        Interpreter.runStensl(stenslScript);
        //System.out.print(Interpreter.getFunctionList());
    }
}

