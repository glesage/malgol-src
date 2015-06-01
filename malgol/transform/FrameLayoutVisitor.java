package malgol.transform;

import java.util.HashMap;
import java.util.Map;

import malgol.ast.*;
import malgol.common.LocationTable;

public class FrameLayoutVisitor implements ASTVisitor {
	
	private int localSpaceUsed;
	private LocationTable table = null;
	private final Map<ASTNode, LocationTable> env = new HashMap<ASTNode, LocationTable>();
	
	public Map<ASTNode, LocationTable> getLayoutInformation() {
		return env;
	}
	
	private void enterScope() {
		table = new LocationTable(table);
	}
	
	private void dropScope() {
		table = table.getEnclosingScope();
	}

	@Override
	public void visit(BlockStatement s) {
		enterScope();
		env.put(s, table);
		
		for(Declaration d : s.getDeclarationList()) {
			localSpaceUsed += d.getType().getByteSize();
			table.insert(d.getName(), -localSpaceUsed);
		}
		for(Statement s2 : s.getStatementList()) {
			s2.accept(this);
		}
		dropScope();
	}

	@Override
	public void visit(AssignmentStatement s) {
		// Do nothing
	}

	@Override
	public void visit(SelectionStatement s) {
		if (!(s.getFalseBranch() instanceof SkipStatement))
			throw new RuntimeException(
					"Not expecting an else during space calculation");
		if (!(s.getTrueBranch() instanceof GotoStatement))
			throw new RuntimeException(
					"Expecting a goto in the true branch during space calculation");
	}

	@Override
	public void visit(WhileStatement s) {
		assert false : "WhileStatement visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(PrintStatement s) {
		// Do nothing
	}

	@Override
	public void visit(SkipStatement s) {
		// Do nothing
	}

	@Override
	public void visit(BinaryExpression e) {
		assert false : "BinaryExpression visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(UnaryExpression e) {
		assert false : "UnaryExpression visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(ArrayExpression e) {
		assert false : "ArrayExpression visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(VariableExpression e) {
		assert false : "VariableExpression visited in FrameLayoutVisitor";
	}
	
	@Override
	public void visit(ConstantExpression e) {
		assert false : "ConstantExpression visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(Declaration d) {
		assert false : "Declaration visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(GotoStatement s) {
		// Do nothing
	}

	@Override
	public void visit(OffsetExpression e) {
		assert false : "OffsetExpression visited in FrameLayoutVisitor";
	}

	@Override
	public void visit(DereferenceExpression e) {
		assert false : "DereferenceExpression visited in FrameLayoutVisitor";
	}

	// Custom Code

	@Override
	public void visit(FunctionDefinition d) {

		enterScope();
		env.put(d, table);

		for(Declaration decl : d.getParameters()) {
			localSpaceUsed += decl.getType().getByteSize();
			table.insert(decl.getName(), +localSpaceUsed);
		}

		dropScope();
	}

	@Override
	public void visit(ReturnStatement s) {
		// TODO
		//throw new RuntimeException("You need to implement this.");
	}

	@Override
	public void visit(FunctionCallExpression e) {
		// TODO
		throw new RuntimeException("You need to implement this.");
	}

	@Override
	public void visit(Program p) {

		localSpaceUsed = 0;

		enterScope();
		env.put(p, table);

		for(FunctionDefinition f : p.getFunctionList()) {
			f.accept(this);
		}

		table.insert("", localSpaceUsed);

		//dropScope();
		
		/* OLD DEFINITION BELOW
		localSpaceUsed = 0;
		outgoingSpaceUsed = 8;
		enterScope();
		env.put(p, table);
		p.getBlockStatement().accept(this);
		table.insert("", localSpaceUsed+outgoingSpaceUsed);
		*/
	}
}
