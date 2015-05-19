package malgol.common;

public enum Operator {
    PLUS("+"),
    MINUS("-"),
    TIMES("*"),
    DIVIDE("/"),
    MOD("%"),
    LESSTHAN("<"),
    GREATERTHAN(">"),
    LEQ("<="),
    GEQ(">="),
    EQUALS("=="),
    BANGEQUALS("!="),
    AMP("&"),
    CARET("^"),
    BAR("|"),
    AND("&&"),
    OR("||"),
    NOT("!"),
    UMINUS("-");
    
    final String string;

    Operator(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }
    
    public boolean isArithmetic() {
    	return (ordinal() <= MOD.ordinal()) || (this == UMINUS);
    }

    public boolean isRelational() {
        return ordinal() >= LESSTHAN.ordinal()
                && ordinal() <= BANGEQUALS.ordinal();
    }
    
    public boolean isBoolean() {
    	return (ordinal() >= AND.ordinal()) && (ordinal() <= NOT.ordinal());
    }

    public boolean isBinary() {
        return ordinal() < UMINUS.ordinal();
    }

    public boolean isUnary() {
        return ordinal() >= NOT.ordinal();
    }
}
