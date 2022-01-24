import java.util.HashMap;

public class Interpreter {
    private static HashMap<String, Datum> memory = new HashMap<>();
    public static void runStensl(String[] code) {
        for (String line : code) {

            String firstToken = "";
            int charCount = 0;
            while (line.charAt(charCount)!=' ' && line.charAt(charCount)!='=') {
                firstToken+=line.charAt(charCount);
                charCount++;
            }

            if (memory.containsKey(firstToken)) {
                assignVar(line);
            } else {
                switch (firstToken) {
                    case "var":
                        initializeVar(line);
                        break;
                    default:
                        (new Parser(line)).getValue();
                        break;
                }
            }
        }
    }
    private static void initializeVar(String line) {
        String[] lineSplitByEqual = line.split("=");
        String[] lineSplitBySpace = lineSplitByEqual[0].split(" ");

        boolean isConst = lineSplitBySpace[1].equals("const");
        int flagOffset = isConst ? 1 : 0;

        String variableType = lineSplitBySpace[1+flagOffset];
        String variableName = lineSplitBySpace[2+flagOffset];
        Parser variableParser = (new Parser(lineSplitByEqual[1]));
        Datum variable = new Datum(variableType, !isConst);
        variable.setValueFrom(variableParser.result());
        memory.put(variableName, variable);
    }
    private static void assignVar(String line) {
        String[] lineSplitByEqual = line.split("=");
        String[] lineSplitBySpace = lineSplitByEqual[0].split(" ");
        String varName = lineSplitBySpace[0];
        memory.get(varName).setValueFrom(new Parser(lineSplitByEqual[1]).result());
    }
    public static HashMap<String, Datum> getMemory() {
        return memory;
    }
}
