public class Operation {
    private int operandNum;
    private OpFunction functionality;
    private String[] operandTypes;
    private String returnType;

    public Operation(OpFunction of, String[] ots, String rt) {
        functionality = of;
        operandNum = ots.length;
        operandTypes = ots;
        returnType = rt;
    }

    public Datum result(Datum[] arguments) {
        for (int i = 0; i<operandNum; i++) {
            if (!arguments[i].getType().equals(operandTypes[i])) {
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1));
            }
        }
        return functionality.result(arguments);
    }

    public String getReturnType() {
        return returnType;
    }
}
