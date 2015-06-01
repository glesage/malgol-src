/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.transform;

import jdk.nashorn.internal.ir.Block;
import malgol.ast.*;
import malgol.common.Operator;
import malgol.type.*;

import java.util.*;

//public class SimplifyExpressionsVisitor extends RemoveStructuredControlVisitor {}


public class SimplifyExpressionsVisitor implements ASTVisitor {
    private Expression expressionResult = null;
    private LinkedList<Declaration> newDeclarations = null;
    private LinkedList<Statement> newStatements = null;
    private Program programResult = null;
    private FunctionDefinition functionDefinition = null;
    private BlockStatement blockStatement = null;
    
    public Program getResult() {
    	return programResult;
    }
    
    private void freshResultVariables() {
    	expressionResult = null;
    	newDeclarations = new LinkedList<Declaration>();
    	newStatements = new LinkedList<Statement>();
    }

    @Override
    public void visit(BlockStatement b) {
        LinkedList<Statement> tempS = new LinkedList<Statement>();
        LinkedList<Declaration> tempD = new LinkedList<Declaration>(b.getDeclarationList());
        for(Statement s : b.getStatementList()) {
            s.accept(this);
            tempS.addAll(newStatements);
            tempD.addAll(newDeclarations);
        }
        BlockStatement newBlock = new BlockStatement(null, tempD, tempS);
        newBlock.addLabels(b.getLabels());
        freshResultVariables();
        newStatements.add(newBlock);
        blockStatement = newBlock;
    }

    @Override
    public void visit(AssignmentStatement s) {
        s.getLeft().accept(this);
        LinkedList<Statement> leftStatements = newStatements;
        LinkedList<Declaration> leftDeclarations = newDeclarations;
        Expression leftExpression = expressionResult;
        s.getRight().accept(this);
        leftStatements.addAll(newStatements);
        leftDeclarations.addAll(newDeclarations);
        newStatements = leftStatements;
        newDeclarations = leftDeclarations;
        Statement newAssignment = new AssignmentStatement(null, leftExpression, expressionResult);
        newStatements.add(newAssignment);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());
    }

    
    @Override
    public void visit(SelectionStatement s) { 
        assert(s.getTrueBranch() instanceof GotoStatement)
                : "True branch is not a goto during expression simplification.";
        assert(s.getFalseBranch() instanceof SkipStatement)
                : "False branch is not a skip during expression simplification.";        
        // NOTE: The true branch should be a GotoStatement and
        //       the false branch should be a SkipStatement.
        s.getTest().accept(this);
        SelectionStatement newIf =
                new SelectionStatement(null, expressionResult, s.getTrueBranch(), s.getFalseBranch());
        newStatements.add(newIf);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());
    }

    @Override
    public void visit(WhileStatement s) {
        // NOTE: There should be no WhileStatements at this point!!!
        
        assert(false) : "While statement encountered during expression simplificaiton.";
        //throw new RuntimeException("There should be no while loop at this point!");
    }

    @Override
    public void visit(PrintStatement s) {
        s.getExpression().accept(this);
        Statement newPrint = new PrintStatement(null, expressionResult);
        newStatements.add(newPrint);
        convertToSingleStatement();
        newStatements.get(0).addLabels(s.getLabels());
    }

    @Override
    public void visit(SkipStatement s) {
        freshResultVariables();
        newStatements.add(s);
    }

    @Override
    public void visit(GotoStatement s) {
    	freshResultVariables();
        newStatements.add(s);
    }

    @Override
    public void visit(ArrayExpression e) {
        VariableExpression location = VariableExpression.freshTemporary("LOCATION");
        location.setType(LocationType.singleton());
        VariableExpression index = VariableExpression.freshTemporary("INDEX");
        index.setType(IntType.singleton());
        LinkedList<Declaration> tempDeclarations = new LinkedList<Declaration>();
        LinkedList<Statement> tempStatements = new LinkedList<Statement>();
        Expression pointer = e;
        while (pointer instanceof ArrayExpression) {
            ArrayExpression ptr = (ArrayExpression) pointer;
            ptr.getIndex().accept(this);
            Expression scaleIndex = new BinaryExpression(null, Operator.TIMES, expressionResult,
            							new ConstantExpression(null, ptr.getType().getByteSize()));
            scaleIndex.setType(IntType.singleton());
            Statement scaleStatement = new AssignmentStatement(null, index, scaleIndex);
            //Expression offset = new BinaryExpression(Operator.PLUS, location, index);
            Expression offset = new OffsetExpression(location, index);
            offset.setType(LocationType.singleton());
            Statement offsetStatement = new AssignmentStatement(null, location, offset);
            tempStatements.add(0, offsetStatement);
            tempStatements.add(0, scaleStatement);
            tempDeclarations.addAll(0, newDeclarations);
            tempStatements.addAll(0, newStatements);
            pointer = ptr.getArray();
        }
        newStatements = tempStatements;
        newStatements.add(0, new AssignmentStatement(null, location, pointer));
        newDeclarations = tempDeclarations;
        newDeclarations.add(0, new Declaration(null, index.getName(), IntType.singleton()));
        newDeclarations.add(0, new Declaration(null, location.getName(), LocationType.singleton()));
        expressionResult = new DereferenceExpression(location);
        expressionResult.setType(e.getType());
    }

    @Override
    public void visit(BinaryExpression e) {
        e.getLeft().accept(this);
        LinkedList<Statement> leftStatements = newStatements;
        LinkedList<Declaration> leftDeclarations = newDeclarations;
        Expression leftExpression = expressionResult;
        e.getRight().accept(this);
        newStatements.addAll(0, leftStatements);
        newDeclarations.addAll(0, leftDeclarations);
        VariableExpression newVar = VariableExpression.freshTemporary("BinaryOp");
        newVar.setType(e.getType());
        Expression newExpression = new BinaryExpression(null, e.getOperator(), leftExpression, expressionResult);
        newStatements.add(new AssignmentStatement(null, newVar, newExpression));
        newDeclarations.add(new Declaration(null, newVar.getName(), e.getType()));
        expressionResult = newVar;
    }

    @Override
    public void visit(UnaryExpression e) {
        e.getExpression().accept(this);
        VariableExpression newVar = VariableExpression.freshTemporary("UnaryOp");
        newVar.setType(e.getType());
        Expression newExpression = new UnaryExpression(null, e.getOperator(), expressionResult);
        newStatements.add(new AssignmentStatement(null, newVar, newExpression));
        newDeclarations.add(new Declaration(null, newVar.getName(), e.getType()));
        expressionResult = newVar;
    }

    @Override
    public void visit(VariableExpression e) {
    	freshResultVariables();
        expressionResult = e;
    }

    @Override
    public void visit(ConstantExpression e) {
    	freshResultVariables();
        expressionResult = e;
    }

    @Override
    public void visit(Declaration d) {
        // This should never be called!!!
        assert(false) : "Attempting to simplify declaration during expression simplification";
    }
    
    private void convertToSingleStatement() {
        if (newStatements.size() > 1 || newDeclarations.size() > 0) {
        	LinkedList<Statement> temp = new LinkedList<Statement>();
        	temp.add(new BlockStatement(null, newDeclarations, newStatements));
        	newStatements = temp;
            newDeclarations = new LinkedList<Declaration>();
        }
        expressionResult = null;
    }

    @Override
    public void visit(DereferenceExpression e) {
        // There should not be any pointers yet!
        assert(false) : "Dereference found DURING expression simplification";
    }
    
    @Override
    public void visit(OffsetExpression e) {
        // There should not be any pointers yet!
        assert(false) : "OffsetExpression found DURING expression simplification";
    }

    ///CUSTOM CODE STARTS HERE!!!///
    
    @Override
    public void visit(FunctionDefinition f) {
        f.getBody().accept(this);

        functionDefinition = new FunctionDefinition(f.getFirstToken(), f.getReturnType(), f.getName(), f.getParameters(), blockStatement);
    }

    @Override
    public void visit(FunctionCallExpression e) {

        freshResultVariables();
        VariableExpression newVar = VariableExpression.freshTemporary("functionCall");
        newVar.setType(e.getType());
        newStatements.add(new AssignmentStatement(null, newVar, e));
        newDeclarations.add(new Declaration(null, newVar.getName(), e.getType()));
        expressionResult = newVar;
    }

    @Override
    public void visit(ReturnStatement s) {
        /*s.getExpression().accept(this);
        ReturnStatement returnStatement = new ReturnStatement(s.getFirstToken(), expressionResult);
        newStatements.add(returnStatement);

        functionDefinition = null;*/
        freshResultVariables();
        newStatements.add(s);
    }

    @Override
	public void visit(Program p) {
        LinkedList<FunctionDefinition> resultList = new LinkedList<>();

        for(FunctionDefinition function : p.getFunctionList()){
            function.accept(this);
            resultList.add(functionDefinition);
        }

        programResult = new Program(resultList);
    	
    	/* OLD DEFINITION BELOW
    	p.getBlockStatement().accept(this);
		assert(newDeclarations.size() == 0);
		assert(newStatements.size() == 1);
		BlockStatement temp = (BlockStatement) newStatements.get(0);
		programResult = new Program(temp);
		*/
	}
}
