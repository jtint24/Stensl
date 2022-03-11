import java.util.HashMap;

public class DatumClass {
    private final HashMap<String, String> properties;
    private HashMap<String, String[]> propertiesScope = new HashMap<>();
    private HashMap<String, Datum> defaultVals;
    private final String className;
    public DatumClass(String name, HashMap<String,String> props) {
        className = name;
        properties = props;
    }
    public HashMap<String, Datum> getDefaultVals() {
        return defaultVals;
    }
    public String getClassName() {
        return className;
    }
    public HashMap<String, String> getProperties() {
        return properties;
    }

    public HashMap<String, String[]> getPropertiesScope() {
        return propertiesScope;
    }

    public void setDefaultVals(HashMap<String, Datum> defaultVals) {
        this.defaultVals = defaultVals;
    }
    public void setPropertiesScope(HashMap<String, String[]> ps) {
        propertiesScope = ps;
    }
    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder("{");
        for (String propertyName : properties.keySet()) {
            retString.append(properties.get(propertyName));
            retString.append(" ").append(propertyName);
            if (defaultVals.containsKey(propertyName)) {
                retString.append(" = ").append(defaultVals.get(propertyName).getValue());
            }
            retString.append(", ");
        }
        retString = new StringBuilder(retString.substring(0, retString.length() - 2));
        return retString+"}";
    }
}
