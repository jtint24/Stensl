public class Datum<T> {
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
}
