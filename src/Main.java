public class Main {
    public static void main(String[] args) {
        Parser strToParserTest = new Parser("!false");
        strToParserTest.toConsole();
        System.out.println(strToParserTest.result().getValue());

        /*OpFunction concatFunc = (arguments) -> new Datum(arguments[0].getValue()+arguments[1].getValue(), "string");
        String[] operandTypes = {"string", "string"};
        Operation concat = new Operation(concatFunc, 2, operandTypes);
        Datum[] operands = {new Datum("12", "string"), new Datum("32", "string")};
        Parser concatTest = new Parser(concat, operands);
        System.out.println(concatTest.result().getValue());

        OpFunction addFunc = (arguments) -> new Datum(String.valueOf(Float.parseFloat(arguments[0].getValue())+Float.parseFloat(arguments[1].getValue())), "float");
        String[] operandTypesFloat = {"float", "float"};
        Operation floatAdd = new Operation(addFunc, 2, operandTypesFloat);
        Datum[] addOperands = {new Datum("123","float"), new Datum("234","float")};
        Parser floatAddTest = new Parser(floatAdd, addOperands);
        System.out.println(floatAddTest.result().getValue());

        Datum[] mlOperands = {floatAddTest, concatTest};
        Parser multiLevelTest = new Parser(floatAdd, mlOperands);
        System.out.println(multiLevelTest.result().getValue());*/
    }
}
