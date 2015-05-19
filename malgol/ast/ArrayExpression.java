package malgol.ast;

import malgol.node.Token;

public class ArrayExpression extends Expression
{
    private final Expression array;
    private final Expression index;
    
    public Expression getArray() {
    	return array;
    }
    
    public Expression getIndex() {
    	return index;
    }

	public ArrayExpression(Token t, Expression a, Expression i) {
		super(t);
		array = a;
		index = i;
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return array.toString() + '[' + index.toString() + ']';
	}
}
