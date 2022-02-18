public class Datum implements Cloneable {
    private String value;
    protected String type;
    private boolean isMutable = true;
    private boolean isBlank = false;

    public Datum(String t, boolean im) {
        type = t;
        isMutable = im;
    }
    public Datum(String v, String t) {
        value = v;
        type = t;
    }

    public Datum(String v, String t, boolean im) {
        value = v;
        type = t;
        isMutable = im;
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
            ErrorManager.printError("Attempt to mutate a constant!");
        }
    }

    public void setValueFrom(Datum dtm) {
        if (TypeChecker.isCompatible(dtm.getType(), this.getType())) {
            setValue(dtm.getValue());
        } else {
            ErrorManager.printError("Type mismatch! Cannot assign a value of type "+dtm.getType()+" to a variable of type "+this.getType()+"!");
        }
    }

    public Datum increment() {
        if (!isMutable) {
            ErrorManager.printError("Cannot increment immutable variable!");
        }
        if (this.getType().equals("int") || this.getType().equals("float")) {
            value = String.valueOf(Float.parseFloat(value)+1);
        } else {
            ErrorManager.printError("Cannot increment non-numerical type, "+getType()+"!");
        }
        return this;
    }

    public Datum getProperty(String str) {
        ErrorManager.printError("Attempt to call non-existent property "+str+"!");
        return new Datum();
    }
    public Datum getProperty(String[] str) {
        ErrorManager.printError("Attempt to call non-existent property "+str+"!");
        return new Datum();
    }

    public String getType() {
        return type;
    }

    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Datum: ");
        System.out.println(tab.repeat(i)+" "+value+" of type "+type);
    }

    public Datum clone() {
        return new Datum(value, type, isMutable);
    }

    public boolean getIsFunction() {
        return false;
    }

    public boolean getIsMutable() { return isMutable; }

    public boolean getIsBlank() { return isBlank; }

    public void setIsMutable(boolean m) {
        isMutable = m;
    }

    public String toString() {
        return value+" of type "+type;
    }

}
