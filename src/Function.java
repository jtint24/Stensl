import java.util.Arrays;

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

    /**
     * result
     *
     * Returns the result of this function
     *
     * @param arguments the array of arguments for the function, in the order that they're taken
     * @return a Datum with the value of the function
     * */
    @Override
    public Datum result(Datum[] arguments) {
        for (int i = 0; i<operandNum; i++) {
            if (!TypeChecker.isCompatible(arguments[i].getType(), operandTypes[i])) {
                ErrorManager.printError("Cannot match given type '"+arguments[i].getType()+"' to expected type '"+operandTypes[i]+"' in argument "+(i+1)+" of operation: '"+name+"' !","6:2.1");
            }
        }
        return Interpreter.runFunction(this, arguments, parameterNames);
    }

    /**
     * regenerateFullName
     *
     * Generates the full name of the function from the function's short name and its arguments
     * */

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

    /**
     * getName
     *
     * Returns the name of the function.
     *
     * @return the value of the variable name with a null safety check
     * */

    @Override
    public String getName() {
        return name==null ? "no name" : name;
    }

    /**
     * setAssociatedObject
     *
     * Sets the value of the associatedObject field
     *
     * @param dtmObj The object to associate this function with
     * */

    public void setAssociatedObject(DatumObject dtmObj) {
        associatedObject = dtmObj;
    }

    /**
     * getAssociatedObject
     *
     * Gets the associated DatumObject
     *
     * @return The value of the associatedObject variable
     * */

    public DatumObject getAssociatedObject() {
        return associatedObject;
    }

    /**
     * getLineNumberLocation
     *
     * Returns the line number on which this function's definition is located
     *
     * @return The value of the lineNumberLocation variable
     * */

    public int getLineNumberLocation() { return lineNumberLocation; }

    /**
     * setName
     *
     * Sets the short name of the function to a String
     *
     * @param n The new short name of the function
     * */

    public void setName(String n) {
        name = n;
    }

    /**
     * clone
     *
     * Returns a dereferenced copy of the Function
     *
     * @return The cloned Function
     * */

    public Function clone() {
        return new Function (super.operandTypes, parameterNames, super.returnType, super.name, super.fullName, lineNumberLocation);
    }

    /**
     * toString
     *
     * Returns a String with the information of the Function
     *
     * @return The Function's full name and type in a String
     * */

    public String toString() {
        return fullName+" of type "+getType();
    }
}
