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

    /**
     * getValue
     *
     * Gets the value of the DatumObjet
     *
     * @return The value of the DatumObject, a String of all its properties
     * */

    @Override
    public String getValue() {
        return this.toString();
    }

    /**
     * getProperty
     *
     * Returns a Datum representing a property
     *
     * @param property The String of the property chain
     * @return A Datum representing the result of the property
     * */

    @Override
    public Datum getProperty(String property) {
        String[] propertyNames = splitByNakedChars(property, ".[");
        return getProperty(propertyNames);
    }

    /**
     * getProperty
     *
     * Returns a Datum representing a property
     *
     * @param propertyNames The String of the property chain
     * @return A Datum representing the result of the property
     * */

    @Override
    public Datum getProperty (String[] propertyNames) {

        if (propertyNames.length==0) {
            if (super.isInScope()) {
                return this;
            } else {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","4:2.1");
            }
        }
        //System.out.println("getting properties from "+ this +": "+propertyNames[0]);
        StringBuilder cleanPropertyName = new StringBuilder();
        boolean isFunction = false;
        for (char c : propertyNames[0].toCharArray()) {
            if (c == '(') {
                isFunction = true;
                break;
            }
            cleanPropertyName.append(c);
        }
        if (properties.containsKey(cleanPropertyName.toString())) {
            String[] newPropertyNames = new String[propertyNames.length-1];
            System.arraycopy(propertyNames, 1, newPropertyNames, 0, propertyNames.length - 1);
            if (isFunction) {
                Datum functionToCall = properties.get(cleanPropertyName.toString());

                if (!functionToCall.isInScope()) {
                    ErrorManager.printError("Cannot get a property from an out-of-scope area!","4:2.1");
                }
                if (!(functionToCall instanceof Function)) {
                    ErrorManager.printError("Cannot call non-function '"+cleanPropertyName+"'!","4:2.2");
                    return new Datum();
                }
                if (propertyNames[0].startsWith(cleanPropertyName+"()")) {
                    Datum resultDatum = ((Function)functionToCall).result(new Datum[0]);
                    return resultDatum.getProperty(newPropertyNames);
                }

                String argumentList = propertyNames[0].substring(cleanPropertyName.length()+1, propertyNames[0].length()-1);
                String[] argumentNames = Interpreter.splitByNakedChar(argumentList, ',');
                Datum[] arguments = new Datum[argumentNames.length];
                for (int i = 0; i< arguments.length; i++) {
                    arguments[i] = new Parser(argumentNames[i]).result();
                }
                return ((Function) functionToCall).result(arguments).getProperty(newPropertyNames);
            }
            if (!properties.get(cleanPropertyName.toString()).isInScope()) {
                ErrorManager.printError("Cannot get a property from an out-of-scope area!","4:2.1");
            }
            return properties.get(cleanPropertyName.toString()).getProperty(newPropertyNames);
        } else {
            ErrorManager.printError("Cannot call property '"+cleanPropertyName+"'!","4:2.3");
            return null;
        }
    }

    /**
     * setValueFrom
     *
     * Sets the value of the DatumObject to the value of a given DatumObject, with safety checks
     *
     * @param dtm The Datum to set the value from
     * */

    @Override
    public void setValueFrom(Datum dtm) {
        if (dtm instanceof DatumObject) {
            if (super.getIsMutable()) {
                this.properties = ((DatumObject) dtm).getProperties();
            } else {
                ErrorManager.printError("Cannot mutate a constant!","4:2.4");
            }
        } else {
            ErrorManager.printError("Cannot set an object to a non-object!","4:2.5");
        }
    }

    /**
     * getProperties
     *
     * Gets the HashMap of properties and their values
     *
     * @return The properties variable
     * */

    public HashMap<String, Datum> getProperties() {
        return properties;
    }

    /**
     * splitByNakedChars
     *
     * Splits a String by multiple characters, so long as those chars are not inside parentheses, quotes,
     * or brackets.
     *
     * @param s The String to split
     * @param chars A string containing all of the characters to split by
     * @return An array of Strings forming the split results of the original String, s.
     * */

    private String[] splitByNakedChars(String s, String chars) {
        ArrayList<String> splitResults = new ArrayList<>();
        StringBuilder currentSplit = new StringBuilder();
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
                splitResults.add(currentSplit.toString());
                currentSplit = new StringBuilder();
            } else {
                currentSplit.append(sChar);
            }
        }
        splitResults.add(currentSplit.toString());
        return splitResults.toArray(new String[0]);
    }

    /**
     * toString
     *
     * Returns a String containing all of this DatumObject's properties
     *
     * @return a String with all of the Datum's properties and their values listed
     * */
    public String toString() {
        StringBuilder retString = new StringBuilder("{");
        for (String propertyName : properties.keySet()) {
            if (propertyName.equals("this")) {
                continue;
            }
            retString.append(properties.get(propertyName).getType());
            retString.append(" ").append(propertyName);
            retString.append(" = ").append(properties.get(propertyName).getValue());
            retString.append(", ");
        }
        retString = new StringBuilder(retString.substring(0, retString.length() - 2));
        return retString+"}";
    }

    @Override
    public Datum publicVersion() {
        DatumObject pubObj = new DatumObject();
        pubObj.properties = this.properties;
        pubObj.type = this.type;
        pubObj.value = this.value;
        pubObj.scope = new String[]{"public"};
        pubObj.isMutable = this.isMutable;
        pubObj.isBlank = this.isBlank;
        return pubObj;
    }
}
