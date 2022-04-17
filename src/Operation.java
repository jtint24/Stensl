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

    /**
     * result
     *
     * Gets the result of the operation when applied to some arguments
     *
     * @param arguments The arguments to apply
     * @return The result of the operator
     * */

    public Datum result(Datum[] arguments) {
        for (int i = 0; i<operandNum; i++) {
            if (!TypeChecker.isCompatible(arguments[i].getType(), operandTypes[i])) {
                ErrorManager.printError("Given type '"+arguments[i].getType()+"' does not match expected type '"+operandTypes[i]+"' in argument "+(i+1)+" of operation: '"+name+"' !","11.1");
            }
        }
        return functionality.result(arguments);
    }

    /**
     * getReturnType
     *
     * Gets the return type of the function
     *
     * @return the value of returnType
     * */

    public String getReturnType() {
        return returnType;
    }

    /**
     * getName
     *
     * Returns that name of the operation with a null safety check
     *
     * @return The value of the name variable with a null default value
     * */

    public String getName() {
        return name==null ? "no name" : name;
    }

    /**
     * getFullName
     *
     * Returns the full name of the operator
     *
     * @return The value of the fullName variable
     * */

    public String getFullName() { return fullName; }

    /**
     * toString
     *
     * Gets the name of the operator to use to name the object as a String
     *
     * @return The value of the name variable
     * */

    public String toString() { return name; }

    /**
     * getType
     *
     * Returns the complete type of the operator
     *
     * @return The type of the operator as a String
     * */

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

    /**
     * getIsFunction
     *
     * Gets whether the object is a function. This is always true here, but it overrides a method from
     * the Datum class which is always true.
     *
     * @return A false value
     * */

    @Override
    public boolean getIsFunction() {
        return true;
    }
}
