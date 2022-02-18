import java.util.HashMap;

public class DatumClass {
    private HashMap<String, String> properties;
    private String className;
    public DatumClass(String name, HashMap<String,String> props) {
        className = name;
        properties = props;
    }

    public String getClassName() {
        return className;
    }
    public HashMap<String, String> getProperties() {
        return properties;
    }
}
