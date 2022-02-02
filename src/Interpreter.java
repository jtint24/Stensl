import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter {
    private static int lineNumber;
    private static Function currentFunction;
    private static String[] codeLines;
    private static HashMap<String, Datum> memory = new HashMap<>();
    private static ArrayList<String> functionShortNameList = new ArrayList<>();
    private static ArrayList<String> functionsThatNeedDisambiguation = new ArrayList<>();
    private static HashMap<String, Integer> functionsByShortName = new HashMap<>();
    private static Stack<Integer> lineNumberStack = new Stack<>();
    private static Stack<HashMap<String, Datum>> localMemory = new Stack<>();
    private static boolean inGlobal = true;

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

                if (!isLegalIdentifier(functionName)) {
                    ErrorManager.printError("Illegal function name: "+functionName+"!");
                }

                String parameterListString = line;
                int parameterListStringIndex = 0;
                while (parameterListStringIndex<parameterListString.length()) {
                    if (parameterListString.charAt(parameterListStringIndex) == '(') { //finds the first '(', the beginning of the parameter list
                        break;
                    }
                    parameterListStringIndex++;
                }
                if (parameterListStringIndex>0) {
                    parameterListString = parameterListString.substring(parameterListStringIndex+1); //cuts at the beginning of the parameter list
                }
                parameterListStringIndex = parameterListString.length()-1;
                while (parameterListStringIndex>0) {                      //finds the last ')', the end of the parameter list
                    if (parameterListString.charAt(parameterListStringIndex) == ')') {
                        break;
                    }
                    parameterListStringIndex--;
                }
                if (parameterListStringIndex>=0) {
                    parameterListString = parameterListString.substring(0, parameterListStringIndex); //cuts at the end of the parameter list
                }
                String[] parameterList = splitByNakedChar(parameterListString, ',');
                ArrayList<String> parameterTypes = new ArrayList<>();
                ArrayList<String> parameterNames = new ArrayList<>();
                String fullFunctionName = functionName+"(";
                if (!parameterListString.isBlank()) {
                    for (String parameterString : parameterList) { //Check all the parameters for the function
                        String[] parameterData = parameterString.split(":");
                        parameterTypes.add(parameterData[0].trim());
                        parameterNames.add(parameterData[1].trim());
                        fullFunctionName += parameterData[0].trim() + ",";
                    }
                    fullFunctionName = fullFunctionName.substring(0,fullFunctionName.length()-1);
                }
                fullFunctionName+=")";

                if (memory.containsKey(fullFunctionName)) {
                    ErrorManager.printError("Duplicate function declaration: "+functionName+" !");
                }
                if (functionShortNameList.contains(functionName)) {
                    functionsThatNeedDisambiguation.add(functionName);
                    functionsByShortName.put(functionName, functionsByShortName.get(functionName)+1);
                } else {
                    functionsByShortName.put(functionName, 1);
                }
                functionShortNameList.add(functionName);
                Function functionToAdd = new Function(parameterTypes.toArray(new String[0]), parameterNames.toArray(new String[0]), "void", functionName, fullFunctionName, lineNumber);
                memory.put(fullFunctionName, functionToAdd);
            }
        }

        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Executes actual lines of code
            String line = codeLines[lineNumber-1];
            //System.out.println(getFullMemory()+" , "+functionShortNameList);
            //System.out.println(" EXECUTING LINE "+ lineNumber+" WHICH IS "+line);
            //System.out.println("local mem is "+localMemory.toString()+" global mem is "+memory.toString());

            //getFullMemory().forEach((key, value) -> { //prints out the value and name of each value in memory
                //System.out.println(key+" holds "+value.getValue()+" of type "+value.getType());
            //});

            if (line.length() == 0) { //doesn't even bother running blank lines
                continue;
            }

            if (line.contains("}")) {
                int linePosition = 0;
                while (line.charAt(linePosition)!='}') {
                    linePosition++;
                }
                String bracketMatch = findMatchingBracket(linePosition);
                //System.out.println("this is bracketMatch: "+bracketMatch);
                if (bracketMatch.startsWith("func ")) {
                    ErrorManager.printError("No return statement!");
                }
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

            if (getFullMemory().containsKey(firstToken) && !getFullMemory().get(firstToken).getIsFunction()) {
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
                        localMemory.pop();
                        if (lineNumberStack.size() == 0) {
                            inGlobal = true;
                        }
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

        if (!isLegalIdentifier(variableName)) {
            ErrorManager.printError("illegal variable name: "+variableName+"!");
        }

        Parser variableParser = (new Parser(lineSplitByEqual[1]));
        Datum variable = variableParser.result();
        variable.setIsMutable(!isConst);

        if (variable instanceof Function) {
            ((Function) variable).setName(variableName);
            ((Function) variable).regenerateFullName();
            functionShortNameList.add(variableName);
            if (functionsByShortName.containsKey(variableName)) {
                functionsByShortName.put(variableName, functionsByShortName.get(variableName)+1);
            } else {
                functionsByShortName.put(variableName, 1);
            }
            variableName = ((Function) variable).getFullName();
        }

        if (inGlobal) {
            memory.put(variableName, variable);
        } else {
            localMemory.peek().put(variableName, variable);
        }
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
        inGlobal = false;
        lineNumberStack.push(lineNumber);
        currentFunction = func;
        lineNumber = func.getLineNumberLocation();
        HashMap<String, Datum> argumentMap = new HashMap<>();
        for (int i = 0; i<arguments.length; i++) { //Put arguments into local memory

            if (arguments[i].getIsFunction()) {
                ((Function)arguments[i]).setName(parameterNames[i]);
                ((Function)arguments[i]).regenerateFullName();
                //Function paramFunc = ((Function)arguments[i]).setName(parameterNames[i]);
                if (functionsByShortName.containsKey(((Function)arguments[i]).getName())) {
                    functionsByShortName.put(((Function) arguments[i]).getName(), functionsByShortName.get(((Function) arguments[i]).getName())+1);
                } else {
                    functionShortNameList.add(((Function) arguments[i]).getName());
                    functionsByShortName.put(((Function) arguments[i]).getName(), 1);
                }
                argumentMap.put(((Function) arguments[i]).fullName, arguments[i]);
            } else {
                argumentMap.put(parameterNames[i], arguments[i]);
            }
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
    public static Stack<Integer> getLineNumberStack() {
        return lineNumberStack;
    }
    public static String getCurrentLine() {
        return codeLines[lineNumber-1];
    }
    public static HashMap<String, Integer> getFunctionsByShortName() {
        return functionsByShortName;
    }

    public static ArrayList<String> getFunctionShortNameList() { return functionShortNameList; }
    public static Function getCurrentFunction() { return currentFunction; }
    public static ArrayList<String> getFunctionsThatNeedDisambiguation() { return functionsThatNeedDisambiguation; }
    private static String findMatchingBracket(int linePosition) {
        int bracketCount = 0;
        int originalLineNumber = lineNumber;
        while (lineNumber>=0) {
            while (linePosition>=0) {
                char activeChar = codeLines[lineNumber-1].charAt(linePosition);
                if (activeChar == '{') {
                    bracketCount--;
                }
                if (activeChar == '}') {
                    bracketCount++;
                }
                if (bracketCount == 0) {
                    return codeLines[lineNumber-1];
                }
                linePosition--;
            }
            lineNumber--;
        }
        lineNumber = originalLineNumber;
        ErrorManager.printError("Bracket mismatch!");
        return "";
    }

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
    private static boolean isLegalIdentifier(String name) {
        String illegalChars = ".()+-%*/\\{}[]=&|!^<>?,;:";
        for (char activeChar : name.toCharArray()) {
            if (illegalChars.contains(""+activeChar)) {
                return false;
            }
        }
        switch(name) {
            case "var", "func", "if", "for", "while", "return", "else", "elseif":
                return false;
            default:
                return true;
        }
    }
}
