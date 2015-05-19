package malgol.ast;

import malgol.type.Type;
import malgol.node.Token;

public abstract class Expression extends ASTNode{
	private Type type;
	
	public Expression(Token firstToken) {
		super(firstToken);
		type = null;
	}

	public void setType(Type t) {
		if (type != null)
			throw new RuntimeException("FATAL ERROR: Reseting expression type!");
		type = t;
	}

	public Type getType() {
		return type;
	}
}
