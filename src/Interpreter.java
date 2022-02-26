import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter {
    private static int lineNumber;
    private static Function currentFunction;
    private static String[] codeLines;
    private static HashMap<String, Datum> memory = new HashMap<>();
    private static ArrayList<String> functionShortNameList = new ArrayList<>();
    private static HashMap<String, DatumClass> classes = new HashMap<>();
    private static Stack<Integer> lineNumberStack = new Stack<>();
    private static Stack<HashMap<String, Datum>> localMemory = new Stack<>();
    private static DatumObject currentObject = null;
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
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Checks for function headers and classes and adds them to functionList
            String line = codeLines[lineNumber-1];

            if (line.startsWith("class ")) {
                String className = line.split(" ")[1];
                if (!isLegalIdentifier(className)) {
                    ErrorManager.printError("Class '"+className+"' is not a legal identifier!");
                }
                int braceCount = 1;
                HashMap<String, String> properties = new HashMap<>();
                HashMap<String, Datum> defaultVals = new HashMap<>();
                while (braceCount!=0) {
                    lineNumber++;
                    if (lineNumber == code.length+1) {
                        ErrorManager.printError("Unterminated class '"+className+"'!");
                    }
                    line = codeLines[lineNumber-1];
                    if (line.endsWith("{")) {
                        braceCount++;
                    }
                    if (line.startsWith("}")) {
                        braceCount--;
                    }
                    if (line.startsWith("var ")) {
                        String[] lineSplitByEqual = new String[1];
                        lineSplitByEqual[0] = line;
                        boolean isAssigned = false;
                        if (line.contains("=")) {
                            isAssigned = true;
                            lineSplitByEqual = line.split("=");
                        }
                        String[] lineSplitBySpace = lineSplitByEqual[0].split(" ");
                        boolean isConstant = false;
                        int offset = 0;
                        if (lineSplitBySpace[1].equals("const")) {
                            offset++;
                            isConstant = true;
                            if (!isAssigned) {
                                ErrorManager.printError("Constants must be assigned to!");
                            }
                        }
                        String propertyType = lineSplitBySpace[1+offset];
                        String propertyName = lineSplitBySpace[2+offset];
                        if (isAssigned) {
                            Datum assignTo = new Parser(line.substring(lineSplitByEqual[0].length()+1)).result();
                            if (!assignTo.getType().equals(propertyType)) {
                                ErrorManager.printError("Type Mismatch! Type "+assignTo.getType()+" does not match expected type "+propertyType+"!");
                            }
                            assignTo.setIsMutable(!isConstant);
                            defaultVals.put(propertyName, assignTo);
                        }
                        properties.put(propertyName, propertyType);
                    }
                }
                DatumClass newClass = new DatumClass(className, properties);
                newClass.setDefaultVals(defaultVals);
                System.out.println(newClass);
                classes.put(className, newClass);
            }

            if (line.startsWith("func ")) {
                String[] headerWords = line.split("\\(")[0].split(" ");
                String functionName = headerWords[2];

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
                        if (parameterNames.contains(parameterData[1].trim())) {
                            ErrorManager.printError("Argument "+parameterData[1].trim()+" is a duplicate!");
                        }
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

                String returnType = headerWords[1];
                functionShortNameList.add(functionName);
                Function functionToAdd = new Function(parameterTypes.toArray(new String[0]), parameterNames.toArray(new String[0]), returnType, functionName, fullFunctionName, lineNumber);
                memory.put(fullFunctionName, functionToAdd);
            }
        }

        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Executes actual lines of code
            runLine();
        }
    }

    private static Datum runLine() {
        String line = codeLines[lineNumber-1];
        //System.out.println(getFullMemory()+" , "+functionShortNameList);
        //System.out.println(" EXECUTING LINE "+ lineNumber+" WHICH IS "+line);
        //System.out.println("local mem is "+localMemory.toString()+" global mem is "+memory.toString());

        //getFullMemory().forEach((key, value) -> { //prints out the value and name of each value in memory
        //System.out.println(key+" holds "+value.getValue()+" of type "+value.getType());
        //});

        if (line.length() == 0) { //doesn't even bother running blank lines
            return new Datum();
        }

        if (line.equals("}")) {
            int linePosition = 0;
            while (line.charAt(linePosition)!='}') {
                linePosition++;
            }
            String bracketMatch = findMatchingBracket(linePosition);
            //System.out.println("this is bracketMatch: "+bracketMatch);
            if (bracketMatch.startsWith("func ")) {
                ErrorManager.printError("No return statement!");
            }
            if (bracketMatch.startsWith("for ")) {
                int minMaxArgsBeginningIndex = bracketMatch.split("\\(")[0].length()+1;
                int minMaxArgsEndIndex = bracketMatch.split("\\{")[0].trim().length()-1;

                String minMaxArgsString = bracketMatch.substring(minMaxArgsBeginningIndex,minMaxArgsEndIndex);
                String[] minMaxArgs = splitByNakedChar(minMaxArgsString,',');

                float maximumIndex = Float.parseFloat(new Parser(minMaxArgs[1]).getValue());
                String indexDeclaration = bracketMatch.split("\\{")[1].split("\\(")[1].split("\\)")[0];
                String indexName = indexDeclaration.split(" ")[1];
                if (Float.parseFloat(localMemory.peek().get(indexName).getValue())<maximumIndex) {
                    localMemory.peek().put(indexName, localMemory.peek().get(indexName).increment());
                    lineNumber = findMatchingBracketIndex(linePosition);
                } else {
                    localMemory.pop();
                }
            }
            return new Datum();
        }
        if (line.matches("}[ ]+else[ ]+\\{")) {
            String bracketMatch = findMatchingBracket(0);
            if (!bracketMatch.startsWith("if ")) {
                ErrorManager.printError("Else without if!");
            }
            moveOverBracketedCode(1);
            return new Datum();
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
        boolean isArray = firstToken.contains("[");
        boolean isProperty = firstToken.contains(".");
        boolean isAssignment = false;
        while (charCount<line.length()) {
            charCount++;
            if (line.charAt(charCount) != ' ') {
                if (line.charAt(charCount) == '=') {
                    isAssignment = true;
                    break;
                } else {
                    break;
                }
            }
        }
        if ((isArray || isProperty) && isAssignment) {
            assignPropertyVal(line);
            return new Datum();
        }
        if ((getFullMemory().containsKey(firstToken) || functionShortNameList.contains(firstToken)) && isAssignment) {
            assignVar(line);
        } else {
            switch (firstToken) {
                case "var":
                    initializeVar(line);
                    break;
                case "func", "class":
                    moveOverBracketedCode();
                    break;
                case "if":
                    runIf();
                    break;
                case "for":
                    runFor();
                    break;
                case "return":
                    if (lineNumberStack.size() == 0) {
                        inGlobal = true;
                    }
                    String argumentStringPlusParen = line.split("\\(")[1];
                    if (argumentStringPlusParen.trim().equals(")")) {
                        lineNumber = lineNumberStack.pop();
                        localMemory.pop();
                        return new Datum("","");
                    } else {
                        String argumentString = line.split("\\(")[1].split("\\)")[0];
                        Datum returnResult = (new Parser(argumentString)).result();
                        lineNumber = lineNumberStack.pop();
                        localMemory.pop();
                        return returnResult;
                    }
                default:
                    (new Parser(line)).getValue();
                    break;
            }
        }
        return new Datum();
    }
    private static void assignPropertyVal(String line) {

        String[] lineSplitByEqual = line.split("=");
        String[] chainData = lineSplitByEqual[0].split("\\.");
        String propertyName = chainData[0];
        int[] indices = new int[chainData.length-1];
        for (int i = 1; i<chainData.length; i++) {
            chainData[i]=chainData[i].trim();

            if (!chainData[i].endsWith("]")) {
                ErrorManager.printError("Syntax error on assignment!");
            }
            indices[i-1] = Integer.parseInt(chainData[i].substring(0, chainData[i].length()-1));
        }
        Datum property = getFullMemory().get(propertyName);
        if (property instanceof DatumObject) {
            String expressionString = line.substring(lineSplitByEqual[0].length()+1);
            Datum assignTo = (new Parser(expressionString.trim())).result();
            ((DatumArray) property).setElement(assignTo, indices);
        } else {
            ErrorManager.printError("Syntax error on assignment!");
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

        Parser variableParser = (new Parser(line.substring(lineSplitByEqual[0].length()+1)));

        if (!variableType.equals(variableParser.getType()) && !(variableType.equals("string") && variableParser.getType().equals("char")) && !(variableType.equals("float") && variableParser.getType().equals("int"))) {
            ErrorManager.printError("Value of type "+variableParser.getType()+" cannot be assigned to a variable of type "+variableType+"!");
        }

        Datum variable = variableParser.result();
        variable.setIsMutable(!isConst);

        if (variable instanceof Function) {
            ((Function) variable).setName(variableName);
            ((Function) variable).regenerateFullName();
            functionShortNameList.add(variableName);
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
            memory.get(varName).setValueFrom(new Parser(line.substring(lineSplitByEqual[0].length()+1)).result());
        } else if (getGlobalFunctionShortnames().contains(varName)) {
            String varFullName = "";
            for (Datum memBlock : memory.values()) {
                if (memBlock.getIsFunction()) {
                    if (((Function) memBlock).getName().equals(varName)) {
                        varFullName = ((Function) memBlock).getFullName();
                    }
                }
            }
            Function functionToAssignTo = ((Function)new Parser(line.substring(lineSplitByEqual[0].length()+1)).result()).clone();
            functionToAssignTo.setName(varName);
            functionToAssignTo.regenerateFullName();
            memory.put(varFullName, functionToAssignTo);
        } else {
            //System.out.println("mutating local memory");
            Datum varToMutate = localMemory.peek().get(varName);
            if (!varToMutate.getIsMutable()) {
                ErrorManager.printError("Attempt to mutate a constant, "+varName+"!");
            }
            HashMap<String, Datum> currentLocalMemory = (HashMap<String, Datum>) localMemory.peek().clone();
            Datum mutatedResult = new Parser(line.substring(lineSplitByEqual[0].length()+1)).result();
            if (!mutatedResult.getType().equals(varToMutate.getType()) && !(mutatedResult.getType().equals("string") && mutatedResult.getType().equals("char")) && !(mutatedResult.getType().equals("float") && mutatedResult.getType().equals("int"))) {
                ErrorManager.printError("Values of type "+mutatedResult.getType()+" are not compatible with variable "+varName+" of type "+varToMutate.getType());
            }
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
        lineNumber = func.getLineNumberLocation()+1;
        HashMap<String, Datum> argumentMap = new HashMap<>();
        for (int i = 0; i<arguments.length; i++) { //Put arguments into local memory
            if (memory.containsKey(parameterNames[i])) {
                ErrorManager.printError("Argument "+parameterNames[i]+" is a duplicate!");
            }
            if (arguments[i].getIsFunction()) {
                ((Function)arguments[i]).setName(parameterNames[i]);
                ((Function)arguments[i]).regenerateFullName();
                //Function paramFunc = ((Function)arguments[i]).setName(parameterNames[i]);
                if (!functionShortNameList.contains(((Function) arguments[i]).getName())) {
                    functionShortNameList.add(((Function) arguments[i]).getName());
                }
                argumentMap.put(((Function) arguments[i]).fullName, arguments[i]);
            } else {
                argumentMap.put(parameterNames[i], arguments[i]);
            }
        }
        localMemory.push(argumentMap);
        Datum lineResult = new Datum();
        for (; lineNumber < codeLines.length; lineNumber++) {
            lineResult = runLine();
            if (!lineResult.getIsBlank()) {
                break;
            }
        }
        return lineResult;
    }
    public static void runIf() {
        String line = codeLines[lineNumber-1];
        int parenCount = 0;
        boolean parenCountHasExceededZero = false;
        int lineIndex = 2;
        while (!(parenCount == 0 && parenCountHasExceededZero)) {
            if (line.charAt(lineIndex) == '(') {
                parenCount++;
                parenCountHasExceededZero = true;
            }
            if (line.charAt(lineIndex) == ')') {
                parenCount--;
            }
            lineIndex++;
            if (lineIndex>=line.length()) {
                ErrorManager.printError("Syntax Error on If!");
            }
        }
        if (!line.trim().endsWith("{")) {
            ErrorManager.printError("Bracket Error on If!");
        }
        String ifExpression = line.substring(2, lineIndex);
        String ifExpressionResult = new Parser(ifExpression).result().getValue();
        if (ifExpressionResult.equals("false")) {
            moveOverBracketedCode();
        }
        if (codeLines[lineNumber-1].matches("}[ ]+else[ ]+\\{")) {
            if (ifExpressionResult.equals("true")) {
                moveOverBracketedCode();
            }
        }
    }
    public static void runFor() {
        String line = codeLines[lineNumber-1];
        int minMaxArgsBeginningIndex = line.split("\\(")[0].length()+1;
        int minMaxArgsEndIndex = line.split("\\{")[0].trim().length()-1;

        String minMaxArgsString = line.substring(minMaxArgsBeginningIndex,minMaxArgsEndIndex);
        String[] minMaxArgs = splitByNakedChar(minMaxArgsString,',');

        float minimumIndex = Float.parseFloat(new Parser(minMaxArgs[0]).getValue());
        float maximumIndex = Float.parseFloat(new Parser(minMaxArgs[1]).getValue());

        if (minimumIndex%1!=0 || maximumIndex%1!=0) {
            ErrorManager.printError("Non-integer index in for loop!");
        }

        String indexDeclaration = line.split("\\{")[1].split("\\(")[1].split("\\)")[0];
        String indexType = indexDeclaration.split(" ")[0];
        String indexName = indexDeclaration.split(" ")[1];
        Datum index = new Datum(((Integer)(int)minimumIndex).toString(), indexType);
        HashMap<String, Datum> currentLocalMem;
        if (localMemory.size()>0) {
            currentLocalMem = (HashMap<String, Datum>) localMemory.peek().clone();
        } else {
            currentLocalMem = new HashMap<>();
        }
        currentLocalMem.put(indexName, index);
        localMemory.push(currentLocalMem);
        return;
    }
    private static void moveOverBracketedCode() {
        int bracketCount = 0;
        lineNumber--;
        do  {
            lineNumber++;
            String currentLine = codeLines[lineNumber-1];
            if (currentLine.endsWith("{")) {
                bracketCount++;
            }
            if (currentLine.startsWith("}")) {
                bracketCount--;
            }
            if (currentLine.startsWith("}") && currentLine.endsWith("{") && bracketCount == 1) {
                break;
            }
        } while (bracketCount!=0);
        //lineNumber++;
    }
    private static void moveOverBracketedCode(int bracketCount) {
        lineNumber--;
        boolean isFirst = true;
        do  {
            lineNumber++;
            String currentLine = codeLines[lineNumber-1];

            if (currentLine.endsWith("{")) {
                bracketCount++;
            }
            if (currentLine.startsWith("}")) {
                bracketCount--;
            }
            if (!isFirst && currentLine.startsWith("}") && currentLine.endsWith("{") && bracketCount == 1) {
                break;
            }
            isFirst = false;
        } while (bracketCount!=0);
        //lineNumber++;
    }
    public static HashMap<String, Datum> getFullMemory() {
        HashMap<String, Datum> fullMemory = (HashMap<String, Datum>) memory.clone();
        if (!localMemory.isEmpty()) {
            fullMemory.putAll(localMemory.peek());
        }
        if (currentObject!=null) {
            fullMemory.putAll(currentObject.getProperties());
        }
        return fullMemory;
    }
    /*public static Datum retrieveMemorySubvalue(String name) {
        if (name.endsWith("]")) {
            int i = name.length();
            while (name.charAt(i)!='[') {
                i--;
            }
            int arrayIndex =
        }
    }*/
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
        HashMap<String, Integer> funcsByShortName = new HashMap<>();
        for (Datum memBlock : getFullMemory().values()) {
            if (memBlock.getIsFunction()) {
                Function memBlockFunc = (Function)memBlock;
                if (funcsByShortName.containsKey(memBlockFunc.getName())) {
                    funcsByShortName.put(memBlockFunc.getName(), funcsByShortName.get(memBlockFunc.getName())+1);
                } else {
                    funcsByShortName.put(memBlockFunc.getName(), 1);
                }
            }
        }
        return funcsByShortName;
    }

    public static ArrayList<String> getGlobalFunctionShortnames() {
        ArrayList<String> globalFuncShortnames = new ArrayList<>();
        for (Datum memBlock : memory.values()) {
            if (memBlock.getIsFunction()) {
                globalFuncShortnames.add(((Function)memBlock).getName());
            }
        }
        return globalFuncShortnames;
    }
    public static HashMap<String, DatumClass> getClasses() {
        return classes;
    }
    public static ArrayList<String> getFunctionShortNameList() { return functionShortNameList; }
    public static Function getCurrentFunction() { return currentFunction; }
    private static String findMatchingBracket(int linePosition) {
        int bracketCount = 0;
        int scanLineNumber = lineNumber;
        while (scanLineNumber>=0) {
            while (linePosition>=0) {
                char activeChar = codeLines[scanLineNumber-1].charAt(linePosition);
                if (activeChar == '{') {
                    bracketCount--;
                }
                if (activeChar == '}') {
                    bracketCount++;
                }
                if (bracketCount == 0) {
                    return codeLines[scanLineNumber-1];
                }
                linePosition--;
            }
            scanLineNumber--;
            linePosition = codeLines[scanLineNumber-1].length()-1;
        }
        ErrorManager.printError("Bracket mismatch!");
        return "";
    }
    private static int findMatchingBracketIndex(int linePosition) {
        int bracketCount = 0;
        int scanLineNumber = lineNumber;
        while (scanLineNumber>=0) {
            while (linePosition>=0) {
                char activeChar = codeLines[scanLineNumber-1].charAt(linePosition);
                if (activeChar == '{') {
                    bracketCount--;
                }
                if (activeChar == '}') {
                    bracketCount++;
                }
                if (bracketCount == 0) {
                    return scanLineNumber;
                }
                linePosition--;
            }
            scanLineNumber--;
            linePosition = codeLines[scanLineNumber-1].length()-1;
        }
        ErrorManager.printError("Bracket mismatch!");
        return 0;
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
        String illegalChars = ".()+-%*/\\{}[]=&|!^<>?,;:\"";
        for (char activeChar : name.toCharArray()) {
            if (illegalChars.contains(""+activeChar)) {
                return false;
            }
        }
        switch(name) {
            case "var", "func", "if", "for", "while", "return", "else", "elseif","const","public","private":
                return false;
            default:
                return true;
        }
    }
}
