import java.util.HashMap;

public class DatumClass {
    private HashMap<String, String> properties;
    private HashMap<String, Datum> defaultVals;
    private String className;
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

    public void setDefaultVals(HashMap<String, Datum> defaultVals) {
        this.defaultVals = defaultVals;
    }
    @Override
    public String toString() {
        String retString = "{";
        for (String propertyName : properties.keySet()) {
            retString+=properties.get(propertyName);
            retString+=" "+propertyName;
            if (defaultVals.containsKey(propertyName)) {
                retString+=" = "+defaultVals.get(propertyName).getValue();
            }
            retString+=", ";
        }
        retString = retString.substring(0,retString.length()-2);
        return retString+"}";
    }
}
