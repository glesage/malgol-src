package malgol.ast;

import malgol.node.Token;

public abstract class ASTNode {
	private final Token tok;
	private static final PrettyPrintVisitor v = new PrettyPrintVisitor();
	
	public ASTNode(Token t) {
		tok = t;
	}
	
	public int getLine() {
		return tok.getLine();
	}
	
	public int getCol() {
		return tok.getPos();
	}
	
	public Token getFirstToken() {
		return tok;
	}
	
	public String getPositionString() {
		return "line: " + tok.getLine() + "    pos: " + tok.getPos();
	}
	
	public String toString() {
		v.clear();
		accept(v);
		return v.getResult();
	}
	
	public abstract void accept(ASTVisitor v);
}
