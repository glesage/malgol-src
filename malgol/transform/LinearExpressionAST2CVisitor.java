package malgol.transform;

import malgol.ast.*;

public class LinearExpressionAST2CVisitor extends NonStructuredAST2CVisitor {
	
	@Override
	public void visit(ArrayExpression e) {
		throw new RuntimeException("Array expression found in linearized expression AST");
	}
	
	@Override
	public void visit(AssignmentStatement s) {
		Expression left = s.getLeft();
		if (!(left instanceof DereferenceExpression) && !(left instanceof VariableExpression))
			throw new RuntimeException("Complex left argument in assignment in linearized AST");
		super.visit(s);
	}
	
	@Override
	public void visit(BinaryExpression e) {
		Expression left = e.getLeft();
		Expression right = e.getRight();
		if (!(left instanceof ConstantExpression) && !(left instanceof VariableExpression)  && !(left instanceof DereferenceExpression))
			throw new RuntimeException("Complex left argument in binary expression in linearized AST");
		if (!(right instanceof ConstantExpression) && !(right instanceof VariableExpression) && !(right instanceof DereferenceExpression))
			throw new RuntimeException("Complex right argument in binary expression in linearized AST");
		super.visit(e);
	}
	
	@Override
	public void visit(DereferenceExpression e) {
		Expression location = e.getLocation();
		if (!(location instanceof VariableExpression))
			throw new RuntimeException("Complex argument to dereference expression in linearized AST");
		super.visit(e);
	}
	
	@Override
	public void visit(OffsetExpression e) {
		Expression location = e.getLocation();
		Expression offset = e.getOffset();
		if (!(location instanceof ConstantExpression) && !(location instanceof VariableExpression)) {
			throw new RuntimeException("Complex left argument in offset expression in linearized AST");
		}
		if (!(offset instanceof ConstantExpression) && !(offset instanceof VariableExpression))
			throw new RuntimeException("Complex right argument in offset expression in linearized AST");
		super.visit(e);
	}
	
	@Override
	public void visit(PrintStatement s) {
		Expression expression = s.getExpression();
		if (!(expression instanceof ConstantExpression) && !(expression instanceof VariableExpression)
				&& !(expression instanceof DereferenceExpression))
			throw new RuntimeException("Complex argument in print statement in linearized AST");
		super.visit(s);
	}
	
	@Override
	public void visit(SelectionStatement s) {
		Expression test = s.getTest();
		if (!(test instanceof ConstantExpression) && !(test instanceof VariableExpression))
			throw new RuntimeException("Complex test in selection statement in linearized AST");
		super.visit(s);
	}
	
	@Override
	public void visit(UnaryExpression e) {
		Expression expression = e.getExpression();
		if (!(expression instanceof ConstantExpression) && !(expression instanceof VariableExpression))
			throw new RuntimeException("Complex argument in unary expression in linearized AST");
		super.visit(e);
	}
	
}
