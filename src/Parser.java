public class Parser extends Datum {
    private Datum[] arguments;
    private Operation operation;

    public Parser(String expr) {
        if (isNumeric(expr)) {
            operation = OpLibrary.pass;
            arguments = new Datum[1];
            arguments[0] = new Datum(expr,"float");
        }


        int parenCount = 0;
        boolean inQuote = false;

        int exprSize = expr.length();
        for (int i = 0; i<exprSize; i++) {
            char activeChar = expr.charAt(i);

            if (activeChar=='(') {
                parenCount++;
            }
            if (activeChar==')') {
                parenCount--;
            }
            if (activeChar=='\"') {
                inQuote=!inQuote;
            }

            if (parenCount==0 && !inQuote) {
                switch(activeChar) {
                    case '+':
                        setArgumentsAround(i,expr);
                        operation = OpLibrary.addition;
                        break;
                    case '-':
                        setArgumentsAround(i,expr);
                        operation = OpLibrary.subtraction;
                        break;
                    case '*':
                        operation = OpLibrary.multiplication;
                        setArgumentsAround(i,expr);
                        break;
                    case '/':
                        operation = OpLibrary.division;
                        setArgumentsAround(i,expr);
                        break;
                    default:
                        break;
                }
            }

        }
    }

    public Parser(Operation op, Datum[] args) {
        super();
        operation = op;
        arguments = args;
    }
    public Datum result() {
        return operation.result(arguments);
    }
    public void setArgumentsAround(int i, String str) {
        arguments = new Datum[2];
        arguments[0] = new Parser(str.substring(0,i));
        arguments[1] = new Parser(str.substring(i+1));
        System.out.println("parsing around: "+str.substring(0,i)+", "+str.substring(i+1));
    }
    @Override
    public String getValue() {
        return this.result().getValue();
    }

    private boolean isNumeric(String str) {
        try {
            float f = Float.parseFloat(str);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
