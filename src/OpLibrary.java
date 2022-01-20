public class OpLibrary {
    private final static String[] doubleFloat = {"float", "float"};
    private final static String[] singleFloat = {"float"};
    private final static String[] doubleInt = {"int", "int"};
    private final static String[] singleInt = {"int"};
    private final static String[] singleString = {"string"};
    private final static String[] doubleString = {"string"};
    private final static String[] stringInt = {"string","int"};

    private final static OpFunction addFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public final static Operation addition = new Operation(addFunction, doubleFloat, "float", OpPrecedence.ADDITIVE, "addition");
    private final static OpFunction multFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "float");
    public final static Operation multiplication = new Operation(multFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE, "multiplication");
    private final static OpFunction subFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "float");
    public final static Operation subtraction = new Operation(subFunction, doubleFloat, "float", OpPrecedence.ADDITIVE, "subtraction");
    private final static OpFunction divFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!");
        }
        return new Datum(String.valueOf(Float.parseFloat(args[0].getValue())/Float.parseFloat(args[1].getValue())), "float");
    };
    public final static Operation division = new Operation(divFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE, "division");
    private final static OpFunction passFunction = (args) -> args[0];
    public final static Operation pass = new Operation(passFunction, singleFloat, "float", OpPrecedence.PASS, "pass");

    private final static OpFunction intAddFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public final static Operation intAddition = new Operation(intAddFunction, doubleInt, "int", OpPrecedence.ADDITIVE, "int add");
    private final static OpFunction intMultFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "int");
    public final static Operation intMultiplication = new Operation(intMultFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE, "int mult");
    private final static OpFunction intSubFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "int");
    public final static Operation intSubtraction = new Operation(intSubFunction, doubleInt, "int", OpPrecedence.ADDITIVE, "int sub");
    private final static OpFunction intDivFunction = (args) -> {
        if (Integer.parseInt(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!");
        }
        return new Datum(String.valueOf(Integer.parseInt(args[0].getValue())/Integer.parseInt(args[1].getValue())), "int");
    };
    public final static Operation intDivision = new Operation(intDivFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE, "int div");
    private final static OpFunction intPassFunction = (args) -> args[0];
    public final static Operation intPass = new Operation(intPassFunction, singleInt, "int", OpPrecedence.PASS, "int pass");

    private final static OpFunction stringPassFunction = (args) -> args[0];
    public final static Operation stringPass = new Operation(stringPassFunction, singleString, "string", OpPrecedence.PASS, "string pass");
    private final static OpFunction concatenationFunction = (args) -> new Datum(args[0].getValue()+args[1].getValue(), "string");
    public final static Operation concatenation = new Operation(concatenationFunction, doubleString, "string", OpPrecedence.ADDITIVE, "concatenation");

    private final static OpFunction charGetFunction = (args) -> new Datum(""+args[0].getValue().charAt(Integer.parseInt(args[1].getValue())), "char");
    public final static Operation charGet = new Operation(charGetFunction, stringInt, "char", OpPrecedence.MULTIPLICATIVE, "char get");
}
