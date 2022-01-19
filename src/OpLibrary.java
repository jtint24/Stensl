public class OpLibrary {
    private static String[] doubleFloat = {"float", "float"};
    private static String[] singleFloat = {"float"};
    private static String[] doubleInt = {"int", "int"};
    private static String[] singleInt = {"int"};

    private static OpFunction addFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public static Operation addition = new Operation(addFunction, doubleFloat, "float", OpPrecedence.ADDITIVE);
    private static OpFunction multFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "float");
    public static Operation multiplication = new Operation(multFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE);
    private static OpFunction subFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "float");
    public static Operation subtraction = new Operation(subFunction, doubleFloat, "float", OpPrecedence.ADDITIVE);
    private static OpFunction divFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!");
        }
        return new Datum(String.valueOf(Float.parseFloat(args[0].getValue())/Float.parseFloat(args[1].getValue())), "float");
    };
    public static Operation division = new Operation(divFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE);
    private static OpFunction passFunction = (args) -> args[0];
    public static Operation pass = new Operation(passFunction, singleFloat, "float", OpPrecedence.PASS);

    private static OpFunction intAddFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public static Operation intAddition = new Operation(intAddFunction, doubleInt, "int", OpPrecedence.ADDITIVE);
    private static OpFunction intMultFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "int");
    public static Operation intMultiplication = new Operation(intMultFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE);
    private static OpFunction intSubFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "int");
    public static Operation intSubtraction = new Operation(intSubFunction, doubleInt, "int", OpPrecedence.ADDITIVE);
    private static OpFunction intDivFunction = (args) -> {
        if (Integer.parseInt(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!");
        }
        return new Datum(String.valueOf(Integer.parseInt(args[0].getValue())/Integer.parseInt(args[1].getValue())), "int");
    };
    public static Operation intDivision = new Operation(intDivFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE);
    private static OpFunction intPassFunction = (args) -> args[0];
    public static Operation intPass = new Operation(intDivFunction, singleInt, "int", OpPrecedence.PASS);
}
