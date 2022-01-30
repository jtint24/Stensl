import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter {
    private static int lineNumber;
    private static Function currentFunction;
    private static String[] codeLines;
    private static HashMap<String, Datum> memory = new HashMap<>();
    private static HashMap<String, Function> functionList = new HashMap<>();
    private static ArrayList<String> functionShortNameList = new ArrayList<>();
    private static Stack<Integer> lineNumberStack = new Stack<>();
    private static Stack<HashMap<String, Datum>> localMemory = new Stack<>();
    public static void runStensl(String[] code) {
        boolean insideInlineComment = false;
        boolean insideBlockComment = false;
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //trim out whitespace and comments from the line;
            String line = code[lineNumber-1];
            line = line.trim();
            String cleanedLine = "";
            int lineLength = line.length();
            for (int i = 0; i<lineLength; i++) {
                char readChar = line.charAt(i);
                if (i<lineLength-1) {
                    if (readChar == '/' && line.charAt(i+1) == '/') { //check for inline // comments
                        insideInlineComment = true;
                    }
                    if (readChar == '/' && line.charAt(i+1) == '*') { //check for opening /* comments
                        insideBlockComment = true;
                    }
                    if (readChar == '*' && line.charAt(i+1) == '/') { // check for closing */ comments
                        if (insideBlockComment) {
                            insideBlockComment = false;
                            i++;
                            continue;
                        } else {
                            ErrorManager.printError("Block comment terminator */ used without opening a block comment!");
                        }
                    }
                }
                if (!insideInlineComment && !insideBlockComment) {
                    cleanedLine+=readChar;
                }
            }
            code[lineNumber-1] = cleanedLine.trim();
            insideInlineComment = false;
        }
        if (insideBlockComment) {
            ErrorManager.printError("Unterminated block comment!");
        }

        codeLines = code;
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Checks for function headers and adds them to functionList
            String line = codeLines[lineNumber-1];

            if (line.startsWith("func ")) {
                String[] headerWords = line.split("\\(")[0].split(" ");
                String functionName = headerWords[1];

                String parameterListString = line.split("\\(")[1].split("\\)")[0];
                String[] parameterList = splitByNakedChar(parameterListString, ',');
                ArrayList<String> parameterTypes = new ArrayList<>();
                ArrayList<String> parameterNames = new ArrayList<>();
                String fullFunctionName = functionName+"(";

                for (String parameterString : parameterList) { //Check all the parameters for the function
                    String[] parameterData = parameterString.split(":");
                    parameterTypes.add(parameterData[0].trim());
                    parameterNames.add(parameterData[1].trim());
                    fullFunctionName+=parameterData[0].trim()+",";
                }
                fullFunctionName+=")";

                if (functionList.containsKey(fullFunctionName)) {
                    ErrorManager.printError("Duplicate function declaration: "+functionName+" !");
                }
                functionShortNameList.add(functionName);
                functionList.put(fullFunctionName, new Function(parameterTypes.toArray(new String[0]), parameterNames.toArray(new String[0]), "void", functionName, fullFunctionName, lineNumber));
            }
        }
        System.out.println(functionList);
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Executes actual lines of code
            String line = codeLines[lineNumber-1];
            //System.out.println(" EXECUTING LINE "+ lineNumber+" WHICH IS "+line);

            //getFullMemory().forEach((key, value) -> { //prints out the value and name of each value in memory
                //System.out.println(key+" holds "+value.getValue()+" of type "+value.getType());
            //});

            if (line.length() == 0) { //doesn't even bother running blank lines
                continue;
            }

            String firstToken = "";
            int charCount = 0;
            while (line.charAt(charCount)!=' ' && line.charAt(charCount)!='=') {
                firstToken+=line.charAt(charCount);
                charCount++;
                if (charCount == line.length()) {
                    break;
                }
            }

            if (getFullMemory().containsKey(firstToken)) {
                assignVar(line);
            } else {
                switch (firstToken) {
                    case "var":
                        initializeVar(line);
                        break;
                    case "func":
                        moveOverBracketedCode();
                        break;
                    case "return":
                        lineNumber = lineNumberStack.pop();
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
        if (memory.containsKey(varName)) {
            memory.get(varName).setValueFrom(new Parser(lineSplitByEqual[1]).result());
        } else {
            //System.out.println("mutating local memory");
            HashMap<String, Datum> currentLocalMemory = (HashMap<String, Datum>) localMemory.peek().clone();
            Datum mutatedResult = new Parser(lineSplitByEqual[1]).result();
            currentLocalMemory.remove(varName);
            currentLocalMemory.put(varName, mutatedResult);
            localMemory.pop();
            localMemory.push(currentLocalMemory);
        }
    }
    public static Datum runFunction(Function func, Datum[] arguments, String[] parameterNames) {
        lineNumberStack.push(lineNumber);
        currentFunction = func;
        lineNumber = func.getLineNumberLocation();
        HashMap<String, Datum> argumentMap = new HashMap<>();
        for (int i = 0; i<arguments.length; i++) {
            argumentMap.put(parameterNames[i], arguments[i]);
        }
        localMemory.push(argumentMap);
        return new Datum();
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
    public static HashMap<String, Datum> getFullMemory() {
        HashMap<String, Datum> fullMemory = (HashMap<String, Datum>) memory.clone();
        if (!localMemory.isEmpty()) {
            fullMemory.putAll(localMemory.peek());
        }
        return fullMemory;
    }
    public static int getLineNumber() {
        return lineNumber;
    }
    public static HashMap<String, Function> getFunctionList() {
        return functionList;
    }
    public static ArrayList<String> getFunctionShortNameList() { return functionShortNameList; }
    public static Function getCurrentFunction() { return currentFunction; }

    private static String[] splitByNakedChar(String s, char c) {
        ArrayList<String> splitResults = new ArrayList<>();
        String currentSplit = "";
        int parenCount = 0;
        boolean inQuotes = false;
        int sLength = s.length();
        for (int i = 0; i<sLength; i++) {
            char sChar = s.charAt(i);
            if (sChar == '"') {
                inQuotes = ! inQuotes;
            }
            if (sChar == '(') {
                parenCount++;
            }
            if (sChar == ')') {
                parenCount--;
            }
            if (parenCount == 0 && !inQuotes && sChar == c) {
                splitResults.add(currentSplit);
                currentSplit = "";
            } else {
                currentSplit+=sChar;
            }
        }
        splitResults.add(currentSplit);
        return splitResults.toArray(new String[0]);
    }
}
