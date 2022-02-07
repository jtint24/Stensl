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

    @Override
    public String getType() {
        return elementType+"[]";
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
