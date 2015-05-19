package malgol.transform;

import malgol.ast.*;
import malgol.common.Label;
import malgol.type.Type;

/**
 * 
 * @author WMarrero
 */
public class AST2CVisitor implements ASTVisitor {
	private static final int MAX_INDENT = 20;
	protected static final String newLine = System
			.getProperty("line.separator");
	protected int indentLevel;
	protected StringBuilder buf;
	protected static final String[] indent = new String[MAX_INDENT];
	static {
		String temp = "";
		for (int i = 0; i < MAX_INDENT; i++) {
			indent[i] = temp;
			temp += "  ";
		}
	}

	public AST2CVisitor() {
		indentLevel = 0;
		buf = new StringBuilder(2000);
	}

	public void clear() {
		indentLevel = 0;
		buf.delete(0, buf.length());
	}

	public String getCode() {
		return buf.toString();
	}

	protected void appendLabels(Statement s) {
		for (Label l : s.getLabels()) {
			buf.append(l.toString());
			buf.append(':');
			buf.append(newLine);
		}
	}

	@Override
	public void visit(BlockStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append('{');
		buf.append(newLine);
		indentLevel++;
		for (Declaration d : s.getDeclarationList()) {
			buf.append(indent[indentLevel]);
			d.accept(this);
			buf.append(';');
			buf.append(newLine);
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
		appendLabels(s);
		buf.append(indent[indentLevel]);
		s.getLeft().accept(this);
		buf.append(" = ");
		s.getRight().accept(this);
		buf.append(';');
		buf.append(newLine);
	}

	@Override
	public void visit(SelectionStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("if (");
		s.getTest().accept(this);
		buf.append(')');
		buf.append(newLine);
		indentLevel++;
		s.getTrueBranch().accept(this);
		indentLevel--;
		buf.append(indent[indentLevel]);
		buf.append("else");
		buf.append(newLine);
		indentLevel++;
		s.getFalseBranch().accept(this);
		indentLevel--;
	}

	@Override
	public void visit(WhileStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("while (");
		s.getTest().accept(this);
		buf.append(')');
		buf.append(newLine);
		indentLevel++;
		s.getBody().accept(this);
		indentLevel--;
	}

	@Override
	public void visit(PrintStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("printf(\"%d\\n\", ");
		s.getExpression().accept(this);
		buf.append(");");
		buf.append(newLine);
	}

	@Override
	public void visit(SkipStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append(';');
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
		if (e.getType().baseType().isBool())
			buf.append("b_");
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

	public void visit(Declaration d) {
		Type baseType = d.getType().baseType();
		if (baseType.isLocation()) {
			buf.append("void* ");
		} else {
			buf.append("int ");
			if (d.getType().baseType().isBool())
				buf.append("b_");
		}
		buf.append(d.getName());
		buf.append(d.getType().dimensions());
	}

	@Override
	public void visit(GotoStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("goto ");
		buf.append(s.getTarget().getName());
		buf.append(';');
		buf.append(newLine);
	}

	@Override
	public void visit(DereferenceExpression e) {
		buf.append("(*(int *)");
		e.getLocation().accept(this);
		buf.append(')');
	}

	@Override
	public void visit(OffsetExpression e) {
		buf.append("((char *) ");
		e.getLocation().accept(this);
		buf.append(") + ");
		e.getOffset().accept(this);
	}

	@Override
	public void visit(FunctionDefinition d) {
		buf.append(indent[indentLevel]);
		buf.append("int ");
		buf.append(d.getName());
		buf.append('(');
		boolean first = true;
		for (Declaration parameter : d.getParameters()) {
			if (first)
				first = false;
			else
				buf.append(", ");
			parameter.accept(this);
		}
		buf.append(')');
		buf.append(newLine);
		d.getBody().accept(this);
	}

	@Override
	public void visit(ReturnStatement s) {
		appendLabels(s);
		buf.append(indent[indentLevel]);
		buf.append("return ");
		s.getExpression().accept(this);
		buf.append(';');
		buf.append(newLine);
	}

	@Override
	public void visit(FunctionCallExpression e) {
		buf.append(e.getName());
		buf.append('(');
		boolean first = true;
		for (Expression arg : e.getArguments()) {
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
		clear();		
		buf.append("#include <stdio.h>");
		buf.append(newLine);
		for (FunctionDefinition f : p.getFunctionList()) {
			buf.append(newLine);
			f.accept(this);
		}
	}
}