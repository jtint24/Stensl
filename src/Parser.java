import java.util.ArrayList;

public class Parser extends Datum {
    private Datum[] arguments;
    private Operation operation;

    public Parser(String expr) {
        //System.out.println("making a parser from "+expr);
        /* REMOVE SPACES FROM THE INPUT EXPRESSION: */
        int exprSize = expr.length();
        boolean inQuote = false;
        StringBuilder exprWithoutSpaces = new StringBuilder();
        for (int i = 0; i<exprSize; i++) {
            char exprChar = expr.charAt(i);
            if (exprChar=='\"') { //keep track of quotes so spaces aren't removed from quoted string literals
                inQuote=!inQuote;
            }
            if (exprChar!=' ' || inQuote) {
                exprWithoutSpaces.append(exprChar);
            }
        }
        expr = exprWithoutSpaces.toString();
        exprSize = expr.length();

        try { //generally catches errors in the parser function because any number of illegal parser expressions could otherwise cause an interpreter crash
            //System.out.println("making a parser of: " + expr);
            if (Interpreter.getFullMemory().containsKey(expr)) { //checks for a variable
                if (!Interpreter.getFullMemory().get(expr).getIsFunction()) {
                    operation = OpLibrary.anyPass;
                    arguments = new Datum[1];
                    arguments[0] = Interpreter.getFullMemory().get(expr);

                    Interpreter.callUnsafeToCopy();
                    return;
                }
            }
            //System.out.println(Interpreter.getFunctionShortNameList().toString());
            //System.out.println(Interpreter.getFunctionList().toString());
            if (Interpreter.getFunctionShortNameList().contains(expr)) { //checks for a function identifier (NOT A FUNCTION CALL)
                if (Interpreter.getFunctionsByShortName().get(expr)!=1) { //Functions that share names, even if they have different types, can't be used because we can't disambiguate
                    ErrorManager.printError("Function '"+expr+"' is ambiguous and cannot be used in a first-class context!","15:2.1");
                }
                for (String functionFullName : Interpreter.getFullMemory().keySet()) {
                    if (functionFullName.startsWith(expr+"(")) {
                        Interpreter.callUnsafeToCopy();
                        operation = OpLibrary.anyPass;
                        arguments = new Datum[1];
                        arguments[0] = Interpreter.getFullMemory().get(functionFullName).clone();
                        return;
                    }
                }
            }
            if (expr.equals("true")) { //Checks for true bool literal
                operation = OpLibrary.boolPass;
                arguments = new Datum[1];
                arguments[0] = new Datum("true", "bool");
                return;
            }
            if (expr.equals("false")) { //Checks for false bool literal
                operation = OpLibrary.boolPass;
                arguments = new Datum[1];
                arguments[0] = new Datum("false", "bool");
                return;
            }
            if (isInt(expr)) { //checks for int literal
                operation = OpLibrary.anyPass;
                arguments = new Datum[1];
                arguments[0] = new Datum(expr, "int");
                return;
            }
            if (isFloat(expr)) { //checks for float literal (must be done after int bc all ints are floats)
                operation = OpLibrary.pass;
                arguments = new Datum[1];
                arguments[0] = new Datum(expr, "float");
                return;
            }

            OpPrecedence minPrecedence = OpPrecedence.PASS; //scan the lowest Op precedence and then move up

            /*CHECKS FOR PREFIX FUNCTIONS:*/
            while (minPrecedence.ordinal() < OpPrecedence.values().length - 1) {
                if (minPrecedence.equals(OpPrecedence.NEGATION)) { // negation has special rules in that it doesn't use parens, so it gets its own block
                    if (expr.charAt(0) == '!') {
                        operation = OpLibrary.logicalNegation;
                        arguments = new Datum[1];
                        arguments[0] = new Parser(expr.substring(1));
                        return;
                    }
                }
                if (expr.length()>2) {
                    String exprSubstring = expr.substring(0, expr.length() - 2);
                    if (Interpreter.getClasses().containsKey(exprSubstring) && expr.endsWith("()")) {
                        operation = OpLibrary.anyPass;
                        arguments = new Datum[1];
                        arguments[0] = new DatumObject(Interpreter.getClasses().get(exprSubstring));
                        return;
                    }
                }

                if (minPrecedence.equals(OpPrecedence.FUNCTIONAL)) { // this checks for all prefix function calls
                    for (Operation prefixFunction : OpLibrary.prefixFunctions) { //This gets built-in prefix function calls
                        String prefixFunctionName = prefixFunction.getName();
                        if (exprSize>=prefixFunctionName.length()) {
                            if (expr.startsWith(prefixFunctionName+"(")) {
                                operation = prefixFunction;
                                arguments = new Datum[1];
                                arguments[0] = new Parser(expr.substring(prefixFunctionName.length()));
                                return;
                            }
                        }
                    }
                    int functionShortNameLength = expr.length()-1;
                    int fsnlParenCount = 0;
                    boolean fsnlInQuotes = false;
                    do {
                        switch (expr.charAt(functionShortNameLength)) {
                            case ')':
                                if (!fsnlInQuotes) {
                                    fsnlParenCount++;
                                }
                                break;
                            case '(':
                                if (!fsnlInQuotes) {
                                    fsnlParenCount--;
                                }
                                break;
                            case '"':
                                fsnlInQuotes = !fsnlInQuotes;
                                break;
                        }
                        functionShortNameLength--;
                        if (functionShortNameLength==-1) {
                            ErrorManager.printError("Syntax error on expression parser!", "15:2.2");
                        }
                    } while (fsnlParenCount != 0);

                    String functionShortName = expr.substring(0,functionShortNameLength+1);

                    if (Interpreter.getFunctionShortNameList().contains(functionShortName)) { //This gets user-defined prefix function calls
                        Interpreter.callUnsafeToCopy();
                        if (expr.startsWith(functionShortName+"()")) {
                            if (Interpreter.getFullMemory().containsKey(functionShortName+"()")) {
                                operation = (Function) Interpreter.getFullMemory().get(functionShortName + "()").clone();
                            } else {
                                operation  = (Function) Interpreter.getFullMemory().get(functionShortName).clone();
                            }
                            arguments = new Datum[0];
                            return;
                        }
                        String argumentList = expr.substring(functionShortName.length()+1, exprSize-1);
                        String[] argumentsStrings = splitByNakedChar(argumentList, ',');
                        arguments = new Datum[argumentsStrings.length];
                        StringBuilder functionFullName = new StringBuilder(functionShortName + "(");

                        for (int i = 0; i<argumentsStrings.length; i++) {
                            Interpreter.callUnsafeToCopy();
                            arguments[i] = new Parser(argumentsStrings[i]).result();
                            functionFullName.append(arguments[i].getType()).append(",");
                        }
                        if (functionFullName.charAt(functionFullName.length()-1) == ',') {
                            functionFullName = new StringBuilder(functionFullName.substring(0, functionFullName.length() - 1));
                        }
                        functionFullName.append(")");
                        if (!Interpreter.getFullMemory().containsKey(functionFullName.toString())) {
                            ErrorManager.printError("Function "+functionFullName+" does not exist!","15:2.3");
                        }
                        operation = (Function)Interpreter.getFullMemory().get(functionFullName.toString()).clone();
                        return;
                    }
                }

                /*CHECK FOR OPERATORS INSIDE THE FUNCTION*/
                int parenCount = 0;
                int minParenCount = 1;
                int bracketCount = 0;
                int minBracketCount = 1;
                int lastBracketIndex = 0;
                boolean allQuote = true;
                for (int i = 0; i < exprSize; i++) {
                    char activeChar = expr.charAt(i);

                    if (activeChar == '(' && !inQuote) {
                        parenCount++;
                    }
                    if (minParenCount > parenCount && i != exprSize - 1) {
                        minParenCount = parenCount;
                    }
                    if (activeChar == ')' && !inQuote) {
                        parenCount--;
                    }
                    if (activeChar == '[' && !inQuote && i != exprSize - 1) {
                        bracketCount++;
                        lastBracketIndex = i;
                    }
                    if (minBracketCount > bracketCount) {
                        minBracketCount = bracketCount;
                    }
                    if (activeChar == ']' && !inQuote) {
                        bracketCount--;
                    }
                    if (activeChar == '\"') {
                        inQuote = !inQuote;
                    }
                    if (!inQuote && i != exprSize - 1) {
                        allQuote = false;
                    }


                    if (parenCount == 0 && !inQuote) {
                        if (i < exprSize - 1) { //CHECK FOR TWO-CHAR INFIX OPERATORS
                            String dual = "" + activeChar + "" + expr.charAt(i + 1);
                            switch (dual) {
                                case "==":
                                    if (minPrecedence.equals(OpPrecedence.COMPARISON)) {
                                        setArgumentsAroundDouble(i, expr);
                                        switch (arguments[0].getType()) {
                                            case "float", "int" -> operation = OpLibrary.numericEquals;
                                            case "char", "string" -> operation = OpLibrary.strEquals;
                                            case "bool" -> operation = OpLibrary.boolEquals;
                                            default -> ErrorManager.printError("No recognized type for '==' !","15:2.4");
                                        }
                                        return;
                                    }
                                case ">=":
                                    if (minPrecedence == OpPrecedence.COMPARISON) {
                                        setArgumentsAroundDouble(i, expr);
                                        operation = OpLibrary.greaterThanOrEqualTo;
                                        return;
                                    }
                                    break;
                                case "<=":
                                    if (minPrecedence == OpPrecedence.COMPARISON) {
                                        setArgumentsAroundDouble(i, expr);
                                        operation = OpLibrary.lessThanOrEqualTo;
                                        return;
                                    }
                                    break;
                                case "&&":
                                    if (minPrecedence == OpPrecedence.CONJUNCTIVE) {
                                        //System.out.println("conjunctive made");
                                        setArgumentsAroundDouble(i, expr);
                                        operation = OpLibrary.logicalConjunction;
                                        return;
                                    }
                                    break;
                                case "||":
                                    if (minPrecedence == OpPrecedence.DISJUNCTIVE) {
                                        //System.out.println("disjunctive made");
                                        setArgumentsAroundDouble(i, expr);
                                        operation = OpLibrary.logicalDisjunction;
                                        return;
                                    }
                                    break;
                                case "%%":
                                    if (minPrecedence == OpPrecedence.COMPARISON) {
                                        //System.out.println("disjunctive made");
                                        setArgumentsAroundDouble(i, expr);
                                        operation = OpLibrary.divisible;
                                        return;
                                    }
                                    break;
                                case "!=":
                                    if (minPrecedence.equals(OpPrecedence.COMPARISON)) {
                                        setArgumentsAroundDouble(i, expr);
                                        switch (arguments[0].getType()) {
                                            case "float", "int" -> operation = OpLibrary.numericUnequals;
                                            case "char", "string" -> operation = OpLibrary.strUnequals;
                                            case "bool" -> operation = OpLibrary.boolUnequals;
                                            default -> ErrorManager.printError("No recognized type for '!=' !","15:2.5");
                                        }
                                        return;
                                    }
                                default:
                                    break;
                            }
                        }
                        switch (activeChar) { //Check for single character infix operators
                            case '<':
                                if (minPrecedence == OpPrecedence.COMPARISON) {
                                    setArgumentsAround(i, expr);
                                    operation = OpLibrary.lessThan;
                                    return;
                                }
                                break;
                            case '>':
                                if (minPrecedence == OpPrecedence.COMPARISON) {
                                    setArgumentsAround(i, expr);
                                    operation = OpLibrary.greaterThan;
                                    return;
                                }
                                break;
                            case '+':
                                if (minPrecedence.equals(OpPrecedence.ADDITIVE)) {
                                    setArgumentsAround(i, expr);
                                    if (arguments[0].getType().equals("float") || arguments[1].getType().equals("float")) {
                                        operation = OpLibrary.addition;
                                    } else {
                                        operation = OpLibrary.intAddition;
                                        //System.out.println("The value on the left is "+arguments[0].getValue());
                                    }
                                    return;
                                }
                                break;
                            case '-':
                                if (minPrecedence.equals(OpPrecedence.ADDITIVE) && i > 0) { //The extra && i>0 ensures that leading negative numbers don't trigger as subtraction
                                    if (!"(*/!=^%&".contains("" + expr.charAt(i - 1))) {
                                        setArgumentsAround(i, expr);
                                        if (arguments[0].getType().equals("float") || arguments[1].getType().equals("float")) {
                                            operation = OpLibrary.subtraction;
                                        } else {
                                            operation = OpLibrary.intSubtraction;
                                        }
                                        return;
                                    }
                                }
                                break;
                            case '*':
                                if (minPrecedence.equals(OpPrecedence.MULTIPLICATIVE)) {
                                    setArgumentsAround(i, expr);
                                    if (arguments[0].getType().equals("float") || arguments[1].getType().equals("float")) {
                                        operation = OpLibrary.multiplication;
                                    } else {
                                        operation = OpLibrary.intMultiplication;
                                    }
                                    return;
                                }
                                break;
                            case '/':
                                if (minPrecedence.equals(OpPrecedence.MULTIPLICATIVE)) {
                                    setArgumentsAround(i, expr);
                                    if (arguments[0].getType().equals("float") || arguments[1].getType().equals("float")) {
                                        operation = OpLibrary.division;
                                    } else {
                                        operation = OpLibrary.intDivision;
                                    }
                                    return;
                                }
                                break;
                            case '%':
                                if (minPrecedence.equals(OpPrecedence.MULTIPLICATIVE)) {
                                    setArgumentsAround(i, expr);
                                    if (arguments[0].getType().equals("float") || arguments[1].getType().equals("float")) {
                                        operation = OpLibrary.floatModulo;
                                    } else {
                                        operation = OpLibrary.intModulo;
                                    }
                                    return;
                                }
                                break;
                            case '&':
                                if (minPrecedence.equals(OpPrecedence.ADDITIVE)) {
                                    if (expr.charAt(i + 1) != '&' && expr.charAt(i - 1) != '&') {
                                        setArgumentsAround(i, expr);
                                        operation = OpLibrary.concatenation;
                                        return;
                                    }
                                }
                                break;
                            case '$':
                                if (minPrecedence.equals(OpPrecedence.MULTIPLICATIVE)) {
                                    setArgumentsAround(i, expr);
                                    operation = OpLibrary.charGet;
                                    return;
                                }
                                break;
                            case '|':
                                if (minPrecedence.equals(OpPrecedence.FUNCTIONAL)) {
                                    setArgumentsAround(i, expr);
                                    Interpreter.callUnsafeToCopy();
                                    operation = (Function)((Parser)arguments[0]).result();
                                    Datum opInput = arguments[1];
                                    arguments = new Datum[1];
                                    arguments[0] = ((Parser)opInput).result();
                                    return;
                                }
                                break;
                            case '.':
                                if (minPrecedence.equals(OpPrecedence.FUNCTIONAL)) {
                                    arguments = new Datum[2];
                                    Interpreter.callUnsafeToCopy();
                                    arguments[0] = new Parser(expr.substring(0,i)).result();
                                    arguments[1] = new Datum(expr.substring(i+1), "string");
                                    operation = OpLibrary.dotApplication;
                                    return;
                                }
                            default:
                                break;
                        }
                    }
                    if (parenCount<0) {
                        ErrorManager.printError("Parentheses mismatch!","15:1.1");
                    }
                    if (bracketCount<0) {

                        ErrorManager.printError("Bracket mismatch!","15:1.2");
                    }
                }
                if (parenCount != 0) {
                    ErrorManager.printError("Parentheses mismatch!","15:1.3");
                }
                if (bracketCount != 0) {
                    ErrorManager.printError("Bracket mismatch! "+bracketCount,"15:1.4");
                }
                if (minParenCount > 0) { //Checks for fully-paren-wrapped expression and unwraps it
                    Parser setThisTo = new Parser(expr.substring(1, exprSize - 1));
                    this.arguments = setThisTo.arguments;
                    this.operation = setThisTo.operation;
                    return;
                }
                if (minBracketCount > 0) { //Checks for array literals
                    String unwrappedExpr = expr.substring(1,exprSize-1);
                    String[] elementsStringList = splitByNakedChar(unwrappedExpr, ',');
                    Datum[] elementsDatums = new Datum[elementsStringList.length];
                    String elementType = "";
                    for (int i = 0; i<elementsDatums.length; i++) {
                        Interpreter.callUnsafeToCopy();
                        elementsDatums[i] = (new Parser(elementsStringList[i])).result();
                        if (i == 0) {
                            elementType = elementsDatums[i].getType();
                        } else {
                            if (!elementType.equals(elementsDatums[i].getType())) {
                                ErrorManager.printError("Cannot create an array of conflicting types "+elementType+" and "+elementsDatums[i].getType()+"!","15:2.6");
                            }
                        }
                    }
                    arguments = new Datum[1];
                    arguments[0] = new DatumArray(elementType, elementsDatums);
                    operation = OpLibrary.anyPass;
                    return;
                }
                if (expr.charAt(expr.length()-1) == ']' && lastBracketIndex != 0) { //check for array element-gets
                    String indexString = expr.substring(lastBracketIndex+1, expr.length()-1);
                    String arrayString = expr.substring(0, lastBracketIndex);
                    Interpreter.callUnsafeToCopy();
                    Datum index = (new Parser(indexString)).result();
                    Datum array = (new Parser(arrayString)).result();
                    operation = OpLibrary.getElementOfType(TypeChecker.unwrapArray(array.getType()));
                    arguments = new Datum[2];
                    arguments[0] = array;
                    arguments[1] = index;
                    return;
                }
                if (allQuote) { //Checks for quote literal
                    this.arguments = new Datum[1];
                    arguments[0] = new Datum(expr.substring(1, exprSize - 1), "string");
                    this.operation = OpLibrary.stringPass;
                    return;
                }

                minPrecedence = minPrecedence.incremented();
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            ErrorManager.printError("Parser syntax error! ","15:2.7");
        }
    }

    public Parser(Operation op, Datum[] args) {
        super();
        operation = op;
        arguments = args;
    }

    /**
     * result
     *
     * Returns the result of the Parser's expression
     *
     * @return The Datum holding the result of the expression
     * */

    public Datum result() {
        if (operation.getName().equals("any pass")) {
            return arguments[0];
        } else {
            return operation.result(arguments);
        }
    }

    /**
     * setArgumentsAround
     *
     * Sets the arguments of a Parser to be the substrings on either side of a character lying at a particular
     * index in a String
     *
     * @param i The index at which the character to split around lies
     * @param str The String to split around
     * */

    private void setArgumentsAround(int i, String str) {
        arguments = new Datum[2];
        arguments[0] = new Parser(str.substring(0,i));
        arguments[1] = new Parser(str.substring(i+1));
    }

    /**
     * setArgumentsAroundDouble
     *
     * Sets the arguments of a Parser to be the substring on either side of a pair of characters that begin
     * at a particular index in a String
     *
     * @param i The index at which the character pair starts
     * @param str The String to split around
     * */

    private void setArgumentsAroundDouble(int i, String str) {
        arguments = new Datum[2];
        arguments[0] = new Parser(str.substring(0,i));
        arguments[1] = new Parser(str.substring(i+2));
    }

    /**
     * getValue
     *
     * Gets the value of the result of the Parser formatted as a String
     *
     * @return The value of the result of this Parser as a String
     * */

    @Override
    public String getValue() {
        return this.result().getValue();
    }

    /**
     * getType
     *
     * Returns the type of the result of the Parser
     *
     * @return The type of the parse as a String
     * */

    @Override
    public String getType() {
        if (operation.getReturnType().equals("indeterminate")) {
            return result().getType();
        } else if (operation.getName().equals("any pass")) {
            return arguments[0].getType();
        } else {
            return operation.getReturnType();
        }
    }

    /**
     * isFloat
     *
     * Checks if a String represents a float
     *
     * @param str The String in question
     * @return Whether str represents a float or not
     * */

    private boolean isFloat(String str) {
        try {
            float f = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * isInt
     *
     * Checks if a String represents an int
     *
     * @param str The String in question
     * @return Whether str represents an int or not
     * */

    private boolean isInt(String str) {
        try {
            int i = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * toConsole
     *
     * Prints the contents of the Parser to the console with a custom indentation level
     *
     * @param i The indentation level to print at
     * */

    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Parser:");
        System.out.println(tab.repeat(i)+" operation: "+operation.getName());
        System.out.println(tab.repeat(i)+" arguments: ");
        for (Datum argument : arguments) {
            argument.toConsole(i+1);
        }
    }

    /**
     * toConsole
     *
     * Prints the contents of the Parser to the console with no indentation
     * */

    public void toConsole() {
        toConsole(0);
    }

    /**
     * splitByNakedChar
     *
     * Splits a String by a particular character, so long as that character is not inside quotes, parentheses,
     * or brackets
     *
     * @param s The String to split
     * @param c The char to split by
     * @return An array of all the split substrings
     * */

    private String[] splitByNakedChar(String s, char c) {
        ArrayList<String> splitResults = new ArrayList<>();
        StringBuilder currentSplit = new StringBuilder();
        int parenCount = 0;
        int bracketCount = 0;
        boolean inQuotes = false;
        int sLength = s.length();
        for (int i = 0; i<sLength; i++) {
            char sChar = s.charAt(i);
            if (sChar == '"') {
                inQuotes = ! inQuotes;
            }
            if (sChar == '(' && !inQuotes) {
                parenCount++;
            }
            if (sChar == ')' && !inQuotes) {
                parenCount--;
            }
            if (sChar == '[' && !inQuotes) {
                bracketCount++;
            }
            if (sChar == ']' && !inQuotes) {
                bracketCount--;
            }
            if (parenCount == 0 && bracketCount==0 && !inQuotes && sChar == c) {
                splitResults.add(currentSplit.toString());
                currentSplit = new StringBuilder();
            } else {
                currentSplit.append(sChar);
            }
        }
        splitResults.add(currentSplit.toString());
        return splitResults.toArray(new String[0]);
    }

    /**
     * getIsFunction
     *
     * Returns whether or not the Parser represents a function
     *
     * @return A boolean representing whether the Parser represents a function
     * */

    @Override
    public boolean getIsFunction() {
        return arguments[0].getIsFunction();
    }
}
