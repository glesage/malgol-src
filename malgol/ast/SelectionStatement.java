package malgol.ast;

import malgol.node.Token;

public class SelectionStatement extends Statement {
	private final Expression test;
	private final Statement trueBranch;
	private final Statement falseBranch;
	
	public Expression getTest() {
		return test;
	}
	
	public Statement getTrueBranch() {
		return trueBranch;
	}
	
	public Statement getFalseBranch() {
		return falseBranch;
	}

	public SelectionStatement(Token firstToken, Expression test, Statement trueBranch, Statement falseBranch) {
		super(firstToken);
		this.test = test;
		this.trueBranch = trueBranch;
		this.falseBranch = falseBranch;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
