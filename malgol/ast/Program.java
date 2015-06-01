package malgol.ast;

import java.util.LinkedList;

public class Program extends ASTNode {
	private LinkedList<FunctionDefinition> functionList;
	
	public Program(LinkedList<FunctionDefinition> fL) {
		super(null);
		functionList = fL;
	}

	
	public LinkedList<FunctionDefinition> getFunctionList() {
		return functionList;
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visit(this);
	}

}
