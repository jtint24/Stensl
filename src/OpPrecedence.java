public enum OpPrecedence {
    PASS, EXPONENTIAL, MULTIPLICATIVE, ADDITIVE, COMPARISON, NEGATION, CONJUNCTIVE, DISJUNCTIVE, FINAL;
    public OpPrecedence incremented() {
        if (this.ordinal()==OpPrecedence.values().length-1) {
            return OpPrecedence.values()[this.ordinal()];
        }
        return OpPrecedence.values()[this.ordinal()+1];
    }
}
