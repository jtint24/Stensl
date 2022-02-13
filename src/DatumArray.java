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
        return value.get(i);
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
