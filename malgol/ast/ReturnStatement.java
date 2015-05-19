package malgol.ast;

import malgol.node.Token;

public class ReturnStatement extends Statement {
	private final Expression exp;
	
	public Expression getExpression() {
		return exp;
	}

	public ReturnStatement(Token firstToken, Expression e) {
		super(firstToken);
		exp = e;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}