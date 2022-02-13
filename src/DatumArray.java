import java.util.ArrayList;
import java.util.Arrays;

public class DatumArray extends Datum {
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
        return;
    }

    @Override
    public Datum getProperty(String str) {
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
