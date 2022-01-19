public class Operation {
    private int operandNum;
    private OpFunction functionality;
    private String[] operandTypes;
    public Operation(OpFunction of, int on, String[] ots) {
        functionality = of;
        operandNum = on;
        operandTypes = ots;
    }
    public Datum result(Datum[] arguments) {
        return functionality.result(arguments);
    }
}
