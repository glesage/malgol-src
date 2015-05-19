/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.ast;

/**
 *
 * @author WMarrero
 */
public class OffsetExpression extends Expression {
    private final Expression location;
    private final Expression offset;

    public OffsetExpression (Expression l, Expression o)
    {
    	super(null);
        if (!l.getType().isLocation()) {
            malgol.util.Error.msg("FATAL ERROR: Bad Offset argument. Contact instructor.");
        }
	this.location = l;
	this.offset = o;
    }
    
    public Expression getLocation() {
    	return location;
    }
    
    public Expression getOffset() {
    	return offset;
    }

    @Override
    public void accept(ASTVisitor v)
    {
	v.visit(this);
    }  
}
