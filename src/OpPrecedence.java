public enum OpPrecedence {
    PASS, DISJUNCTIVE, CONJUNCTIVE, NEGATION, COMPARISON, ADDITIVE, MULTIPLICATIVE, EXPONENTIAL, FUNCTIONAL, FINAL;
    //PASS, FUNCTIONAL, EXPONENTIAL, MULTIPLICATIVE, ADDITIVE, COMPARISON, NEGATION, CONJUNCTIVE, DISJUNCTIVE, FINAL;
    public OpPrecedence incremented() {
        if (this.ordinal()==OpPrecedence.values().length-1) {
            return OpPrecedence.values()[this.ordinal()];
        }
        return OpPrecedence.values()[this.ordinal()+1];
    }
}
