public class Function extends Operation implements Cloneable {
    private int lineNumberLocation;
    private String[] parameterNames;
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
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1)+" of operation: "+name);
            }
        }
        return Interpreter.runFunction(this, arguments, parameterNames);
    }

    public void regenerateFullName() {
        String newFullName = name+"(";
        for (String operandType : operandTypes) {
            newFullName+=operandType+",";
        }
        if (newFullName.charAt(newFullName.length()-1) == ',') {
            newFullName = newFullName.substring(0, newFullName.length() - 1);
        }
        newFullName+=")";
        super.fullName = newFullName;
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
