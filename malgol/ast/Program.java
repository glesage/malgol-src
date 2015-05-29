package malgol.ast;

import java.util.LinkedList;
import java.util.List;

public class Program extends ASTNode {
	private List<FunctionDefinition> functionList;
	
	public Program(List<FunctionDefinition> fL) {
		super(null);
		functionList = fL;
	}

	
	public List<FunctionDefinition> getFunctionList() {
		return functionList;
	}

	@Override
	public void accept(ASTVisitor v) {
		v.visit(this);
	}

}
