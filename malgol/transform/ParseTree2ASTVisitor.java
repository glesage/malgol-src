package malgol.transform;

import malgol.analysis.Analysis;
import malgol.node.*;
import malgol.ast.*;
import malgol.common.Operator;
import malgol.type.*;

import java.util.LinkedList;

public class ParseTree2ASTVisitor implements Analysis {
	private Expression expressionResult;
	private LinkedList<Expression> expressionListResult;
	private LinkedList<Declaration> declarationListResult;
	private Statement statementResult;
	private LinkedList<Statement> statementListResult;
	private Declaration declarationResult;
	private FunctionDefinition functionDefinitionResult;
	private LinkedList<FunctionDefinition> functionDefinitionListResult;
	private Type typeResult;
	private Program programResult;

	private void clearResults() {
		/*
		expressionResult = null;
		expressionListResult = null;
		statementResult = null;
		statementListResult = null;
		declarationResult = null;
		declarationListResult = null;
		functionDefinitionResult = null;
		functionDefinitionListResult = null;
		typeResult = null;
		programResult = null;
		*/
	}

	public Program getResult() {
		return programResult;
	}

	@Override
	public Object getIn(Node node) {
		return null;
	}

	@Override
	public void setIn(Node node, Object o) {
	}

	@Override
	public Object getOut(Node node) {
		return null;
	}

	@Override
	public void setOut(Node node, Object o) {
	}

	@Override
	public void caseStart(Start node) {
		node.getPProgram().apply(this);
	}

	@Override
	public void caseABlockBlock(ABlockBlock node) {
		PDeclsStmts declsStmts = node.getDeclsStmts();
		declsStmts.apply(this);
		statementResult = new BlockStatement(node.getLBrace(), declarationListResult, statementListResult);
	}

	@Override
	public void caseAPrintStmt(APrintStmt node) {
		node.getExp().apply(this);
		Expression temp = expressionResult;
		clearResults();
		statementResult = new PrintStatement(node.getPrint(), temp);
	}

	@Override
	public void caseAAssignStmt(AAssignStmt node) {
		node.getL().apply(this);
		Expression left = expressionResult;
		node.getR().apply(this);
		Expression right = expressionResult;
		clearResults();
		statementResult = new AssignmentStatement(left.getFirstToken(), left,
				right);
		expressionResult = null;
	}

	@Override
	public void caseABlockStmt(ABlockStmt node) {
		PDeclsStmts declsStmts = node.getDeclsStmts();
		declsStmts.apply(this);
		statementResult = new BlockStatement(node.getLBrace(), declarationListResult, statementListResult);
	}
	
	@Override
	public void caseASomeDeclsDeclsStmts(ASomeDeclsDeclsStmts node) {
		PDecl decl = node.getDecl();
		decl.apply(this);
		Declaration temp = declarationResult;
		PDeclsStmts declsStmts = node.getDeclsStmts();
		declsStmts.apply(this);
		declarationListResult.addFirst(temp);
	}

	@Override
	public void caseANoDeclsDeclsStmts(ANoDeclsDeclsStmts node) {
		PStmtList stmtList = node.getStmtList();
		stmtList.apply(this);
		declarationListResult = new LinkedList<Declaration>();
	}

	@Override
	public void caseASomeStmtList(ASomeStmtList node) {
		PStmt stmt = node.getStmt();
		stmt.apply(this);
		Statement temp = statementResult;
		PStmtList stmtList = node.getStmtList();
		stmtList.apply(this);
		statementListResult.addFirst(temp);
	}

	@Override
	public void caseANoneStmtList(ANoneStmtList node) {
		statementListResult = new LinkedList<Statement>();
	}

	@Override
	public void caseASelectStmt(ASelectStmt node) {
		node.getCond().apply(this);
		Expression cond = expressionResult;
		expressionResult = null;
		node.getTBranch().apply(this);
		Statement tBranch = statementResult;
		node.getFBranch().apply(this);
		Statement fBranch = statementResult;
		clearResults();
		statementResult = new SelectionStatement(node.getIf(), cond, tBranch,
				fBranch);
	}

	@Override
	public void caseAWhileStmt(AWhileStmt node) {
		node.getCond().apply(this);
		Expression cond = expressionResult;
		expressionResult = null;
		node.getBody().apply(this);
		Statement body = statementResult;
		clearResults();
		statementResult = new WhileStatement(node.getWhile(), cond, body);
	}

	@Override
	public void caseASkipStmt(ASkipStmt node) {
		clearResults();
		statementResult = new SkipStatement(node.getSkip());
	}

	@Override
	public void caseADecl(ADecl node) {
		TId id = node.getId();
		node.getType().apply(this);
		Type temp = typeResult;
		clearResults();
		declarationResult = new Declaration(id, id.getText(), temp);
	}

	@Override
	public void caseAIntType(AIntType node) {
		clearResults();
		typeResult = IntType.singleton();
	}

	@Override
	public void caseABoolType(ABoolType node) {
		clearResults();
		typeResult = BoolType.singleton();
	}

	@Override
	public void caseAArrayType(AArrayType node) {
		node.getElements().apply(this);
		Type temp = typeResult;
		clearResults();
		typeResult = new ArrayType(temp, new Integer(node.getSize().getText()));
	}

	@Override
	public void caseAOrExp(AOrExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.OR, left, right);
	}

	@Override
	public void caseABaseExp(ABaseExp node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseAAndAndExp(AAndAndExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.AND, left, right);
	}

	@Override
	public void caseABaseAndExp(ABaseAndExp node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseANotNotExp(ANotNotExp node) {
		node.getArg().apply(this);
		Expression temp = expressionResult;
		clearResults();
		expressionResult = new UnaryExpression(node.getBang(), Operator.NOT,
				temp);
	}

	@Override
	public void caseABaseNotExp(ABaseNotExp node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseALtRelExp(ALtRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.LESSTHAN, left, right);
	}

	@Override
	public void caseAGtRelExp(AGtRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.GREATERTHAN, left, right);
	}

	@Override
	public void caseAEqRelExp(AEqRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.EQUALS, left, right);
	}

	@Override
	public void caseANeqRelExp(ANeqRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.BANGEQUALS, left, right);
	}

	@Override
	public void caseAGeqRelExp(AGeqRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.GEQ, left, right);
	}

	@Override
	public void caseALeqRelExp(ALeqRelExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.LEQ, left, right);
	}

	@Override
	public void caseAIntExpRelExp(AIntExpRelExp node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseAPlusIntExp(APlusIntExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.PLUS, left, right);
	}

	@Override
	public void caseAMinusIntExp(AMinusIntExp node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.MINUS, left, right);
	}

	@Override
	public void caseATermIntExp(ATermIntExp node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseAMultTerm(AMultTerm node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.TIMES, left, right);
	}

	@Override
	public void caseADivTerm(ADivTerm node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.DIVIDE, left, right);
	}

	@Override
	public void caseAModTerm(AModTerm node) {
		node.getLeft().apply(this);
		Expression left = expressionResult;
		expressionResult = null;
		node.getRight().apply(this);
		Expression right = expressionResult;
		clearResults();
		expressionResult = new BinaryExpression(left.getFirstToken(),
				Operator.MOD, left, right);
	}

	@Override
	public void caseAFactorTerm(AFactorTerm node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseAIntNegFactor(AIntNegFactor node) {
		node.getArg().apply(this);
		Expression temp = expressionResult;
		clearResults();
		expressionResult = new UnaryExpression(node.getMinus(),
				Operator.UMINUS, temp);
	}

	@Override
	public void caseAPrimeExpFactor(APrimeExpFactor node) {
		node.getArg().apply(this);
	}

	@Override
	public void caseAIntPrimeExp(AIntPrimeExp node) {
		node.getNumber().apply(this);
	}

	@Override
	public void caseABoolPrimeExp(ABoolPrimeExp node) {
		node.getBoolean().apply(this);
	}

	@Override
	public void caseAVarPrimeExp(AVarPrimeExp node) {
		node.getVarExp().apply(this);
	}

	@Override
	public void caseAParenPrimeExp(AParenPrimeExp node) {
		node.getExp().apply(this);
	}

	@Override
	public void caseAIdVarExp(AIdVarExp node) {
		clearResults();
		expressionResult = new VariableExpression(node.getId(), node.getId()
				.getText());
	}

	@Override
	public void caseAArrayVarExp(AArrayVarExp node) {
		node.getArray().apply(this);
		Expression array = expressionResult;
		expressionResult = null;
		node.getIndex().apply(this);
		Expression index = expressionResult;
		clearResults();
		expressionResult = new ArrayExpression(array.getFirstToken(), array,
				index);
	}

	@Override
	public void caseTLPar(TLPar node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTRPar(TRPar node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTLBrace(TLBrace node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTRBrace(TRBrace node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTLBracket(TLBracket node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTRBracket(TRBracket node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTPlus(TPlus node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTMinus(TMinus node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTMult(TMult node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTDiv(TDiv node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTMod(TMod node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTComma(TComma node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTLt(TLt node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTGt(TGt node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTGeq(TGeq node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTLeq(TLeq node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTEq(TEq node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTNeq(TNeq node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTAmp(TAmp node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTBar(TBar node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTBang(TBang node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTColonEquals(TColonEquals node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTInt(TInt node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTBool(TBool node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTArrayOf(TArrayOf node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTColon(TColon node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTPrint(TPrint node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTIf(TIf node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTThen(TThen node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTElse(TElse node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTWhile(TWhile node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTDo(TDo node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTSkip(TSkip node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTBlank(TBlank node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTNumber(TNumber node) {
		clearResults();
		expressionResult = new ConstantExpression(node, new Integer(
				node.getText()));
	}

	@Override
	public void caseTId(TId node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseTBoolean(TBoolean node) {
		clearResults();
		expressionResult = new ConstantExpression(node, new Boolean(
				node.getText()));
	}

	@Override
	public void caseEOF(EOF node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseInvalidToken(InvalidToken node) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void caseAProgram(AProgram node) {
		PFunctionDef first = node.getFunctionDef();
		first.apply(this);
		FunctionDefinition firstResult = functionDefinitionResult;
		PFunctionDefList rest = node.getFunctionDefList();
		rest.apply(this);
		functionDefinitionListResult.addFirst(firstResult);
		Program result = new Program(functionDefinitionListResult);
		clearResults();
		programResult = result;
	}
	

	@Override
	public void caseASomeFunctionDefList(ASomeFunctionDefList node) {
		PFunctionDef first = node.getFunctionDef();
		first.apply(this);
		FunctionDefinition firstResult = functionDefinitionResult;
		PFunctionDefList rest = node.getFunctionDefList();
		rest.apply(this);
		functionDefinitionListResult.addFirst(firstResult);
		LinkedList<FunctionDefinition> temp = functionDefinitionListResult;
		clearResults();
		functionDefinitionListResult = temp;
	}

	@Override
	public void caseANoneFunctionDefList(ANoneFunctionDefList node) {
		clearResults();
		functionDefinitionListResult = new LinkedList<FunctionDefinition>();
	}

	@Override
	public void caseAFunctionDef(AFunctionDef node) {
		Token firstToken = node.getId();
		String name = firstToken.getText();
		node.getType().apply(this);
		Type returnType = typeResult;
		node.getParamList().apply(this);
		LinkedList<Declaration> parameterList = declarationListResult;
		node.getBlock().apply(this);
		BlockStatement body = (BlockStatement) statementResult;
		clearResults();
		functionDefinitionResult = new FunctionDefinition(firstToken,
				returnType, name, parameterList, body);

	}

	@Override
	public void caseANoneParamList(ANoneParamList node) {
		clearResults();
		declarationListResult = new LinkedList<Declaration>();
	}

	@Override
	public void caseASomeParamList(ASomeParamList node) {
		PDecl first = node.getDecl();
		first.apply(this);
		Declaration firstResult = declarationResult;
		PParamListTail rest = node.getParamListTail();
		rest.apply(this);
		declarationListResult.addFirst(firstResult);
		LinkedList<Declaration> result = declarationListResult;
		clearResults();
		declarationListResult = result;
	}

	@Override
	public void caseANoneParamListTail(ANoneParamListTail node) {
		clearResults();
		declarationListResult = new LinkedList<Declaration>();
	}

	@Override
	public void caseASomeParamListTail(ASomeParamListTail node) {
		PDecl first = node.getDecl();
		first.apply(this);
		Declaration firstResult = declarationResult;
		PParamListTail rest = node.getParamListTail();
		rest.apply(this);
		declarationListResult.addFirst(firstResult);
		LinkedList<Declaration> result = declarationListResult;
		clearResults();
		declarationListResult = result;
	}
	
	@Override
	public void caseAReturnStmt(AReturnStmt node) {
		node.getExp().apply(this);
		Expression temp = expressionResult;
		clearResults();
		statementResult = new ReturnStatement(node.getReturn(), temp);
	}

	@Override
	public void caseAFunctionCallPrimeExp(AFunctionCallPrimeExp node) {
		Token name = node.getId();
		node.getArgList().apply(this);
		LinkedList<Expression> args = expressionListResult;
		clearResults();
		expressionResult = new FunctionCallExpression(name, name.getText(),
				args);
	}

	@Override
	public void caseASomeArgList(ASomeArgList node) {
		PExp first = node.getExp();
		first.apply(this);
		Expression firstResult = expressionResult;
		PArgListTail rest = node.getArgListTail();
		rest.apply(this);
		expressionListResult.addFirst(firstResult);
		LinkedList<Expression> temp = expressionListResult;
		clearResults();
		expressionListResult = temp;
	}
	
	@Override
	public void caseANoneArgList(ANoneArgList node) {
		expressionListResult = new LinkedList<Expression>();
	}

	@Override
	public void caseASomeArgListTail(ASomeArgListTail node) {
		PExp first = node.getExp();
		first.apply(this);
		Expression firstResult = expressionResult;
		PArgListTail rest = node.getArgListTail();
		rest.apply(this);
		expressionListResult.addFirst(firstResult);
		LinkedList<Expression> temp = expressionListResult;
		clearResults();
		expressionListResult = temp;
	}

	@Override
	public void caseANoneArgListTail(ANoneArgListTail node) {
		expressionListResult = new LinkedList<Expression>();
	}

	@Override
	public void caseTReturn(TReturn node) {
		throw new UnsupportedOperationException();
	}
}