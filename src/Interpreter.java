import javax.swing.plaf.synth.SynthButtonUI;
import java.util.HashMap;

public class Interpreter {
    private static int lineNumber;
    private static int functionCallLevel;
    private static String currentFunction;
    private static String[] codeLines;
    private static HashMap<String, Datum> memory = new HashMap<>();
    public static void runStensl(String[] code) {
        codeLines = code;
        functionCallLevel = 0;
        currentFunction = "";
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) {
            String line = codeLines[lineNumber-1];
            //System.out.println(" EXECUTING LINE "+ lineNumber+" WHICH IS "+line);

            String firstToken = "";
            int charCount = 0;
            while (line.charAt(charCount)!=' ' && line.charAt(charCount)!='=') {
                firstToken+=line.charAt(charCount);
                charCount++;
                if (charCount == line.length()) {
                    break;
                }
            }

            if (memory.containsKey(firstToken)) {
                assignVar(line);
            } else {
                switch (firstToken) {
                    case "var":
                        initializeVar(line);
                        break;
                    case "func":
                        moveOverBracketedCode();
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
    public static void runFunction() {

    }
    private static void moveOverBracketedCode() {
        int bracketCount = 0;
        lineNumber--;
        do  {
            lineNumber++;
            String currentLine = codeLines[lineNumber-1];
            if (currentLine.contains("{")) {
                bracketCount++;
            }
            if (currentLine.contains("}")) {
                bracketCount--;
            }
        } while (bracketCount!=0);
        //lineNumber++;
    }
    public static HashMap<String, Datum> getMemory() {
        return memory;
    }
    public static int getLineNumber() {
        return lineNumber;
    }
}
