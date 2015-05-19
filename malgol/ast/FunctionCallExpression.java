package malgol.ast;

import java.util.LinkedList;
import malgol.node.Token;

public class FunctionCallExpression extends Expression {
    private final String name;
    private final LinkedList<Expression> arguments;

    public FunctionCallExpression(Token t, String n, LinkedList<Expression> a) {
    	super(t);
        name = n;
        arguments = a;
    }
    
    public String getName() {
    	return name;
    }
    
    public LinkedList<Expression> getArguments() {
    	return arguments;
    }

    @Override
    public void accept(ASTVisitor v) {
        v.visit(this);
    }
}
