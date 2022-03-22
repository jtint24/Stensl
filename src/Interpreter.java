import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Interpreter {
    private static int lineNumber;
    private static Function currentFunction;
    private static String[] codeLines;
    private static InstructionType[] instructionTypes;
    private static Parser[] premadeParsers;
    private static final HashMap<String, Datum> memory = new HashMap<>();
    private static final ArrayList<String> functionShortNameList = new ArrayList<>();
    private static final HashMap<String, DatumClass> classes = new HashMap<>();
    private static final Stack<Integer> lineNumberStack = new Stack<>();
    private static final Stack<HashMap<String, Datum>> localMemory = new Stack<>();
    private static final Stack<DatumObject> currentObject = new Stack<>();
    private static long instructionTime = InputManager.startTime;
    private static boolean safeToCopy = true;

    public static void runStensl(String[] code) {
        String[] newCode = new String[code.length+1];
        System.arraycopy(code, 0, newCode, 0, code.length);
        newCode[code.length] = "";
        code = newCode;
        boolean insideInlineComment = false;
        boolean insideBlockComment = false;
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //trim out whitespace and comments from the line;
            String line = code[lineNumber-1];
            line = line.trim();
            StringBuilder cleanedLine = new StringBuilder();
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
                            ErrorManager.printError("Block comment terminator */ used without opening a block comment!","9:1.1");
                        }
                    }
                }
                if (!insideInlineComment && !insideBlockComment) {
                    cleanedLine.append(readChar);
                }
            }
            code[lineNumber-1] = cleanedLine.toString().trim();
            insideInlineComment = false;
        }
        if (insideBlockComment) {
            ErrorManager.printError("Unterminated block comment!","9:1.2");
        }

        codeLines = code;
        instructionTypes = new InstructionType[codeLines.length];
        premadeParsers = new Parser[codeLines.length];
        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Checks for function headers and classes and adds them to functionList
            String line = codeLines[lineNumber-1];

            if (line.startsWith("class ")) { //this initializes classes
                String className = line.split(" ")[1];
                if (!isLegalIdentifier(className)) {
                    ErrorManager.printError("Class '"+className+"' is not a legal identifier!","9:1.3");
                }
                int braceCount = 1;
                HashMap<String, String> properties = new HashMap<>();
                HashMap<String, String[]> propertiesScopes = new HashMap<>();
                HashMap<String, Datum> defaultVals = new HashMap<>();
                while (braceCount!=0) {         //iterates across the class body
                    lineNumber++;
                    if (lineNumber == code.length+1) {
                        ErrorManager.printError("Unterminated class '"+className+"'!","9:1.4");
                    }
                    line = codeLines[lineNumber-1];
                    if (line.endsWith("{")) {
                        braceCount++;
                    }
                    if (line.startsWith("}")) {
                        braceCount--;
                    }
                    if (line.startsWith("func ")) {
                        String[] headerWords = line.split("\\(")[0].split(" ");
                        int offset = 0;
                        String[] scopeItems = {"public"};
                        if (headerWords[1+offset].equals("public") || headerWords[1+offset].equals("private") || headerWords[1+offset].contains(",")) {
                            String scope = headerWords[1+offset];
                            offset++;
                            if (scope.equals("public")) {
                                scopeItems = new String[]{"public"};
                            } else if (scope.equals("private")) {
                                scopeItems = new String[]{className};
                            } else {
                                scopeItems = scope.split(",");
                            }
                        }
                        String returnType = headerWords[1+offset];
                        String functionName = headerWords[2+offset];

                        if (!isLegalIdentifier(functionName)) {
                            ErrorManager.printError("Illegal function name: "+functionName+"!","9:1.5");
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
                        StringBuilder fullFunctionName = new StringBuilder(functionName + "(");
                        if (!parameterListString.isBlank()) {
                            for (String parameterString : parameterList) { //Check all the parameters for the function
                                String[] parameterData = parameterString.split(":");
                                if (parameterNames.contains(parameterData[1].trim())) {
                                    ErrorManager.printError("Argument "+parameterData[1].trim()+" is a duplicate!", "9:2.1");
                                }
                                parameterTypes.add(parameterData[0].trim());
                                parameterNames.add(parameterData[1].trim());
                                fullFunctionName.append(parameterData[0].trim()).append(",");
                            }
                            fullFunctionName = new StringBuilder(fullFunctionName.substring(0, fullFunctionName.length() - 1));
                        }
                        fullFunctionName.append(")");

                        if (properties.containsKey(functionName)) {
                            ErrorManager.printError("Duplicate function declaration: "+functionName+" !","9:2.2");
                        }

                        Function functionToAdd = new Function(parameterTypes.toArray(new String[0]), parameterNames.toArray(new String[0]), returnType, functionName, fullFunctionName.toString(), lineNumber);
                        functionToAdd.setScope(scopeItems);
                        properties.put(functionName, functionToAdd.getType()); //Adds a version of the function that can be used as a property
                        defaultVals.put(functionName, functionToAdd);
                        propertiesScopes.put(functionName, scopeItems);
                        moveOverBracketedCode();
                        lineNumber--;
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
                                ErrorManager.printError("Constants must be assigned to!","9:1.6");
                            }
                        }
                        String[] scopeItems = new String[]{"public"};
                        if (lineSplitBySpace[1+offset].equals("public") || lineSplitBySpace[1+offset].equals("private") || lineSplitBySpace[1+offset].contains(",")) {
                            String scope = lineSplitBySpace[1+offset];
                            offset++;
                            if (scope.equals("public")) {
                                scopeItems = new String[]{"public"};
                            } else if (scope.equals("private")) {
                                scopeItems = new String[]{className};
                            } else {
                                scopeItems = scope.split(",");
                            }
                        }
                        String propertyType = lineSplitBySpace[1+offset];
                        String propertyName = lineSplitBySpace[2+offset];
                        if (isAssigned) {
                            Datum assignTo = new Parser(line.substring(lineSplitByEqual[0].length()+1)).result();
                            if (!assignTo.getType().equals(propertyType)) {
                                ErrorManager.printError("Type Mismatch! Type "+assignTo.getType()+" does not match expected type "+propertyType+"!","9:2.3");
                            }
                            assignTo.setIsMutable(!isConstant);
                            assignTo.setScope(scopeItems);
                            defaultVals.put(propertyName, assignTo);
                        }

                        properties.put(propertyName, propertyType);
                        propertiesScopes.put(propertyName, scopeItems);
                    }
                }
                DatumClass newClass = new DatumClass(className, properties);
                newClass.setDefaultVals(defaultVals);
                newClass.setPropertiesScope(propertiesScopes);
                //System.out.println(newClass);
                classes.put(className, newClass);
            }

            if (line.startsWith("func ")) {
                String[] headerWords = line.split("\\(")[0].split(" ");

                String returnType = headerWords[1];
                String functionName = headerWords[2];

                if (!isLegalIdentifier(functionName)) {
                    ErrorManager.printError("Illegal function name: "+functionName+"!","9:1.5");
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
                StringBuilder fullFunctionName = new StringBuilder(functionName + "(");
                if (!parameterListString.isBlank()) {
                    for (String parameterString : parameterList) { //Check all the parameters for the function
                        String[] parameterData = parameterString.split(":");
                        if (parameterNames.contains(parameterData[1].trim())) {
                            ErrorManager.printError("Argument "+parameterData[1].trim()+" is a duplicate!","9:2.1");
                        }
                        parameterTypes.add(parameterData[0].trim());
                        parameterNames.add(parameterData[1].trim());
                        fullFunctionName.append(parameterData[0].trim()).append(",");
                    }
                    fullFunctionName = new StringBuilder(fullFunctionName.substring(0, fullFunctionName.length() - 1));
                }
                fullFunctionName.append(")");

                if (memory.containsKey(fullFunctionName.toString())) {
                    ErrorManager.printError("Duplicate function declaration: "+functionName+" !","9:2.2");
                }

                functionShortNameList.add(functionName);
                Function functionToAdd = new Function(parameterTypes.toArray(new String[0]), parameterNames.toArray(new String[0]), returnType, functionName, fullFunctionName.toString(), lineNumber);
                memory.put(fullFunctionName.toString(), functionToAdd);
            }
        }

        for (lineNumber = 1; lineNumber<code.length+1; lineNumber++) { //Executes actual lines of code
            runLine();
        }
    }

    private static Datum runLine() {
        String line = codeLines[lineNumber-1];
        //System.out.println(getFullMemory()+" , "+functionShortNameList);
        //if (System.nanoTime()-instructionTime!=0) {
            //System.out.println("that took " + (System.nanoTime() - instructionTime)/1000.0);
        //}
        //instructionTime = System.nanoTime();
        System.out.println(" EXECUTING LINE "+ lineNumber+" WHICH IS "+line);
        //if (!localMemory.isEmpty()) {
        //    System.out.println("local mem is " + localMemory.peek().toString() + " global mem is " + memory.toString());
        //}
        //System.out.println("CURRENT OBJECTS: "+currentObject);
        //System.out.println("CURRENT LINE NUMBER STACK: "+lineNumber+": "+lineNumberStack);

        //getFullMemory().forEach((key, value) -> { //prints out the value and name of each value in memory
        //System.out.println(key+" holds "+value.getValue()+" of type "+value.getType());
        //});

        if (line.length() == 0) { //doesn't even bother running blank lines
            return new Datum();
        }

        if (instructionTypes[lineNumber-1] != null) {
            switch (instructionTypes[lineNumber-1]) {
                case INITIALIZE -> initializeVar(line);
                case IF -> runIf();
                case FOR -> runFor();
                /*case PARSER -> {
                    safeToCopy = true;
                    //if (premadeParsers[lineNumber-1] == null) {
                        premadeParsers[lineNumber-1] = new Parser(line);
                   // }
                    premadeParsers[lineNumber-1].getValue();
                    //if (!safeToCopy) {
                        premadeParsers[lineNumber-1] = null;
                    //}
                }*/
                case ASSIGN -> assignVar(line);
            }
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
                ErrorManager.printError("No return statement!","9:2.4");
            }
            if (bracketMatch.startsWith("for ")) {
                int minMaxArgsBeginningIndex = bracketMatch.split("\\(")[0].length()+1;
                int minMaxArgsEndIndex = bracketMatch.split("\\{")[0].trim().length()-1;

                String minMaxArgsString = bracketMatch.substring(minMaxArgsBeginningIndex,minMaxArgsEndIndex);
                String[] minMaxArgs = splitByNakedChar(minMaxArgsString,',');

                float maximumIndex = Float.parseFloat(new Parser(minMaxArgs[1]).getValue());
                String indexDeclaration = bracketMatch.split("\\{")[1].split("\\(")[1].split("\\)")[0];
                String indexName = indexDeclaration.split(" ")[1];
                if (Float.parseFloat(localMemory.peek().get(indexName).getValue())<maximumIndex) { //Code if the for loop runs its body again
                    Datum incrementedIndex = localMemory.peek().get(indexName).increment();
                    HashMap<String, Datum> newInsideForLoopMem = new HashMap<>();
                    if (localMemory.size()>1) {
                        HashMap<String, Datum> oldInsideForLoopMem = localMemory.pop();
                        for (String localVar : oldInsideForLoopMem.keySet()) {
                            if (localMemory.peek().containsKey(localVar)) {
                                newInsideForLoopMem.put(localVar, oldInsideForLoopMem.get(localVar));              //Removes variables that were declared inside the for loop body
                            }
                        }
                    } else {
                        localMemory.pop();
                    }
                    newInsideForLoopMem.put(indexName, incrementedIndex);
                    localMemory.push(newInsideForLoopMem);
                    lineNumber = findMatchingBracketIndex(linePosition);
                } else {     //Code for when the for loop has been exhausted:
                    HashMap<String, Datum> oldLocalMem = localMemory.pop();
                    if (!localMemory.isEmpty()) {
                        for (String localVar : oldLocalMem.keySet()) { //puts any variables that were modified in the loop back in local mem
                            if (localMemory.peek().containsKey(localVar)) {
                                localMemory.peek().put(localVar, oldLocalMem.get(localVar));
                            }
                        }
                    }

                }
            }
            return new Datum();
        }
        if (line.startsWith("}") && line.endsWith("{")) {
            if (line.matches("}[ ]+else[ ]+\\{")) {
                String bracketMatch = findMatchingBracket(0);
                if (!bracketMatch.startsWith("if ")) {
                    ErrorManager.printError("Else without if!","9.1.");
                }
                moveOverBracketedCode(1);
                return new Datum();
            }
        }

        StringBuilder firstToken = new StringBuilder();
        int charCount = 0;
        while (line.charAt(charCount)!=' ' && line.charAt(charCount)!='=') {
            firstToken.append(line.charAt(charCount));
            charCount++;
            if (charCount == line.length()) {
                break;
            }
        }
        boolean isArray = firstToken.toString().contains("[");
        boolean isProperty = firstToken.toString().contains(".");
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
        if ((getFullMemory().containsKey(firstToken.toString()) || functionShortNameList.contains(firstToken.toString())) && isAssignment) {
            instructionTypes[lineNumber-1] = InstructionType.ASSIGN;
            assignVar(line);
        } else {
            switch (firstToken.toString()) {
                case "var" -> {
                    instructionTypes[lineNumber-1] = InstructionType.INITIALIZE;
                    initializeVar(line);
                }
                case "func", "class" -> moveOverBracketedCode();
                case "if" -> {
                    instructionTypes[lineNumber-1] = InstructionType.IF;
                    runIf();
                }
                case "for" -> {
                    instructionTypes[lineNumber-1] = InstructionType.FOR;
                    runFor();
                }
                case "return" -> {
                    String argumentStringPlusParen = line.split("\\(")[1];
                    if (argumentStringPlusParen.trim().equals(")")) {
                        lineNumber = lineNumberStack.pop();
                        localMemory.pop();
                        if (!currentObject.isEmpty()) {
                            currentObject.pop();
                        }
                        return new Datum("", "");
                    } else {
                        String argumentString = line.substring(line.split("\\(")[0].length() + 1, line.length() - 1);
                        Datum returnResult = (new Parser(argumentString)).result();
                        lineNumber = lineNumberStack.pop();
                        localMemory.pop();
                        if (!currentObject.isEmpty()) {
                            currentObject.pop();
                        }
                        return returnResult.publicVersion();
                    }
                }
                default -> {
                    safeToCopy = true;
                    //instructionTypes[lineNumber-1] = InstructionType.PARSER;
                    premadeParsers[lineNumber-1] = new Parser(line);
                    premadeParsers[lineNumber-1].getValue();
                    if (!safeToCopy) {
                        premadeParsers[lineNumber-1] = null;
                    }
                }
            }
        }
        return new Datum();
    }
    private static void assignPropertyVal(String line) {
        String[] lineSplitByEqual = splitByNakedChar(line,'='); // Get the first half of the assignment
        String[] chainData = lineSplitByEqual[0].split("[\\.\\[]"); //Get the root of the chain
        String propertyName = chainData[0]; //Property name = root of chain
        Datum property = getFullMemory().get(propertyName).getProperty(line.substring(propertyName.length()+1,lineSplitByEqual[0].length()-1)); //Get the actual property from memory
        String expressionString = line.substring(lineSplitByEqual[0].length()+1); //Get the string representing the assignTo expression
        Datum assignTo = (new Parser(expressionString.trim())).result(); //Parse the expressionString
        property.setValueFrom(assignTo); //Set the property to the assign value
    }
    private static void initializeVar(String line) {
        String[] lineSplitByEqual = line.split("=");
        String[] lineSplitBySpace = lineSplitByEqual[0].split(" ");

        boolean isConst = lineSplitBySpace[1].equals("const");
        int flagOffset = isConst ? 1 : 0;

        String variableType = lineSplitBySpace[1+flagOffset];
        String variableName = lineSplitBySpace[2+flagOffset];

        if (!isLegalIdentifier(variableName)) {
            ErrorManager.printError("Illegal variable name: '"+variableName+"' !","9:1.7");
        }

        if (getFullMemory().containsKey(variableName)) {
            ErrorManager.printError("Duplicate variable declaration: '"+variableName+"' !","9:2.5");
        }

        safeToCopy = true;
        if (premadeParsers[lineNumber-1] == null) {
            premadeParsers[lineNumber-1] = (new Parser(line.substring(lineSplitByEqual[0].length() + 1)));
        }
        Parser variableParser = premadeParsers[lineNumber-1];
        if (!safeToCopy) {
            premadeParsers[lineNumber-1] = null;
        }

        //System.out.println(TypeChecker.isCompatible(variableParser.getType(), variableType));
        if (!TypeChecker.isCompatible(variableParser.getType(), variableType)) {
            ErrorManager.printError("Value of type '"+variableParser.getType()+"' cannot be assigned to a variable of type '"+variableType+"' !", "9:2.6");
        }

        Datum variable = variableParser.result();
        variable.setType(variableType);
        variable.setIsMutable(!isConst);

        if (variable instanceof Function) {
            ((Function) variable).setName(variableName);
            ((Function) variable).regenerateFullName();
            functionShortNameList.add(variableName);
            variableName = ((Function) variable).getFullName();
        }

        if (localMemory.isEmpty()) {
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
                ErrorManager.printError("Attempt to mutate a constant, "+varName+"!","9:2.7");
            }
            HashMap<String, Datum> currentLocalMemory = localMemory.peek();
            Datum mutatedResult = new Parser(line.substring(lineSplitByEqual[0].length()+1)).result();
            if (!TypeChecker.isCompatible(mutatedResult.getType(), varToMutate.getType())) {
                ErrorManager.printError("Values of type '"+mutatedResult.getType()+"' are not compatible with variable '"+varName+"' of type '"+varToMutate.getType()+"' !","9:2.8");
            }
            currentLocalMemory.remove(varName);
            currentLocalMemory.put(varName, mutatedResult);
            //localMemory.pop();
            //localMemory.push(currentLocalMemory);
        }
    }
    public static Datum runFunction(Function func, Datum[] arguments, String[] parameterNames) {
        lineNumberStack.push(lineNumber);
        currentObject.push(func.getAssociatedObject());
        currentFunction = func;
        lineNumber = func.getLineNumberLocation()+1;
        HashMap<String, Datum> argumentMap = new HashMap<>();
        for (int i = 0; i<arguments.length; i++) { //Put arguments into local memory
            if (memory.containsKey(parameterNames[i])) {
                ErrorManager.printError("Argument '"+parameterNames[i]+"' is a duplicate!","9:2.1");
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
                ErrorManager.printError("Syntax error on if!", "9:2.9");
            }
        }
        if (!line.trim().endsWith("{")) {
            ErrorManager.printError("Bracket mismatch on if!", "9:2.10");
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
            ErrorManager.printError("Cannot use a non-integer index in for loop!", "9:2.11");
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
    }
    private static void moveOverBracketedCode() {
        int bracketCount = 0;
        lineNumber--;
        do  {
            lineNumber++;
            String currentLine = codeLines[lineNumber-1];
            if (currentLine.endsWith("{") || currentLine.startsWith("for ")) {
                bracketCount++;
            }
            if (currentLine.startsWith("}")) {
                bracketCount--;
            }
            if (currentLine.startsWith("}") && (currentLine.endsWith("{") || currentLine.startsWith("for ")) && bracketCount == 1) {
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

            if (currentLine.endsWith("{") || currentLine.startsWith("for ")) {
                bracketCount++;
            }
            if (currentLine.startsWith("}")) {
                bracketCount--;
            }
            if (!isFirst && currentLine.startsWith("}") && (currentLine.endsWith("{") || currentLine.startsWith("for ")) && bracketCount == 1) {
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
        if (!currentObject.isEmpty()) {
            fullMemory.putAll(currentObject.peek().getProperties());
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
    public static DatumObject getCurrentObject() {
        if (currentObject.isEmpty()) {
            return new DatumObject();
        }
        return currentObject.peek();
    }
    public static ArrayList<String> getFunctionShortNameList() {
        ArrayList<String> functionShortNames = new ArrayList<>();
        for (Datum memoryEntry : getFullMemory().values()) {
            if (memoryEntry instanceof Function) {
                functionShortNames.add(((Function) memoryEntry).getName());
            }
        }
        return functionShortNames;
        //return functionShortNameList;
    }
    public static Function getCurrentFunction() { return currentFunction; }
    public static void setCurrentObject(DatumObject dtmo) {
        currentObject.push(dtmo);
    }
    public static void callUnsafeToCopy() {
        safeToCopy = false;
    }
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
        ErrorManager.printError("Bracket mismatch!","9.1.8");
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
        ErrorManager.printError("Bracket mismatch!","9:1.8");
        return 0;
    }

    public static String[] splitByNakedChar(String s, char c) {
        ArrayList<String> splitResults = new ArrayList<>();
        StringBuilder currentSplit = new StringBuilder();
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
                splitResults.add(currentSplit.toString());
                currentSplit = new StringBuilder();
            } else {
                currentSplit.append(sChar);
            }
        }
        splitResults.add(currentSplit.toString());
        return splitResults.toArray(new String[0]);
    }
    private static boolean isLegalIdentifier(String name) {
        String illegalChars = ".()+-%*/\\{}[]=&|!^<>?,;:\"";
        for (char activeChar : name.toCharArray()) {
            if (illegalChars.contains(""+activeChar)) {
                return false;
            }
        }
        return switch (name) {
            case "var", "func", "if", "for", "while", "return", "else", "elseif", "const", "public", "private", "this", "class" -> false;
            default -> true;
        };
    }
}
