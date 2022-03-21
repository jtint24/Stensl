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
            if (!TypeChecker.isCompatible(arguments[i].getType(), operandTypes[i])) {
                ErrorManager.printError("Given type '"+arguments[i].getType()+"' does not match expected type '"+operandTypes[i]+"' in argument "+(i+1)+" of operation: '"+name+"' !","11:2.1");
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

    public String toString() { return name; }

    public String getType() {
        StringBuilder type = new StringBuilder("(");
        for (String inputType : operandTypes) {
            type.append(inputType).append(",");
        }
        if (type.charAt(type.length()-1) == ',') {
            type = new StringBuilder(type.substring(0, type.length() - 1));
        }
        type.append(")->");
        type.append(returnType);
        return type.toString();
    }

    @Override
    public boolean getIsFunction() {
        return true;
    }
}
