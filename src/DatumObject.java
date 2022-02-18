import java.util.ArrayList;
import java.util.HashMap;

public class DatumObject extends Datum {
    private HashMap<String, Datum> properties;

    public DatumObject(DatumClass cls) {
        super.type = cls.getClassName();
        HashMap<String, String> classProperties = cls.getProperties();
        for (HashMap.Entry<String, String> classProperty : classProperties.entrySet()) {
            String propertyName = classProperty.getKey();
            String propertyType = classProperty.getValue();
            properties.put(propertyName, new Datum("", propertyType));
        }
    }
    public DatumObject() {}

    @Override
    public Datum getProperty(String property) {
        String[] propertyNames = splitByNakedChar(property, '.');
        return getProperty(propertyNames);
    }
    @Override
    public Datum getProperty (String[] propertyNames) {
        String cleanPropertyName = "";
        String stopChars = "([";
        for (char c : propertyNames[0].toCharArray()) {
            if (stopChars.contains(c+"")) {
                break;
            }
            cleanPropertyName+=c;
        }
        if (!properties.containsKey(cleanPropertyName)) {
            ErrorManager.printError("Classes of type "+super.type+" do not have a property "+propertyNames[0]+"!");
        }
        Datum propertyResult = properties.get(cleanPropertyName);
        if (propertyNames.length>1) {
            String[] newPropertyNames = new String[propertyNames.length-1];
            System.arraycopy(propertyNames, 1, newPropertyNames, 0, propertyNames.length - 1);
            propertyResult = propertyResult.getProperty(newPropertyNames);
        }
        return propertyResult;
    }
    private String[] splitByNakedChar(String s, char c) {
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
            if (parenCount == 0 && bracketCount==0 && !inQuotes && sChar == c) {
                splitResults.add(currentSplit);
                currentSplit = "";
            } else {
                currentSplit+=sChar;
            }
        }
        splitResults.add(currentSplit);
        return splitResults.toArray(new String[0]);
    }

}
