import java.util.Arrays;

public class Datum implements Cloneable {
    protected String value;
    protected String type;
    protected boolean isMutable = true;
    protected boolean isBlank = false;
    protected String[] scope = {"public"};

    public Datum(String v, String t) {
        value = v;
        type = t;
        scope = new String[]{"public"};
    }

    public Datum(String v, String t, boolean im) {
        value = v;
        type = t;
        isMutable = im;
        scope = new String[]{"public"};
    }

    public Datum(String v, String t, String[] sc) {
        value = v;
        type = t;
        scope = sc;
    }

    public Datum() {
        isBlank = true;
    }
    /**
     * getValue
     *
     * Gets the string representing the value of the Datum
     *
     * @return String containing value of the Datum
     * */
    public String getValue() {
        if (getType()!=null && value!=null) {
            if (getType().equals("int") && !value.isEmpty()) {
                value = String.valueOf((int) Float.parseFloat(value));
            }
        }
        return value;
    }

    /**
     * setValue
     *
     * Sets the value of the Datum to some string
     *
     * @param v The value to set to
     * */

    public void setValue(String v) {
        if (isMutable) {
            value = v;
        } else {
            ErrorManager.printError("Cannot mutate a constant!","1.1");
        }
    }

    /**
     * setValueFrom
     *
     * Sets the value of this Datum to the value of another Datum, with safety checks
     *
     * @param dtm The Datum to set the value from
     * */

    public void setValueFrom(Datum dtm) {
        if (TypeChecker.isCompatible(dtm.getType(), this.getType())) {
            setValue(dtm.getValue());
        } else {
            ErrorManager.printError("Cannot assign a value of type '"+dtm.getType()+"' to a variable of type '"+this.getType()+"' !", "1.2");
        }
    }

    /**
     * increment
     *
     * Increments the value of the Datum by 1, if its type is numeric. Contains safety checks for
     * mutability and type
     *
     * @return Itself, the incremented datum
     * */

    public Datum increment() {
        if (!isMutable) {
            ErrorManager.printError("Cannot increment immutable variable!","1.3");
        }
        if (this.getType().equals("int") || this.getType().equals("float")) {
            value = String.valueOf(Float.parseFloat(value)+1);
        } else {
            ErrorManager.printError("Cannot increment non-numerical type, '"+getType()+"' !","1.4");
        }
        return this;
    }

    /**
     * getProperty
     *
     * Gets the value of some property of the Datum
     *
     * @param str The string defining the property name (and possibly other following properties)
     * @return The property as a datum
     * */

    public Datum getProperty(String str) {
        if (str.length() == 0) {
           // System.out.println(this+" is getting property with scope "+Arrays.asList(scope));
            if (scope.length == 0) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","1.5");
            }
            if (scope[0].equals("private")) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!", "1.6");
            }
            if (scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType())) {
                return this;
            }
            ErrorManager.printError("Cannot get a property from an out-of-scope area!","1.7");
        }
        ErrorManager.printError("Cannot call non-existent property '"+str+"' !","1.8");
        return new Datum();
    }

    /**
     * getProperty
     *
     * Gets the value of some property of the Datum
     *
     * @param str The array of strings defining the properties to get from the Datum
     * @return The property as a datum
     * */

    public Datum getProperty(String[] str) {
        if (str.length == 0) {
            //System.out.println(this+" is getting property with scope "+Arrays.asList(scope));
            if (scope.length == 0) {
                if (isInScope()) {
                    return this;
                } else {
                    ErrorManager.printError("Cannot get a property from an out-of-scope area!","1.9");
                }
            }
            if (scope[0].equals("private")) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","1.10");
            }
            if (scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType())) {
                return this;
            }
            ErrorManager.printError("Cannot get a property from an out-of-scope area!","1.11");
        }
        ErrorManager.printError("Cannot call non-existent property '"+str[0]+"' !","1.12");
        return new Datum();
    }

    /**
     * getType
     *
     * Gets the type of the Datum
     *
     * @return String representing the type
     * */

    public String getType() {
        return type;
    }

    /**
     * getScopeString
     *
     * Fets the scope of the Datum
     *
     * @return The scope of the Datum
     * */

    public String getScopeString() {
        return Arrays.toString(scope);
    }

    /**
     * publicVersion
     *
     * Creates a new version of the Datum which is public
     *
     * @return Cloned version of the Datum with a public scope
     * */

    public Datum publicVersion() {
        Datum clonedVersion = this.clone();
        clonedVersion.setScope(new String[]{"public"});
        return clonedVersion;
    }

    /**
     * toConsole
     *
     * Prints value of the datum to console, called from Parser's toConsole
     *
     * @param i The number of indentation levels to print with
     * */

    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Datum: ");
        System.out.println(tab.repeat(i)+" "+value+" of type "+type);
    }

    /**
     * isInScope
     *
     * Returns whether the Datum is available from current scope
     *
     * @return Whether Datum is in scope
     * */

    protected boolean isInScope() {
        if (scope.length == 0) {
            return false;
        }
        return scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType());
    }

    /**
     * clone
     *
     * Returns dereferenced copy of the Datum
     *
     * @return Dereferenced copy
     * */
    @Override
    public Datum clone() {
        return new Datum(value, type, isMutable);
    }

    /**
     * getIsFunction
     *
     * Returns if this Datum is a function (is always false here but is overridden in other classes)
     *
     * @return Whether it's a function: false
     * */

    public boolean getIsFunction() {
        return false;
    }

    /**
     * getIsMutable
     *
     * Gets whether the Datum is mutable
     *
     * @return The isMutable value
     * */

    public boolean getIsMutable() { return isMutable; }

    /**
     * getIsBlank
     *
     * Gets whether the Datum is blank
     *
     * @return The isBlank value
     * */

    public boolean getIsBlank() { return isBlank; }

    /**
     * setScope
     *
     * Sets the scope of the Datum
     *
     * @param sc The scope of the variable as a string array
     * */

    public void setScope(String[] sc) {
        scope = sc;
    }

    /**
     * setIsMutable
     *
     * Sets the mutability of the Datum
     *
     * @param m Whether the Datum is mutable as a boolean
     * */

    public void setIsMutable(boolean m) {
        isMutable = m;
    }

    /**
     * setType
     *
     * Sets the type of the Datum as a String
     *
     * @param t The type to set the Datum to as a String
     * */

    public void setType(String t) {
        type = t;
    }

    /**
     * toString
     *
     * Returns a String representing the Datum's internal info
     *
     * @return a String containing the Datum's value and type
     * */
    @Override
    public String toString() {
        return value+" of type "+type;
    }
}
