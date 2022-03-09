import java.util.ArrayList;
import java.util.HashMap;

public class DatumObject extends Datum {
    private HashMap<String, Datum> properties = new HashMap<>();

    public DatumObject(DatumClass cls) {
        super.type = cls.getClassName();
        HashMap<String, String> classProperties = cls.getProperties();
        HashMap<String, String[]> classPropertyScopes = cls.getPropertiesScope();
        for (HashMap.Entry<String, String> classProperty : classProperties.entrySet()) {
            String propertyName = classProperty.getKey();
            String propertyType = classProperty.getValue();
            String[] propertyScope = classPropertyScopes.get(propertyName);
            properties.put(propertyName, cls.getDefaultVals().getOrDefault(propertyName, new Datum("",propertyType,propertyScope)));
        }
        for (Datum property : properties.values()) {
            if (property instanceof Function) {
                ((Function) property).setAssociatedObject(this);
            }
        }
        properties.put("this", this);
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
            if (super.isInScope()) {
                return this;
            } else {
                ErrorManager.printError("Attempt to get a property from an out-of-scope area!");
            }
        }
        //System.out.println("getting properties from "+ this +": "+propertyNames[0]);
        String cleanPropertyName = "";
        boolean isFunction = false;
        for (char c : propertyNames[0].toCharArray()) {
            if (c == '(') {
                isFunction = true;
                break;
            }
            cleanPropertyName+=c;
        }
        if (properties.containsKey(cleanPropertyName)) {
            String[] newPropertyNames = new String[propertyNames.length-1];
            System.arraycopy(propertyNames, 1, newPropertyNames, 0, propertyNames.length - 1);
            if (isFunction) {
                Datum functionToCall = properties.get(cleanPropertyName);

                if (!functionToCall.isInScope()) {
                    ErrorManager.printError("Attempt to get a property from an out-of-scope area!");
                }
                if (!(functionToCall instanceof Function)) {
                    ErrorManager.printError("Can't call non-function '"+cleanPropertyName+"'!");
                    return new Datum();
                }
                if (propertyNames[0].startsWith(cleanPropertyName+"()")) {
                    return ((Function)functionToCall).result(new Datum[0]).getProperty(newPropertyNames);
                }

                String argumentList = propertyNames[0].substring(cleanPropertyName.length()+1, propertyNames[0].length()-1);
                String[] argumentNames = Interpreter.splitByNakedChar(argumentList, ',');
                Datum[] arguments = new Datum[argumentNames.length];
                for (int i = 0; i< arguments.length; i++) {
                    arguments[i] = new Parser(argumentNames[i]).result();
                }
                return ((Function) functionToCall).result(arguments).getProperty(newPropertyNames);
            }
            if (!properties.get(cleanPropertyName).isInScope()) {
                ErrorManager.printError("Attempt to get a property from an out-of-scope area!");
            }
            return properties.get(cleanPropertyName).getProperty(newPropertyNames);
        } else {
            ErrorManager.printError("Can't find property '"+cleanPropertyName+"'!");
            return null;
        }
    }
    @Override
    public void setValueFrom(Datum dtm) {
        if (dtm instanceof DatumObject) {
            if (super.getIsMutable()) {
                this.properties = ((DatumObject) dtm).getProperties();
            } else {
                ErrorManager.printError("Attempt to mutate a constant!");
            }
        } else {
            ErrorManager.printError("Type mistmatch!");
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
            if (propertyName.equals("this")) {
                continue;
            }
            retString+=properties.get(propertyName).getType();
            retString+=" "+propertyName;
            retString+=" = "+properties.get(propertyName).getValue();
            retString+=", ";
        }
        retString = retString.substring(0,retString.length()-2);
        return retString+"}";
    }
}
