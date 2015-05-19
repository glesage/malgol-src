package malgol.ast;

import malgol.node.Token;

public class AssignmentStatement extends Statement {
	private final Expression left;
	private final Expression right;
	
	public Expression getLeft() {
		return left;
	}
	
	public Expression getRight() {
		return right;
	}

	public AssignmentStatement(Token firstToken, Expression left, Expression right) {
		super(firstToken);
		this.left = left;
		this.right = right;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
