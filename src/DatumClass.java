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

    /**
     * getDefaultVals
     *
     * Gets the default values for the Class
     *
     * @return The defaultVals variable
     * */

    public HashMap<String, Datum> getDefaultVals() {
        return defaultVals;
    }

    /**
     * getClassName
     *
     * Gets the class name
     *
     * @return The className variable
     * */

    public String getClassName() {
        return className;
    }

    /**
     * getProperties
     *
     * Gets the HashMap of properties and their type
     *
     * @return The properties variable
     * */

    public HashMap<String, String> getProperties() {
        return properties;
    }

    /**
     * getPropertiesScope
     *
     * Gets the HashMap of property names and their scopes
     *
     * @return The propertiesScope variable
     * */

    public HashMap<String, String[]> getPropertiesScope() {
        return propertiesScope;
    }

    /**
     * setDefaultVals
     *
     * Sets the default values of the Class
     *
     * @param defaultVals The default values as a HashMap of their names and associated Datums
     * */

    public void setDefaultVals(HashMap<String, Datum> defaultVals) {
        this.defaultVals = defaultVals;
    }

    /**
     * setPropertiesScope
     *
     * Sets the scopes of the properties
     *
     * @param ps The scopes for the variables as a HashMap of their names and scopes as an array
     * */

    public void setPropertiesScope(HashMap<String, String[]> ps) {
        propertiesScope = ps;
    }

    /**
     * toString
     *
     * Returns a String of all the info of the DatumClass
     *
     * @return String of all the properties of the class and default values, if a default value exists
     * */

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
