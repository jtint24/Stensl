public class Function extends Operation implements Cloneable {
    private final int lineNumberLocation;
    private final String[] parameterNames;
    private DatumObject associatedObject = new DatumObject();

    public Function (String[] ots, String[] pn, String rt, String nm, String fn, int ln) {
        super((args) -> args[0], ots, rt, OpPrecedence.FUNCTIONAL, nm, fn);
        parameterNames = pn;
        super.name = nm;
        lineNumberLocation = ln;
    }

    @Override
    public Datum result(Datum[] arguments) {

        for (int i = 0; i<operandNum; i++) {
            if (!TypeChecker.isCompatible(arguments[i].getType(), operandTypes[i])) {
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1)+" of operation: "+name,"6:2.1");
            }
        }
        return Interpreter.runFunction(this, arguments, parameterNames);
    }

    public void regenerateFullName() {
        StringBuilder newFullName = new StringBuilder(name + "(");
        for (String operandType : operandTypes) {
            newFullName.append(operandType).append(",");
        }
        if (newFullName.charAt(newFullName.length()-1) == ',') {
            newFullName = new StringBuilder(newFullName.substring(0, newFullName.length() - 1));
        }
        newFullName.append(")");
        super.fullName = newFullName.toString();
    }

    @Override
    public String getName() {
        return name==null ? "no name" : name;
    }

    public void setAssociatedObject(DatumObject dtmObj) {
        associatedObject = dtmObj;
    }
    public DatumObject getAssociatedObject() {
        return associatedObject;
    }
    public int getLineNumberLocation() { return lineNumberLocation; }

    public void setName(String n) {
        name = n;
    }

    public Function clone() {
        return new Function (super.operandTypes, parameterNames, super.returnType, super.name, super.fullName, lineNumberLocation);
    }

    public String toString() {
        return fullName+" of type "+getType();
    }
}
