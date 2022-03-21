import java.util.Arrays;

public class Datum implements Cloneable {
    private String value;
    protected String type;
    private boolean isMutable = true;
    private boolean isBlank = false;
    private String[] scope = {"public"};

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

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        if (isMutable) {
            value = v;
        } else {
            ErrorManager.printError("Cannot to mutate a constant!","1:2.1");
        }
    }

    public void setValueFrom(Datum dtm) {
        if (TypeChecker.isCompatible(dtm.getType(), this.getType())) {
            setValue(dtm.getValue());
        } else {
            ErrorManager.printError("Cannot assign a value of type '"+dtm.getType()+"' to a variable of type '"+this.getType()+"' !", "1:2.2");
        }
    }

    public Datum increment() {
        if (!isMutable) {
            ErrorManager.printError("Cannot increment immutable variable!","1:2.3");
        }
        if (this.getType().equals("int") || this.getType().equals("float")) {
            value = String.valueOf(Float.parseFloat(value)+1);
        } else {
            ErrorManager.printError("Cannot increment non-numerical type, '"+getType()+"' !","1:2.4");
        }
        return this;
    }

    public Datum getProperty(String str) {
        if (str.length() == 0) {
           // System.out.println(this+" is getting property with scope "+Arrays.asList(scope));
            if (scope.length == 0) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","1:2.5");
            }
            if (scope[0].equals("private")) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!", "1:2.5");
            }
            if (scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType())) {
                return this;
            }
            ErrorManager.printError("Cannot get a property from an out-of-scope area!","1:2.5");
        }
        ErrorManager.printError("Cannot call non-existent property '"+str+"' !","1:2.6");
        return new Datum();
    }
    public Datum getProperty(String[] str) {
        if (str.length == 0) {
            //System.out.println(this+" is getting property with scope "+Arrays.asList(scope));
            if (scope.length == 0) {
                if (isInScope()) {
                    return this;
                } else {
                    ErrorManager.printError("Cannot get a property from an out-of-scope area!","1:2.5");
                }
            }
            if (scope[0].equals("private")) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","1:2.5");
            }
            if (scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType())) {
                return this;
            }
            ErrorManager.printError("Cannot get a property from an out-of-scope area!","1:2.5");
        }
        ErrorManager.printError("Cannot call non-existent property '"+str[0]+"' !","1:2.5");
        return new Datum();
    }

    public String getType() {
        return type;
    }

    public String getScopeString() {
        return Arrays.toString(scope);
    }

    public Datum publicVersion() {
        Datum clonedVersion = this.clone();
        clonedVersion.setScope(new String[]{"public"});
        return clonedVersion;
    }

    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Datum: ");
        System.out.println(tab.repeat(i)+" "+value+" of type "+type);
    }

    protected boolean isInScope() {
        if (scope.length == 0) {
            return false;
        }
        return scope[0].equals("public") || Arrays.asList(scope).contains(Interpreter.getCurrentObject().getType());
    }

    public Datum clone() {
        return new Datum(value, type, isMutable);
    }

    public boolean getIsFunction() {
        return false;
    }

    public boolean getIsMutable() { return isMutable; }

    public boolean getIsBlank() { return isBlank; }

    public void setScope(String[] sc) {
        scope = sc;
    }

    public void setIsMutable(boolean m) {
        isMutable = m;
    }

    public void setType(String t) {
        type = t;
    }

    public String toString() {
        return value+" of type "+type;
    }

}
