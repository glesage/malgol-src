/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.transform;

import malgol.ast.GotoStatement;
import malgol.ast.SelectionStatement;
import malgol.ast.SkipStatement;
import malgol.ast.WhileStatement;

/**
 * 
 * @author WMarrero
 */
public class NonStructuredAST2CVisitor extends AST2CVisitor {
	@Override
	public void visit(SelectionStatement s) {
		GotoStatement target = null;
		if (!(s.getFalseBranch() instanceof SkipStatement))
			throw new RuntimeException(
					"Not expecting an else in non structured program");
		if (!(s.getTrueBranch() instanceof GotoStatement))
			throw new RuntimeException(
					"Expecting a goto in the true branch in a non structured program");
		else
			target = (GotoStatement) s.getTrueBranch();

		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("if (");
		s.getTest().accept(this);
		buf.append(") ");
		buf.append("goto ");
		buf.append(target.getTarget().getName());
		buf.append(';');
		buf.append(newLine);
	}

	@Override
	public void visit(WhileStatement s) {
		throw new RuntimeException(
				"Not expecting while statement in a non structured program");
	}

}
