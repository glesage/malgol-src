/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.ast;

import malgol.common.Label;
import malgol.node.Token;

/**
 * 
 * @author WMarrero
 */
public class GotoStatement extends Statement {
	private final Label target;
	
	public Label getTarget() {
		return target;
	}

	public GotoStatement(Token firstToken, Label l) {
		super(null);
		target = l;
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}