import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class OpLibrary {
    private final static String[] doubleFloat = {"float", "float"};
    private final static String[] singleFloat = {"float"};
    private final static String[] doubleInt = {"int", "int"};
    private final static String[] singleInt = {"int"};
    private final static String[] singleString = {"string"};
    private final static String[] doubleString = {"string"};
    private final static String[] stringInt = {"string","int"};
    private final static String[] doubleBool = {"bool", "bool"};
    private final static String[] singleBool = {"bool"};
    private final static String[] singleAny = {"any"};
    private final static String[] anyArrInt = {"[any]", "int"};
    private final static String[] anyString = {"any", "string"};

    private final static OpFunction addFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "float");
    public final static Operation addition = new Operation(addFunction, doubleFloat, "float", OpPrecedence.ADDITIVE, "addition");
    private final static OpFunction multFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "float");
    public final static Operation multiplication = new Operation(multFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE, "multiplication");
    private final static OpFunction subFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "float");
    public final static Operation subtraction = new Operation(subFunction, doubleFloat, "float", OpPrecedence.ADDITIVE, "subtraction");
    private final static OpFunction divFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!","13.1");
        }
        return new Datum(String.valueOf(Float.parseFloat(args[0].getValue())/Float.parseFloat(args[1].getValue())), "float");
    };
    public final static Operation division = new Operation(divFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE, "division");
    private final static OpFunction passFunction = (args) -> args[0];
    public final static Operation pass = new Operation(passFunction, singleFloat, "float", OpPrecedence.PASS, "pass");

    private final static OpFunction intAddFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())+Float.parseFloat(args[1].getValue())), "int");
    public final static Operation intAddition = new Operation(intAddFunction, doubleInt, "int", OpPrecedence.ADDITIVE, "int add");
    private final static OpFunction intMultFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())*Float.parseFloat(args[1].getValue())), "int");
    public final static Operation intMultiplication = new Operation(intMultFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE, "int mult");
    private final static OpFunction intSubFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())-Float.parseFloat(args[1].getValue())), "int");
    public final static Operation intSubtraction = new Operation(intSubFunction, doubleInt, "int", OpPrecedence.ADDITIVE, "int sub");
    private final static OpFunction intDivFunction = (args) -> {
        if ((int)Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!","13.2");
        }
        return new Datum(String.valueOf((int)Float.parseFloat(args[0].getValue())/(int)Float.parseFloat(args[1].getValue())), "int");
    };
    public final static Operation intDivision = new Operation(intDivFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE, "int div");

    private final static OpFunction intModFunction = (args) -> {
        if ((int)Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!","13.3");
        }
        return new Datum(String.valueOf((int)Float.parseFloat(args[0].getValue())%(int)Float.parseFloat(args[1].getValue())), "int");
    };
    public final static Operation intModulo = new Operation(intModFunction, doubleInt, "int", OpPrecedence.MULTIPLICATIVE, "int mod");

    private final static OpFunction floatModFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!","13.4");
        }
        return new Datum(String.valueOf(Float.parseFloat(args[0].getValue())%Float.parseFloat(args[1].getValue())), "float");
    };
    public final static Operation floatModulo = new Operation(floatModFunction, doubleFloat, "float", OpPrecedence.MULTIPLICATIVE, "float mod");

    private final static OpFunction divisibleFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())==0) {
            ErrorManager.printError("Division by zero!","13.5");
        }
        double remainder = Float.parseFloat(args[0].getValue())%Float.parseFloat(args[1].getValue());
        boolean divisor = (remainder <= 3E-6 && remainder >= -3E-6);
        return new Datum(divisor ? "true" : "false", "bool");
    };
    public final static Operation divisible = new Operation(divisibleFunction, doubleFloat, "bool", OpPrecedence.MULTIPLICATIVE, "divisible");



    private final static OpFunction intPassFunction = (args) -> args[0];
    public final static Operation intPass = new Operation(intPassFunction, singleInt, "int", OpPrecedence.PASS, "int pass");

    private final static OpFunction stringPassFunction = (args) -> args[0];
    public final static Operation stringPass = new Operation(stringPassFunction, singleString, "string", OpPrecedence.PASS, "string pass");
    private final static OpFunction concatenationFunction = (args) -> new Datum(args[0].getValue()+args[1].getValue(), "string");
    public final static Operation concatenation = new Operation(concatenationFunction, doubleString, "string", OpPrecedence.ADDITIVE, "concatenation");

    private final static OpFunction charGetFunction = (args) -> new Datum(""+args[0].getValue().charAt((int)Float.parseFloat(args[1].getValue())), "char");
    public final static Operation charGet = new Operation(charGetFunction, stringInt, "char", OpPrecedence.MULTIPLICATIVE, "char get");

    private final static OpFunction boolPassFunction = (args) -> args[0];
    public final static Operation boolPass = new Operation(boolPassFunction, singleBool, "bool", OpPrecedence.PASS, "bool pass");

    private final static OpFunction strEqualFunction = (args) -> new Datum(args[0].getValue().equals(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation strEquals = new Operation(strEqualFunction, doubleString, "bool", OpPrecedence.COMPARISON, "str equals");

    private final static OpFunction numericEqualFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) == Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation numericEquals = new Operation(numericEqualFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "numeric equals");

    public final static Operation boolEquals = new Operation(strEqualFunction, doubleBool, "bool", OpPrecedence.COMPARISON, "str equals");

    private final static OpFunction greaterThanFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) > Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation greaterThan = new Operation(greaterThanFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "greater than");

    private final static OpFunction lessThanFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) < Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation lessThan = new Operation(lessThanFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "less than");

    private final static OpFunction lessThanOrEqualToFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) <= Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation lessThanOrEqualTo = new Operation(lessThanOrEqualToFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "less than or equal to");

    private final static OpFunction greaterThanOrEqualToFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) >= Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation greaterThanOrEqualTo = new Operation(greaterThanOrEqualToFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "greater than or equal to");

    private final static OpFunction strUnequalFunction = (args) -> new Datum(!args[0].getValue().equals(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation strUnequals = new Operation(strUnequalFunction, doubleString, "bool", OpPrecedence.COMPARISON, "str unequals");

    private final static OpFunction numericUnequalFunction = (args) -> new Datum(Float.parseFloat(args[0].getValue()) != Float.parseFloat(args[1].getValue()) ? "true" : "false","bool");
    public final static Operation numericUnequals = new Operation(numericUnequalFunction, doubleFloat, "bool", OpPrecedence.COMPARISON, "numeric unequals");

    public final static Operation boolUnequals = new Operation(strUnequalFunction, doubleBool, "bool", OpPrecedence.COMPARISON, "str unequals");

    private final static OpFunction logicalConjunctionFunction = (args) -> new Datum(args[0].getValue().equals("true") && args[1].getValue().equals("true") ? "true" : "false", "bool");
    public final static Operation logicalConjunction = new Operation(logicalConjunctionFunction, doubleBool, "bool", OpPrecedence.CONJUNCTIVE, "logical conjunction");

    private final static OpFunction logicalDisjunctionFunction = (args) -> new Datum(args[0].getValue().equals("true") || args[1].getValue().equals("true") ? "true" : "false", "bool");
    public final static Operation logicalDisjunction = new Operation(logicalDisjunctionFunction, doubleBool, "bool", OpPrecedence.CONJUNCTIVE, "logical disjunction");

    private final static OpFunction logicalNegationFunction = (args) -> new Datum(args[0].getValue().equals("false") ? "true" : "false", "bool");
    public final static Operation logicalNegation = new Operation(logicalNegationFunction, singleBool, "bool", OpPrecedence.NEGATION, "logical negation");

    private final static OpFunction stringConversionFunction = (args) -> new Datum(args[0].getValue(), "string");
    public final static Operation stringConversion = new Operation(stringConversionFunction, singleAny, "string", OpPrecedence.FUNCTIONAL, "str","string conversion");

    private final static OpFunction intConversionFunction = (args) -> new Datum(String.valueOf((int)Float.parseFloat(args[0].getValue())), "int");
    public final static Operation intConversion = new Operation(intConversionFunction, singleAny, "int", OpPrecedence.FUNCTIONAL, "int","int conversion");

    private final static OpFunction floatConversionFunction = (args) -> new Datum(String.valueOf(Float.parseFloat(args[0].getValue())), "float");
    public final static Operation floatConversion = new Operation(floatConversionFunction, singleAny, "float", OpPrecedence.FUNCTIONAL, "float","float conversion");

    private final static OpFunction printFunction = (args) -> {
        System.out.print(args[0].getValue());
        return new Datum();
    };
    public final static Operation print = new Operation(printFunction, singleAny, "void", OpPrecedence.FUNCTIONAL, "print");

    private final static OpFunction printlnFunction = (args) -> {
        System.out.println(args[0].getValue());
        return new Datum();
    };
    public final static Operation println = new Operation(printlnFunction, singleAny, "void", OpPrecedence.FUNCTIONAL, "println");
    public final static Operation anyPass = new Operation(printlnFunction, singleAny, "any",  OpPrecedence.PASS,       "any pass");
    private final static OpFunction lengthFunction = (args) -> new Datum(Integer.toString(args[0].getValue().length()),"int");
    public final static Operation lengthOp = new Operation(lengthFunction, singleString, "int", OpPrecedence.FUNCTIONAL, "len");
    private final static OpFunction traceFunction = (args) -> {
        System.out.println("Evaluated expression to: "+args[0].getValue());
        System.out.println("\tFrom line number "+Interpreter.getLineNumber());
        Stack<Integer> stackTrace = Interpreter.getLineNumberStack();
        for (int lineLocation : stackTrace) {
            System.out.println("\tFrom line number "+lineLocation);
        }
        return args[0];
    };
    public final static Operation trace = new Operation(traceFunction, singleAny, "indeterminate", OpPrecedence.FUNCTIONAL, "trace");
    private final static OpFunction assertFunction = (args) -> {
        if (args[0].getValue().equals("false")) {
            ErrorManager.printError("Failed assertion!","13.6");
        }
        return new Datum();
    };

    /*private final static OpFunction functionApplicationOp = (args) -> {
        Datum[] argsForSubfunc = new Datum[args.length-1];
        System.arraycopy(args, 1, argsForSubfunc, 0, args.length - 1);
        return ((Function)args[0]).result(argsForSubfunc);
    };*/

    public final static Operation assertOp = new Operation(assertFunction, singleBool, "void", OpPrecedence.FUNCTIONAL, "assert");
    private final static OpFunction asciiFunction = (args) -> {
        char returnChar = (char) Integer.parseInt(args[0].getValue());
        return new Datum(""+returnChar, "char");
    };
    public final static Operation ascii = new Operation(asciiFunction, singleInt, "char", OpPrecedence.FUNCTIONAL, "ascii");

    private final static OpFunction typeofFunction = (args) -> new Datum(args[0].getType(),"string");
    public final static Operation typeof = new Operation(typeofFunction, singleAny, "string", OpPrecedence.FUNCTIONAL, "typeof");

    public final static OpFunction getElementFunction = (args) -> {
        if (Float.parseFloat(args[1].getValue())%1!=0) {
            ErrorManager.printError("Cannot index an array with a non-integer!","13.7");
        }
        return ((DatumArray)args[0]).getElement((int)Float.parseFloat(args[1].getValue()));
    };
    public static Operation getElementOfType(String type) {
        return new Operation(getElementFunction, anyArrInt, type, OpPrecedence.FUNCTIONAL, "getValue");
    }


    private final static OpFunction inputOp = (args) -> {
        if (!args[0].getValue().equals("")) {
            System.out.println(args[0].getValue());
        }
        String strValue = "";
        Scanner scan = new Scanner(System.in);
        if (scan.hasNextLine()) {
            strValue = scan.nextLine();
        }
        scan.close();
        return new Datum(strValue, "string");
    };


    public final static Operation inputFunction = new Operation(inputOp, singleString, "string", OpPrecedence.FUNCTIONAL, "input");

    private final static OpFunction throwOp = (args) -> {
        ErrorManager.printError(args[0].getValue(),"13.8");
        return null;
    };

    public final static Operation throwFunction = new Operation(throwOp, singleString, "void", OpPrecedence.FUNCTIONAL, "throw");

    public static ArrayList<Operation> prefixFunctions = new ArrayList<>(Arrays.asList(stringConversion, intConversion, floatConversion, print, println, assertOp, ascii, typeof, inputFunction, lengthOp, throwFunction));

    private final static OpFunction dotApplicationFunction = (args) -> args[0].getProperty(args[1].getValue());
    public final static Operation dotApplication = new Operation(dotApplicationFunction, anyString, "indeterminate", OpPrecedence.FUNCTIONAL, "dot application");

}
