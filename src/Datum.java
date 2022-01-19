public class Datum {
    private String value;
    private String type;

    public Datum(String v, String t) {
        value = v;
        type = t;
    }

    public Datum() {}

    public String getValue() {
        return value;
    }

    public void setValue(String v) {
        value = v;
    }

    public String getType() {
        return type;
    }

    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Datum: "+value+" of type "+type);
    }
}
