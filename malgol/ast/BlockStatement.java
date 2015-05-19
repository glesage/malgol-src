package malgol.ast;

import java.util.List;
import malgol.node.Token;

public class BlockStatement extends Statement {

	private final List<Statement> statementList;
	private final List<Declaration> declarationList;
	
	public List<Statement> getStatementList() {
		return statementList;
	}
	
	public List<Declaration> getDeclarationList() {
		return declarationList;
	}

	public BlockStatement(Token firstToken, List<Declaration> declarationList, List<Statement> statementList) {
		super(firstToken);
		this.declarationList = declarationList;
		this.statementList = statementList;
	}

	public void accept(ASTVisitor v) {
		v.visit(this);
	}
}