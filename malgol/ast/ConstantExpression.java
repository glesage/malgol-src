package malgol.ast;

import malgol.type.*;
import malgol.node.Token;

public class ConstantExpression extends Expression {
	private final int value;
	
	public int getValue() {
		return value;
	}

	public ConstantExpression(Token firstToken, Integer value) {
		super(firstToken);
		this.value = value.intValue();
		setType(IntType.singleton());
	}

	public ConstantExpression(Token firstToken, Boolean value) {
		super(firstToken);
		this.value = value ? 1 : 0;
		setType(BoolType.singleton());
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}