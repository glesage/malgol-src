/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.transform;

import malgol.ast.*;
import malgol.common.Label;

import java.util.LinkedList;

/**
 *
 * @author WMarrero
 */
public class RemoveStructuredControlVisitor implements ASTVisitor {

	private Statement statementResult;
	private FunctionDefinition functionDefinitionResult;
	private Program programResult;

	public Program getResult() {
		return programResult;
	}

	@Override
	public void visit(BlockStatement s) {
		LinkedList<Statement> newSL = new LinkedList<Statement>();
		for (Statement oldS : s.getStatementList()) {
			oldS.accept(this);
			newSL.add(statementResult);
		}
		statementResult = new BlockStatement(s.getFirstToken(), s.getDeclarationList(),
				newSL);
	}

	@Override
	public void visit(AssignmentStatement s) {
		statementResult = s;
	}

	@Override
	public void visit(SelectionStatement s) {
		// 1) if (s.exp) goto L_if_true
		// 2) goto L_if_false
		// 3) L_if_true: s.trueStatement
		// 4) goto L_if_end
		// 5) L_if_false: s.falseStatement
		// 6) L_if_end: noop
		Label trueLabel = Label.freshLabel("if_true");
		Label falseLabel = Label.freshLabel("if_false");
		Label endLabel = Label.freshLabel("if_end");

		Statement line1 = new SelectionStatement(s.getFirstToken(),
				s.getTest(), new GotoStatement(null, trueLabel),
				new SkipStatement(null));
		Statement line2 = new GotoStatement(null, falseLabel);
		s.getTrueBranch().accept(this);
		Statement line3 = statementResult;
		line3.addLabel(trueLabel);
		Statement line4 = new GotoStatement(null, endLabel);
		s.getFalseBranch().accept(this);
		Statement line5 = statementResult;
		line5.addLabel(falseLabel);
		Statement line6 = new SkipStatement(null);
		line6.addLabel(endLabel);

		LinkedList<Statement> temp = new LinkedList<Statement>();
		temp.add(line1);
		temp.add(line2);
		temp.add(line3);
		temp.add(line4);
		temp.add(line5);
		temp.add(line6);
		statementResult = new BlockStatement(s.getFirstToken(),
				new LinkedList<Declaration>(), temp);
	}

	@Override
	public void visit(WhileStatement s) {
		// 1) L_loop_test: if (s.exp) goto L_loop_body
		// 2) goto L_loop_exit
		// 3) L_loop_body: s.body
		// 4) goto L_loop_test
		// 5) L_loop_exit: noop
		Label testLabel = Label.freshLabel("loop_test");
		Label bodyLabel = Label.freshLabel("loop_body");
		Label exitLabel = Label.freshLabel("loop_exit");

		Statement line1 = new SelectionStatement(null, s.getTest(),
				new GotoStatement(null, bodyLabel), new SkipStatement(null));
		line1.addLabel(testLabel);
		Statement line2 = new GotoStatement(null, exitLabel);
		s.getBody().accept(this);
		Statement line3 = statementResult;
		line3.addLabel(bodyLabel);
		Statement line4 = new GotoStatement(null, testLabel);
		Statement line5 = new SkipStatement(null);
		line5.addLabel(exitLabel);

		LinkedList<Statement> temp = new LinkedList<Statement>();
		temp = new LinkedList<Statement>();
		temp.add(line1);
		temp.add(line2);
		temp.add(line3);
		temp.add(line4);
		temp.add(line5);
		statementResult = new BlockStatement(s.getFirstToken(),
				new LinkedList<Declaration>(), temp);
	}

	@Override
	public void visit(PrintStatement s) {
		statementResult = s;
	}

	@Override
	public void visit(SkipStatement s) {
		statementResult = s;
	}

	@Override
	public void visit(ArrayExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(BinaryExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(UnaryExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(VariableExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(ConstantExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(Declaration d) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting declaration");
	}

	@Override
	public void visit(GotoStatement s) {
		statementResult = s;
	}

	@Override
	public void visit(DereferenceExpression e) {
		throw new RuntimeException("There should not be any pointers yet!");
	}

	@Override
	public void visit(OffsetExpression e) {
		throw new RuntimeException("There should not be any pointers yet!");
	}

	@Override
	public void visit(FunctionDefinition f) {
		f.getBody().accept(this);
		BlockStatement b = null;
		if (statementResult instanceof BlockStatement)
			b = (BlockStatement) statementResult;
		else
			throw new RuntimeException(
					"Removing Structured Control visitor: Function body returned non block statement");
		functionDefinitionResult = new FunctionDefinition(f.getFirstToken(),
				f.getReturnType(), f.getName(), f.getParameters(), b);
		statementResult = null;
	}

	@Override
	public void visit(FunctionCallExpression e) {
		throw new RuntimeException(
				"Remvoing Structured Control visiting expression");
	}

	@Override
	public void visit(ReturnStatement s) {
		statementResult = s;
		functionDefinitionResult = null;
	}

	@Override
	public void visit(Program p) {
		LinkedList<FunctionDefinition> resultList = new LinkedList<FunctionDefinition>();
		for(FunctionDefinition f : p.getFunctionList()) {
			f.accept(this);
			resultList.add(functionDefinitionResult);
		}
		programResult = new Program(resultList);
	}
}
