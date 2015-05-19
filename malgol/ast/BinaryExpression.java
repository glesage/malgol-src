package malgol.ast;

import malgol.common.Operator;
import malgol.node.Token;

public class BinaryExpression extends Expression {

	private final Operator operation;
	private final Expression left;
	private final Expression right;
	
	public BinaryExpression(Token firstToken, Operator op, Expression left, Expression right) {
		super(firstToken);
		this.operation = op;
		this.left = left;
		this.right = right;
	}
	
	public Operator getOperator() {
		return operation;
	}
	
	public Expression getLeft() {
		return left;
	}
	
	public Expression getRight() {
		return right;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
