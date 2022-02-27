import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DatumArray extends DatumObject {
    private ArrayList<Datum> value;
    private String elementType;
    public DatumArray(String et, ArrayList<Datum> val) {
        elementType = et;
        value = val;
    }
    public DatumArray(String et, Datum[] val) {
        elementType = et;
        value =  new ArrayList<>(Arrays.asList(val));
    }

    public Datum getElement(int i) {
        if (i<value.size()) {
            return value.get(i);
        } else {
            ErrorManager.printError("Array index "+i+" out of bounds for length "+value.size()+"!");
        }
        return new Datum();
    }

    public void setElement(Datum setTo, int... indices) {
        if (indices.length == 1) {
            Datum mutatedElement = value.get(indices[0]);
            mutatedElement.setValueFrom(setTo);
            value.set(indices[0], mutatedElement);
        } else {
            if (value.get(indices[0]) instanceof DatumArray) {
                ((DatumArray)value.get(indices[0])).setElement(setTo, Arrays.copyOfRange(indices,1,indices.length));
            } else {
                ErrorManager.printError("Attempt to set an array element of a non-array!");
            }
        }
    }

    @Override
    public void setValueFrom(Datum dtm) {
        if (dtm instanceof DatumArray) {
            if (super.getIsMutable()) {
                this.value = ((DatumArray) dtm).value;
            } else {
                ErrorManager.printError("Attempt to mutate a constant!");
            }
        } else {
            ErrorManager.printError("Type mismatch!");
        }
    }

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
                ErrorManager.printError("Syntax error on array index!");
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
                    ErrorManager.printError("Can't index and array with a non-integer!");
                }
                if (removeAtIndex%1==0) {
                    value.remove((int)removeAtIndex);
                } else {
                    ErrorManager.printError("Can't index an array with a non-integer!");
                }
                return new Datum("","void");
            case "rotate":
                String rotateIndexString = new Parser(argumentName).result().getValue();
                float rotateIndex = 0;
                try {
                    rotateIndex = Float.parseFloat(rotateIndexString);
                } catch (NumberFormatException nfe) {
                    ErrorManager.printError("Can't index and array with a non-integer!");
                }
                if (rotateIndex%1==0) {
                    Collections.rotate(value, (int)rotateIndex);
                } else {
                    ErrorManager.printError("Can't index an array with a non-integer!");
                }
                return new Datum("","void");

        }

        return new Datum();
    }

    @Override
    public String getType() {
        return "["+elementType+"]";
    }

    @Override
    public String toString() {
        String toStringResult = "[";
        for (int i = 0; i<value.size(); i++) {
            toStringResult+=value.get(i).toString();
            if (i<value.size()-1) {
                toStringResult+=", ";
            }
        }
        toStringResult+="]";
        return toStringResult;
    }
}
