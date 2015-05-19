package malgol.ast;

public class PrettyPrintVisitor implements ASTVisitor {
	private static final int MAX_INDENT = 20;
	private static final String[] indent = new String[MAX_INDENT];
	private static final String newLine = System.getProperty("line.separator");
	private int indentLevel;
	private StringBuilder buf;
	
	static {
		String temp = "";
		for(int i = 0; i < MAX_INDENT; i++) {
			indent[i] = temp;
			temp += "  ";
		}
	}
	
	public PrettyPrintVisitor() {
		indentLevel = 0;
		buf = new StringBuilder(2000);
	}
	
	public void clear() {
		indentLevel = 0;
		buf.delete(0, buf.length());
	}
	
	public String getResult() {
		return buf.toString();
	}
	
	@Override
	public void visit(BlockStatement s) {
		buf.append(indent[indentLevel]);
		buf.append('{');
		buf.append(newLine);
		indentLevel++;
		for (Declaration d : s.getDeclarationList()) {
			d.accept(this);
		}
		for (Statement s2 : s.getStatementList()) {
			s2.accept(this);
		}
		indentLevel--;
		buf.append(indent[indentLevel]);
		buf.append('}');
		buf.append(newLine);
	}

	@Override
	public void visit(AssignmentStatement s) {
		buf.append(indent[indentLevel]);
		s.getLeft().accept(this);
		buf.append(" := ");
		s.getRight().accept(this);
		buf.append(newLine);
	}

	@Override
	public void visit(SelectionStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("IF ");
		s.getTest().accept(this);
		buf.append(newLine);
		indentLevel++;
		buf.append(indent[indentLevel]);
		buf.append("THEN ");
		s.getTrueBranch().accept(this);
		buf.append(newLine);
		buf.append(indent[indentLevel]);
		buf.append("ELSE ");
		s.getFalseBranch().accept(this);
		indentLevel--;
	}

	@Override
	public void visit(WhileStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("WHILE ");
		s.getTest().accept(this);
		buf.append(newLine);
		indentLevel++;
		s.getBody().accept(this);
		indentLevel--;
	}

	@Override
	public void visit(PrintStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("PRINT ");
		s.getExpression().accept(this);
		buf.append(newLine);
	}

	@Override
	public void visit(SkipStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("SKIP");
		buf.append(newLine);
	}

	@Override
	public void visit(BinaryExpression e) {
		buf.append("(");
		e.getLeft().accept(this);
		buf.append(" ");
		buf.append(e.getOperator().toString());
		buf.append(" ");
		e.getRight().accept(this);
		buf.append(")");
	}

	@Override
	public void visit(UnaryExpression e) {
		buf.append("(");
		buf.append(e.getOperator().toString());
		e.getExpression().accept(this);
		buf.append(")");
	}

	@Override
	public void visit(VariableExpression e) {
		buf.append(e.getName());
	}

	@Override
	public void visit(ConstantExpression e) {
		buf.append(e.getValue());
	}

	@Override
	public void visit(ArrayExpression e) {
		e.getArray().accept(this);
		buf.append('[');
		e.getIndex().accept(this);
		buf.append(']');
	}

	@Override
	public void visit(Declaration d) {
		buf.append(indent[indentLevel]);
		if (d.getType().isLocation())
			throw new RuntimeException("Encountered Location Type in PrettyPrint");
		buf.append(d.getName());
		buf.append(" : ");
		buf.append(d.getType().toString());
		buf.append(newLine);
	}
	
	@Override
	public void visit(ReturnStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("RETURN ");
		s.getExpression().accept(this);
		buf.append(newLine);
	}

	
	@Override
	public void visit(GotoStatement s) {
		buf.append(indent[indentLevel]);
		buf.append("GOTO ");
		buf.append(s.getTarget().getName());
		buf.append(newLine);
	}

	@Override
	public void visit(DereferenceExpression e) {
		buf.append('*');
		e.getLocation().accept(this);
	}

	@Override
	public void visit(OffsetExpression e) {
		e.getLocation().accept(this);
		buf.append(" + ");
		e.getOffset().accept(this);
	}

	@Override
	public void visit(FunctionDefinition d) {
		buf.append(indent[indentLevel]);
		buf.append(d.getName());
		buf.append('(');
		boolean first = true;
		for(Declaration p : d.getParameters()) {
			if (first)
				first = false;
			else
				buf.append(", ");
			p.accept(this);
		}
		buf.append(") : ");
		buf.append(d.getReturnType().toString());
		buf.append(newLine);
		indentLevel++;
		d.getBody().accept(this);
		indentLevel--;
	}
	
	@Override
	public void visit(FunctionCallExpression e) {
		buf.append(e.getName());
		buf.append('(');
		boolean first = true;
		for(Expression arg : e.getArguments()) {
			if (first)
				first = false;
			else
				buf.append(", ");
			arg.accept(this);
		}
		buf.append(')');
	}


	@Override
	public void visit(Program p) {
		for(FunctionDefinition f : p.getFunctionList())
			f.accept(this);
	}

}
