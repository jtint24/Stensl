import java.util.HashMap;

public class Interpreter {
    private static HashMap<String, Datum> memory = new HashMap<>();
    public static void runStensl(String[] code) {
        for (String line : code) {
            String firstToken = line.split(" ")[0];
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
        String[] lineSplitBySpace = line.split(" ");
        boolean isConst = lineSplitBySpace[1].equals("const");
        int flagOffset = isConst ? 1 : 0;
        if (!lineSplitBySpace[3+flagOffset].equals("=")) {
            ErrorManager.printError("syntax error on variable declaration!");
        }
        String variableType = lineSplitBySpace[1+flagOffset];
        String variableName = lineSplitBySpace[2+flagOffset];
        String variableValue = (new Parser(lineSplitBySpace[4+flagOffset])).getValue();
        memory.put(variableName, new Datum(variableValue, variableType, !isConst));
    }
    private static void assignVar(String line) {
        String[] lineSplitBySpace = line.split(" ");
        String varName = lineSplitBySpace[0];
        if (!lineSplitBySpace[1].equals("=")) {
            ErrorManager.printError("syntax error on variable declaration!");
        }
        memory.get(varName).setValueFrom(new Parser(lineSplitBySpace[2]).result());
    }
    public static HashMap<String, Datum> getMemory() {
        return memory;
    }
}
