public class Function extends Operation {
    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr) {
        super(of, ots, rt, pr);
    }

    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm) {
        super(of, ots, rt, pr, nm);
    }

    public Function(OpFunction of, String[] ots, String rt, OpPrecedence pr, String nm, String fn) {
        super(of, ots, rt, pr, nm, fn);
    }
    public Function(String nm) {
        super();
        super.name = nm;
    }

    @Override
    public Datum result(Datum[] arguments) {
        return Interpreter.runFunction(name);
    }

    public String toString() {
        return super.name;
    }
}
