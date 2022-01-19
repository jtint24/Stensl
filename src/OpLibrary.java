public class OpLibrary {
    private static String[] doubleFloat = {"float", "float"};
    private static String[] singleFloat = {"float"};
    private static OpFunction addFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public static Operation addition = new Operation(addFunction, doubleFloat, "float");
    private static OpFunction multFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "float");
    public static Operation multiplication = new Operation(multFunction, doubleFloat, "float");
    private static OpFunction subFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "float");
    public static Operation subtraction = new Operation(subFunction, doubleFloat, "float");
    private static OpFunction divFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!");
        }
        return new Datum(String.valueOf(Float.parseFloat(args[0].getValue())/Float.parseFloat(args[1].getValue())), "float");
    };
    public static Operation division = new Operation(divFunction, doubleFloat, "float");
    private static OpFunction passFunction = (args) -> args[0];
    public static Operation pass = new Operation(passFunction, singleFloat, "float");
}
