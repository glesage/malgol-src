package malgol.ast;

import malgol.node.Token;

public class VariableExpression extends Expression {
    private static int counter = 0;
    public static VariableExpression freshTemporary(String purpose) {
        counter++;
        return new VariableExpression(null, "TEMP_" + purpose + "_" + counter);
    }
	
	
	private final String name;

	public String getName() {
		return name;
	}
	
	public VariableExpression(Token firstToken, String name) {
		super(firstToken);
		this.name = name;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
