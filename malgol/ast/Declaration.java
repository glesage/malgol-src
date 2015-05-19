package malgol.ast;

import malgol.type.Type;
import malgol.node.Token;

public class Declaration extends ASTNode {

	private String name;
	private Type type;
	
	public String getName() {
		return name;
	}
	
	public Type getType() {
		return type;
	}

	public Declaration(Token token, String n, Type t) {
		super(token);
		name = n;
		type = t;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
