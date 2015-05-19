package malgol.ast;

import malgol.node.Token;

public class WhileStatement extends Statement {
	private final Expression test;
	private final Statement body;
	
	public Expression getTest() {
		return test;
	}
	
	public Statement getBody() {
		return body;
	}

	public WhileStatement(Token firstToken, Expression test, Statement body) {
		super(firstToken);
		this.test = test;
		this.body = body;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
