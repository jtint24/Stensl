import java.util.ArrayList;
import java.util.HashMap;

public class DatumObject extends Datum {
    private HashMap<String, Datum> properties = new HashMap<>();

    public DatumObject(DatumClass cls) {
        super.type = cls.getClassName();
        HashMap<String, String> classProperties = cls.getProperties();
        for (HashMap.Entry<String, String> classProperty : classProperties.entrySet()) {
            String propertyName = classProperty.getKey();
            String propertyType = classProperty.getValue();
            properties.put(propertyName, cls.getDefaultVals().getOrDefault(propertyName, new Datum("",propertyType)));
        }
    }
    public DatumObject() {}
    @Override
    public String getValue() {
        return this.toString();
    }
    @Override
    public Datum getProperty(String property) {
        String[] propertyNames = splitByNakedChars(property, ".[");
        return getProperty(propertyNames);
    }
    @Override
    public Datum getProperty (String[] propertyNames) {
        if (propertyNames.length==0) {
            return this;
        }
        String cleanPropertyName = "";
        for (char c : propertyNames[0].toCharArray()) {
            if (c == '(') {
                break;
            }
            cleanPropertyName+=c;
        }
        if (properties.containsKey(cleanPropertyName)) {
            String[] newPropertyNames = new String[propertyNames.length-1];
            System.arraycopy(propertyNames, 1, newPropertyNames, 0, propertyNames.length - 1);
            return properties.get(cleanPropertyName).getProperty(newPropertyNames);
        } else {
            ErrorManager.printError("Can't find property '"+cleanPropertyName+"'!");
            return null;
        }
    }
    public HashMap<String, Datum> getProperties() {
        return properties;
    }
    private String[] splitByNakedChars(String s, String chars) {
        ArrayList<String> splitResults = new ArrayList<>();
        String currentSplit = "";
        int parenCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;
        int sLength = s.length();
        for (int i = 0; i<sLength; i++) {
            char sChar = s.charAt(i);
            if (sChar == '"') {
                inQuotes = ! inQuotes;
            }
            if (sChar == '(' && !inQuotes) {
                parenCount++;
            }
            if (sChar == ')' && !inQuotes) {
                parenCount--;
            }
            if (sChar == '[' && !inQuotes) {
                bracketCount++;
            }
            if (sChar == ']' && !inQuotes) {
                bracketCount--;
            }
            if (parenCount == 0 && bracketCount==0 && !inQuotes && chars.contains(""+sChar) || (bracketCount==1 && sChar == '[' && chars.contains("["))) {
                splitResults.add(currentSplit);
                currentSplit = "";
            } else {
                currentSplit+=sChar;
            }
        }
        splitResults.add(currentSplit);
        return splitResults.toArray(new String[0]);
    }
    public String toString() {
        String retString = "{";
        for (String propertyName : properties.keySet()) {
            retString+=properties.get(propertyName).getType();
            retString+=" "+propertyName;
            retString+=" = "+properties.get(propertyName).getValue();
            retString+=", ";
        }
        retString = retString.substring(0,retString.length()-2);
        return retString+"}";
    }
}
