public class Function extends Operation {
    private int lineNumberLocation = 0;
    private String[] parameterNames = new String[operandNum];

    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr) {
        super(of, ots, rt, pr);
    }

    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm) {
        super(of, ots, rt, pr, nm);
    }

    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm, String fn) {
        super(of, ots, rt, pr, nm, fn);
    }

    public Function (String[] ots, String[] pn, String rt, String nm, String fn, int ln) {
        super((args) -> {return args[0];}, ots, rt, OpPrecedence.FUNCTIONAL, nm, fn);
        parameterNames = pn;
        super.name = nm;
        lineNumberLocation = ln;
    }

    public Function(String nm) {
        super();
        super.name = nm;
        if (Interpreter.getCurrentFunction() == null) {
            super.fullName = name+"§main";
        } else {
            super.fullName = name + "§" + Interpreter.getCurrentFunction().getFullName();
        }
    }

    @Override
    public Datum result(Datum[] arguments) {

        for (int i = 0; i<operandNum; i++) {
            if (!arguments[i].getType().equals(operandTypes[i]) && !(operandTypes[i].equals("any")) && !(arguments[i].getType().equals("int") && operandTypes[i].equals("float")) && !(arguments[i].getType().equals("char") && operandTypes[i].equals("string"))) {
                ErrorManager.printError("Type mismatch! Given type "+arguments[i].getType()+" does not match expected type "+operandTypes[i]+" in argument "+(i+1)+" of operation: "+name);
            }
        }
        System.out.println("getting result...");
        return Interpreter.runFunction(this, arguments, parameterNames);
    }

    @Override
    public String toString() {
        return super.name;
    }

    @Override
    public String getName() {
        return name==null ? "no name" : name;
    }

    public int getLineNumberLocation() { return lineNumberLocation; }
}
