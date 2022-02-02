import java.util.ArrayList;

public class Parser extends Datum {
    private Datum[] arguments;
    private Operation operation;

    public Parser(String expr) {
        //System.out.println("making a parser from "+expr);
        /* REMOVE SPACES FROM THE INPUT EXPRESSION: */
        int exprSize = expr.length();
        boolean inQuote = false;
        String exprWithoutSpaces = "";
        for (int i = 0; i<exprSize; i++) {
            char exprChar = expr.charAt(i);
            if (exprChar=='\"') { //keep track of quotes so spaces aren't removed from quoted string literals
                inQuote=!inQuote;
            }
            if (exprChar!=' ' || inQuote) {
                exprWithoutSpaces+=exprChar;
            }
        }
        expr = exprWithoutSpaces;
        exprSize = expr.length();

        try { //generally catches errors in the parser function because any number of illegal parser expressions could otherwise cause an interpreter crash
            //System.out.println("making a parser of: " + expr);
            if (Interpreter.getFullMemory().containsKey(expr)) { //checks for a variable
                if (!Interpreter.getFullMemory().get(expr).getIsFunction()) {
                    operation = OpLibrary.anyPass;
                    arguments = new Datum[1];
                    arguments[0] = Interpreter.getFullMemory().get(expr);
                    return;
                }
            }
            //System.out.println(Interpreter.getFunctionShortNameList().toString());
            //System.out.println(Interpreter.getFunctionList().toString());
            if (Interpreter.getFunctionShortNameList().contains(expr)) { //checks for a function identifier (NOT A FUNCTION CALL)
                if (Interpreter.getFunctionsByShortName().get(expr)!=1) { //Functions that share names, even if they have different types, can't be used because we can't disambiguate
                    ErrorManager.printError("Function "+expr+" is ambiguous and cannot be used in a first-class context!");
                }
                for (String functionFullName : Interpreter.getFullMemory().keySet()) {
                    if (functionFullName.startsWith(expr+"(")) {
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
                operation = OpLibrary.intPass;
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
                    String functionShortName = expr.split("\\(")[0];

                    if (Interpreter.getFunctionShortNameList().contains(functionShortName)) { //This gets user-defined prefix function calls
                        if (expr.startsWith(functionShortName+"()")) {
                            operation = (Function)Interpreter.getFullMemory().get(functionShortName+"()").clone();
                            arguments = new Datum[0];
                            return;
                        }
                        String argumentList = expr.substring(functionShortName.length()+1, exprSize-1);
                        String[] argumentsStrings = splitByNakedChar(argumentList, ',');
                        arguments = new Datum[argumentsStrings.length];
                        String functionFullName = functionShortName+"(";

                        for (int i = 0; i<argumentsStrings.length; i++) {
                            arguments[i] = new Parser(argumentsStrings[i]).result();

                            functionFullName+=arguments[i].getType()+",";

                        }
                        if (functionFullName.charAt(functionFullName.length()-1) == ',') {
                            functionFullName = functionFullName.substring(0, functionFullName.length() - 1);
                        }
                        functionFullName+=")";
                        operation = (Function)Interpreter.getFullMemory().get(functionFullName).clone();
                        return;
                    }

                    /*for (Function prefixFunction : Interpreter.getFunctionList()) { //OLD FUNCTION IMPLEMENTATION
                        String prefixFunctionName = prefixFunction.getName();
                        if (exprSize>=prefixFunctionName.length()) {
                            if (expr.startsWith(prefixFunctionName+"(")) {
                                operation = prefixFunction;
                                if (expr.startsWith(prefixFunctionName+"()")) {
                                    arguments = new Datum[0];
                                    return;
                                }
                                String argumentList = expr.substring(prefixFunctionName.length()+1, exprSize-1);
                                String[] argumentsStrings = splitByNakedChar(argumentList, ',');
                                arguments = new Datum[argumentsStrings.length];
                                for (int i = 0; i<argumentsStrings.length; i++) {
                                    arguments[i] = new Parser(argumentsStrings[i]);
                                }
                                return;
                            }
                        }
                    }*/
                }

                /*CHECK FOR OPERATORS INSIDE THE FUNCTION*/
                int parenCount = 0;
                int minParenCount = 1;
                boolean allQuote = true;
                for (int i = 0; i < exprSize; i++) {
                    char activeChar = expr.charAt(i);

                    if (activeChar == '(') {
                        parenCount++;
                    }
                    if (minParenCount > parenCount && i != exprSize - 1) {
                        minParenCount = parenCount;
                    }
                    if (activeChar == ')') {
                        parenCount--;
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
                                            case "float", "int":
                                                operation = OpLibrary.numericEquals;
                                                break;
                                            case "char", "string":
                                                operation = OpLibrary.strEquals;
                                                break;
                                            case "bool":
                                                operation = OpLibrary.boolEquals;
                                                break;
                                            default:
                                                ErrorManager.printError("no recognized type for ==!");
                                                break;
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
                                case "!=":
                                    if (minPrecedence.equals(OpPrecedence.COMPARISON)) {
                                        setArgumentsAroundDouble(i, expr);
                                        switch (arguments[0].getType()) {
                                            case "float", "int":
                                                operation = OpLibrary.numericUnequals;
                                                break;
                                            case "char", "string":
                                                operation = OpLibrary.strUnequals;
                                                break;
                                            case "bool":
                                                operation = OpLibrary.boolUnequals;
                                                break;
                                            default:
                                                ErrorManager.printError("no recognized type for '!='!");
                                                break;
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
                            default:
                                break;
                        }
                    }
                }
                if (parenCount != 0) {
                    ErrorManager.printError("Parentheses mismatch!");
                }
                if (minParenCount > 0) { //Checks for fully-paren-wrapped expression and unwraps it
                    Parser setThisTo = new Parser(expr.substring(1, exprSize - 1));
                    this.arguments = setThisTo.arguments;
                    this.operation = setThisTo.operation;
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
            //e.printStackTrace(System.out);
            ErrorManager.printError("Parser syntax error at line "+ Interpreter.getLineNumber()+"! "+e);
        }
    }

    public Parser(Operation op, Datum[] args) {
        super();
        operation = op;
        arguments = args;
    }

    public Datum result() {
        if (operation.getName().equals("any pass")) {
            return arguments[0];
        } else {
            return operation.result(arguments);
        }
    }

    private void setArgumentsAround(int i, String str) {
        arguments = new Datum[2];
        arguments[0] = new Parser(str.substring(0,i));
        arguments[1] = new Parser(str.substring(i+1));
    }

    private void setArgumentsAroundDouble(int i, String str) {
        arguments = new Datum[2];
        arguments[0] = new Parser(str.substring(0,i));
        arguments[1] = new Parser(str.substring(i+2));
    }

    @Override
    public String getValue() {
        return this.result().getValue();
    }

    @Override
    public String getType() {
        if (operation.getName().equals("any pass")) {
            return arguments[0].getType();
        } else {
            return operation.getReturnType();
        }
    }

    private boolean isFloat(String str) {
        try {
            float f = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    private boolean isInt(String str) {
        try {
            float f = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    protected void toConsole(int i) {
        String tab = "\t";
        System.out.println(tab.repeat(i)+"Parser:");
        System.out.println(tab.repeat(i)+" operation: "+operation.getName());
        System.out.println(tab.repeat(i)+" arguments: ");
        for (Datum argument : arguments) {
            argument.toConsole(i+1);
        }
    }
    public void toConsole() {
        toConsole(0);
    }
    private String[] splitByNakedChar(String s, char c) {
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

    @Override
    public boolean getIsFunction() {
        return arguments[0].getIsFunction();
    }
}
