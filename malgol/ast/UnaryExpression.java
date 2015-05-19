package malgol.ast;

import malgol.common.Operator;

import malgol.node.Token;

public class UnaryExpression extends Expression {
	private final Operator operation;
	private final Expression expression;

	public Operator getOperator() {
		return operation;
	}
	
	public Expression getExpression() {
		return expression;
	}
	
	public UnaryExpression(Token firstToken, Operator op, Expression expression) {
		super(firstToken);
		this.operation = op;
		this.expression = expression;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
