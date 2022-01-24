public class Datum implements Cloneable {
    private String value;
    private String type;
    private boolean isMutable = true;

    public Datum(String v, String t) {
        value = v;
        type = t;
    }

    public Datum(String v, String t, boolean m) {
        value = v;
        type = t;
        isMutable = m;
    }

    public Datum() {}

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
        if (dtm.getType().equals(this.getType())) {
            setValue(dtm.getValue());
        } else {
            ErrorManager.printError("Type mismatch! Cannot assign a value of type "+dtm.getType()+" to a variable of type "+this.getType()+"!");
        }
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
}
