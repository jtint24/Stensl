public class Operation {
    private String name;
    private int operandNum;
    private OpFunction functionality;
    private String[] operandTypes;
    private String returnType;
    private OpPrecedence precedence;

    public Operation(OpFunction of, String[] ots, String rt, OpPrecedence pr) {
        functionality = of;
        operandNum = ots.length;
        operandTypes = ots;
        returnType = rt;
        precedence = pr;
    }
    public Operation(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm) {
        functionality = of;
        operandNum = ots.length;
        operandTypes = ots;
        returnType = rt;
        precedence = pr;
        name = nm;
    }

    public Datum result(Datum[] arguments) {
        for (int i = 0; i<operandNum; i++) {
            if (!arguments[i].getType().equals(operandTypes[i]) && !(arguments[i].getType().equals("int") && operandTypes[i].equals("float")) && !(arguments[i].getType().equals("char") && operandTypes[i].equals("string"))) {
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1));
            }
        }
        return functionality.result(arguments);
    }

    public String getReturnType() {
        return returnType;
    }

    public String getName() {
        return name;
    }
}
