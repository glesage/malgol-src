/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.ast;

/**
 *
 * @author WMarrero
 */
public class DereferenceExpression extends Expression {
    private final Expression location;
    
    public Expression getLocation() {
    	return location;
    }
    
    public DereferenceExpression(Expression e) {
    	super(null);
        location = e;
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }
    
}
