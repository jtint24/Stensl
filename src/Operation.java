public class Operation extends Datum {
    protected String name;
    protected int operandNum;
    private OpFunction functionality;
    protected String[] operandTypes;
    protected String returnType;
    private OpPrecedence precedence;
    protected String fullName;

    public Operation() {}
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
    public Operation(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm, String fn) {
        functionality = of;
        operandNum = ots.length;
        operandTypes = ots;
        returnType = rt;
        precedence = pr;
        name = nm;
        fullName = fn;
    }

    public Datum result(Datum[] arguments) {
        for (int i = 0; i<operandNum; i++) {
            if (!arguments[i].getType().equals(operandTypes[i]) && !(operandTypes[i].equals("any")) && !(arguments[i].getType().equals("int") && operandTypes[i].equals("float")) && !(arguments[i].getType().equals("char") && operandTypes[i].equals("string"))) {
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1)+" of operation: "+name);
            }
        }
        return functionality.result(arguments);
    }

    public String getReturnType() {
        return returnType;
    }

    public String getName() {
        return name==null ? "no name" : name;
    }

    public String getFullName() { return fullName; }

    public String toString() {return name;}

    public String getType() {
        String type = "(";
        for (String inputType : operandTypes) {
            type+=inputType+",";
        }
        type+=")->";
        type+=returnType;
        return type;
    }

    @Override
    public boolean getIsFunction() {
        return true;
    }
}
