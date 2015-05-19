package malgol.ast;

public interface ASTVisitor {
	public void visit(BlockStatement s);

	public void visit(AssignmentStatement s);

	public void visit(SelectionStatement s);

	public void visit(WhileStatement s);

	public void visit(PrintStatement s);

	public void visit(SkipStatement s);

	public void visit(BinaryExpression e);

	public void visit(UnaryExpression e);
	
	public void visit(ArrayExpression e);

	public void visit(VariableExpression e);

	public void visit(ConstantExpression e);

	public void visit(Declaration d);
	
	public void visit(GotoStatement s);
	
	public void visit(OffsetExpression e);
	
	public void visit(DereferenceExpression e);
	
	public void visit(FunctionDefinition d);
	
	public void visit(ReturnStatement s);
	
	public void visit(FunctionCallExpression e);
	
	public void visit(Program p);
	
}