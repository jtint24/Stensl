import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DatumArray extends DatumObject {
    private ArrayList<Datum> value;
    private final String elementType;
    public DatumArray(String et, ArrayList<Datum> val) {
        elementType = et;
        value = val;
    }
    public DatumArray(String et, Datum[] val) {
        elementType = et;
        value =  new ArrayList<>(Arrays.asList(val));
    }

    /**
     * getElement
     *
     * Gets the Datum that lays at a particular element within the DatumArray
     *
     * @param i The index of the element to return
     * @return the Datum that lays at the index i
     * */

    public Datum getElement(int i) {
        if (i<value.size()) {
            return value.get(i);
        } else {
            ErrorManager.printError("Array index "+i+" out of bounds for length "+value.size()+"!","2:3.1");
        }
        return new Datum();
    }

    /**
     * setValueFrom
     *
     * Sets the value of the DatumArray to the value of another DatumArray, with safety checks
     *
     * @param dtm The DatumArray to set the value to
     * */

    @Override
    public void setValueFrom(Datum dtm) {
        if (dtm instanceof DatumArray) {
            if (super.getIsMutable()) {
                this.value = ((DatumArray) dtm).value;
            } else {
                ErrorManager.printError("Cannot mutate a constant!","2:2.2");
            }
        } else {
            ErrorManager.printError("Cannot set an array to a non-array!","2:2.3");
        }
    }

    /**
     * getProperty
     *
     * Gets the value of the property named by a particular property String
     *
     * @param str The String that defines the property to get
     * @return The property to get
     * */

    @Override
    public Datum getProperty(String str) {
        if (str.contains("]")) { //Checks if the property is an index (THIS WILL BREAK IF AN ARRAY PROPERTY IS INTRODUCED THAT RETURNS AN ARRAY)
            if (str.startsWith("[")) { //Cleans leading brackets
                str = str.substring(1);
            }
            String indexStr = Interpreter.splitByNakedChar(str, ']')[0]; //Gets only the first index for multidimensional indices
            Datum indexDatum = new Parser(indexStr).result(); //Parses the index string
            if (TypeChecker.isCompatible(indexDatum.getType(),"int")) {
                int indexInt = (int) Float.parseFloat(indexDatum.getValue()); //float->int is performed to avoid .0 error
                if (indexStr.length()>str.length()-2) { //If there are still indices/properties, then continue getting properties
                    return this.getElement(indexInt); //Gets the element
                } else {
                    return this.getElement(indexInt).getProperty(str.substring(indexStr.length()+1));
                }
            } else {
                ErrorManager.printError("Cannot index an array with a non-integer!","2:2.4");
            }
        }


        String propertyName = str.split("\\(")[0].trim();
        int propertyNameLength = propertyName.length();
        String argumentName = str.substring(propertyNameLength+1, str.length()-1);
        switch (propertyName) {
            case "length":
                return new Datum(""+value.size(), "int");
            case "add":
                value.add(new Parser(argumentName).result());
                return new Datum("","void");
            case "remove":
                String removeAtIndexStr = new Parser(argumentName).result().getValue();
                float removeAtIndex = 0;
                try {
                    removeAtIndex = Float.parseFloat(removeAtIndexStr);
                } catch (NumberFormatException nfe) {
                    ErrorManager.printError("Cannot index an array with a non-integer!","2:1.1");
                }
                if (removeAtIndex%1==0) {
                    value.remove((int)removeAtIndex);
                } else {
                    ErrorManager.printError("Cannot index an array with a non-integer!","2:1.1");
                }
                return new Datum("","void");
            case "rotate":
                String rotateIndexString = new Parser(argumentName).result().getValue();
                float rotateIndex = 0;
                try {
                    rotateIndex = Float.parseFloat(rotateIndexString);
                } catch (NumberFormatException nfe) {
                    ErrorManager.printError("Cannot index and array with a non-integer!","2:1.1");
                }
                if (rotateIndex%1==0) {
                    Collections.rotate(value, (int)rotateIndex);
                } else {
                    ErrorManager.printError("Cannot index an array with a non-integer!","2:1.1");
                }
                return new Datum("","void");

        }

        return new Datum();
    }

    /**
     * getType
     *
     * Gets the type of the DatumArray
     *
     * @return The type of the DatumArray as a String
     * */

    @Override
    public String getType() {
        return "["+elementType+"]";
    }

    /**
     * toString
     *
     * Returns a String representing the DatumArray's internal info
     *
     * @return A string listing the elements of the DatumArray as Strings
     * */

    @Override
    public String toString() {
        StringBuilder toStringResult = new StringBuilder("[");
        for (int i = 0; i<value.size(); i++) {
            toStringResult.append(value.get(i).toString());
            if (i<value.size()-1) {
                toStringResult.append(", ");
            }
        }
        toStringResult.append("]");
        return toStringResult.toString();
    }
}
