package malgol.ast;

import malgol.node.Token;

public class SkipStatement extends Statement {
	public SkipStatement(Token firstToken) {
		super(firstToken);
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}
