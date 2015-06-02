/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.transform;

import java.util.Map;

import malgol.ast.*;
import malgol.common.*;
import malgol.type.Type;

public class CodeGenerationVisitor implements ASTVisitor {

	private static final int INDENT_AMOUNT = 20;
	private static final int INSTRUCTION_WIDTH = 10;
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String INDENT = generateIndentString(INDENT_AMOUNT);
	public static final String PRINTF_STRING = "_printf_string";
	private final StringBuilder buf;
	private final Map<ASTNode, LocationTable> locationInfo;
	private LocationTable currentLocationTable;
	private SymbolTable symbolTable;
	private boolean lvalue = false;
	private boolean isParameter = false;
	private static final String[] spaces = { "", " ", "  ", "   ", "    ",
			"     ", "      ", "       ", "        ", "         ",
			"          ", "           ", "            ", "             ",
			"              ", "               ", "                ",
			"                 ", "                  ", "                   ",
			"                    ", "                     " };

	public CodeGenerationVisitor(Map<ASTNode, LocationTable> locationInfo) {
		this.locationInfo = locationInfo;
		buf = new StringBuilder(1024);
		// spaceVisitor = new SpaceCalculationVisitor();
		symbolTable = new SymbolTable();
	}

	public String getCode() {
		return buf.toString();
	}

	@Override
	public void visit(BlockStatement s) {
		symbolTable.createNewScope();
		currentLocationTable = locationInfo.get(s);
		for (Declaration decl : s.getDeclarationList()) {
			decl.accept(this);
		}
		buf.append(generateLabels(s));
		for (Statement stat : s.getStatementList()) {
			stat.accept(this);
		}
		currentLocationTable = currentLocationTable.getEnclosingScope();
		symbolTable.dropScope();
	}

	@Override
	public void visit(AssignmentStatement s) {
		buf.append(generateLabels(s));
		buf.append("### " + s.getLeft() + " := " + s.getRight() + NEWLINE);
		s.getRight().accept(this);
		buf.append(generateInstruction("movl", "%eax", "%ecx"));
		lvalue = true;
		s.getLeft().accept(this);
		lvalue = false;
		buf.append(generateInstruction("movl", "%ecx", "(%eax)"));
	}

	@Override
	public void visit(SelectionStatement s) {
		assert (s.getFalseBranch() instanceof SkipStatement) : "Found false branch during code generation";
		assert (s.getTrueBranch() instanceof GotoStatement) : "Found non-jump in true branch during code generation";
		assert (s.getTest() instanceof ConstantExpression
				|| s.getTest() instanceof VariableExpression || s.getTest() instanceof DereferenceExpression) : "Found complex test during code generation"
				+ s.toString();
		GotoStatement target = (GotoStatement) s.getTrueBranch();
		buf.append(generateLabels(s));
		buf.append("### IF (" + s.getTest() + ") " + s.getTrueBranch());
		s.getTest().accept(this);
		buf.append(generateInstruction("cmpl", "$0", "%eax"));
		buf.append(generateInstruction("jne", target.getTarget().getName()));
	}

	@Override
	public void visit(WhileStatement s) {
		assert false : "Encountered while statement during code generation!";
	}

	@Override
	public void visit(PrintStatement s) {
		buf.append(generateLabels(s));
		buf.append("### PRINT " + s.getExpression() + NEWLINE);
		s.getExpression().accept(this);
		buf.append(generateInstruction("movl", "%eax", "4(%esp)"));
		buf.append(generateInstruction("movl", '$' + PRINTF_STRING, "(%esp)"));
		buf.append(generateInstruction("call", "_printf"));
	}

	@Override
	public void visit(SkipStatement s) {
		buf.append(generateLabels(s));
		buf.append(generateInstruction("nop"));
	}

	@Override
	public void visit(GotoStatement s) {
		buf.append(generateLabels(s));
		buf.append("### GOTO " + s.getTarget().getName() + NEWLINE);
		buf.append(generateInstruction("jmp", s.getTarget().getName()));
	}

	@Override
	public void visit(ArrayExpression e) {
		assert false : "Encountered array expression during code generation";
	}

	@Override
	public void visit(BinaryExpression e) {
		e.getRight().accept(this);
		buf.append(generateInstruction("movl", "%eax", "%ecx"));
		e.getLeft().accept(this);
		switch (e.getOperator()) {
		case AND:
			buf.append(generateInstruction("andl", "%ecx", "%eax"));
			break;
		case OR:
			buf.append(generateInstruction("orl", "%ecx", "%eax"));
			break;
		case PLUS:
			buf.append(generateInstruction("addl", "%ecx", "%eax"));
			break;
		case MINUS:
			buf.append(generateInstruction("subl", "%ecx", "%eax"));
			break;
		case TIMES:
			buf.append(generateInstruction("imull", "%ecx", "%eax"));
			break;
		case DIVIDE:
			buf.append(generateInstruction("cltd"));
			buf.append(generateInstruction("idivl", "%ecx"));
			break;
		case MOD:
			buf.append(generateInstruction("cltd"));
			buf.append(generateInstruction("idivl", "%ecx"));
			buf.append(generateInstruction("movl", "%edx", "%eax"));
			break;
		case LESSTHAN:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setl", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		case GREATERTHAN:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setg", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		case LEQ:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setle", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		case GEQ:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setge", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		case EQUALS:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setz", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		case BANGEQUALS:
			buf.append(generateInstruction("cmpl", "%ecx", "%eax"));
			buf.append(generateInstruction("setnz", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		default:
			assert false : "Unknown binary operator during code generation.";
		}
	}

	@Override
	public void visit(UnaryExpression e) {
		e.getExpression().accept(this);
		switch (e.getOperator()) {
		case UMINUS:
			buf.append(generateInstruction("negl", "%eax"));
			break;
		case NOT:
			buf.append(generateInstruction("cmpl", "$0", "%eax"));
			buf.append(generateInstruction("sete", "%al"));
			buf.append(generateInstruction("movzbl", "%al", "%eax"));
			break;
		default:
			assert false : "Unknown unary operator during code generation.";
		}
	}

	@Override
	public void visit(VariableExpression e) {
		Symbol s = symbolTable.lookupInAllScopes(e.getName());
		if (s.getType().isArray()) {
			buf.append(generateInstruction("leal", s.getLocation() + "(%ebp)",
					"%eax"));
		} else {
			if (lvalue) {
				buf.append(generateInstruction("leal", s.getLocation()
						+ "(%ebp)", "%eax"));
			} else {
				buf.append(generateInstruction("movl", s.getLocation()
						+ "(%ebp)", "%eax"));
			}
		}
	}

	@Override
	public void visit(ConstantExpression e) {
		buf.append(generateInstruction("movl", "$" + e.getValue(), "%eax"));
	}

	@Override
	public void visit(Declaration d) {
		String name = d.getName();
		Type type = d.getType();
		int location = currentLocationTable.lookup(name);
		Symbol sym = Symbol
				.newVariableSymbol(name, type, isParameter, location);
		symbolTable.insert(sym);
	}

	@Override
	public void visit(DereferenceExpression e) {
		e.getLocation().accept(this);
		buf.append(generateInstruction("movl", "(%eax)", "%eax"));
	}

	@Override
	public void visit(OffsetExpression e) {
		e.getOffset().accept(this);
		buf.append(generateInstruction("movl", "%eax", "%ecx"));
		e.getLocation().accept(this);
		buf.append(generateInstruction("addl", "%ecx", "%eax"));
	}

	@Override
	public void visit(Program p) {
		// Clear buf
		buf.setLength(0);

		// Generate assembly header
		buf.append(generateInstruction(".data"));
		buf.append(generateOneLabel(PRINTF_STRING));
		buf.append(generateInstruction(".ascii", "\"%d\\n\\0\""));
		buf.append(generateInstruction(".text"));
		buf.append(generateInstruction(".global _main"));

		// Recursive call on all function definitions.
		for( FunctionDefinition function : p.getFunctionList() ){
			function.accept(this);
		}

		/*  OLD DEFINITION BELOW
		// Clear buf
		buf.setLength(0);

		// Set up location table that holds frame size
		currentLocationTable = locationInfo.get(p);

		// Generate assembly header
		buf.append(generateInstruction(".data"));
		buf.append(generateOneLabel(PRINTF_STRING));
		buf.append(generateInstruction(".ascii", "\"%d\\n\\0\""));
		buf.append(generateInstruction(".text"));
		buf.append(generateInstruction(".global _main"));
		buf.append(NEWLINE);

		// Setup main entry
		buf.append(generateOneLabel("_main"));
		buf.append(generateInstruction("pushl", "%ebp"));
		buf.append(generateInstruction("movl", "%esp", "%ebp"));
		buf.append(generateInstruction("subl",
				"$" + currentLocationTable.lookup(""), "%esp"));

		// Recursive call on block.
		p.getBlockStatement().accept(this);

		// Setup main exit
		buf.append(generateInstruction("leave"));
		buf.append(generateInstruction("ret"));
		*/
	}

	@Override
	public void visit(FunctionDefinition d) {
		// TODO Auto-generated method stub
		throw new RuntimeException("You need to implement this.");
	}

	@Override
	public void visit(ReturnStatement s) {
		buf.append(generateLabels(s));
		s.getExpression().accept(this);
		buf.append(generateInstruction("leave"));
		buf.append(generateInstruction("ret"));
	}

	@Override
	public void visit(FunctionCallExpression e) {
		// TODO Auto-generated method stub
		throw new RuntimeException("You need to implement this.");
	}
	
	private static String generateIndentString(int size) {
		String localResult = "";
		for (int i = 0; i < size; i++) {
			localResult += ' ';
		}
		return localResult;
	}

	private String generateOneLabel(String s) {
		StringBuilder local = new StringBuilder(50);
		int spaceCount = INDENT_AMOUNT - (s.length() + 3);
		for (int i = 0; i < spaceCount; i++) {
			local.append(' ');
		}
		local.append(s);
		local.append(":");
		local.append(NEWLINE);
		return local.toString();
	}

	private String generateLabels(Statement s) {
		String temp = "";
		for (Label l : s.getLabels()) {
			temp += generateOneLabel(l.getName());
		}
		return temp;
	}

	private String generateInstruction(String op) {
		return INDENT + op + NEWLINE;
	}

	private String generateInstruction(String op, String arg) {
		return INDENT + op + spaces[INSTRUCTION_WIDTH - op.length()] + arg
				+ NEWLINE;
	}

	private String generateInstruction(String op, String arg1, String arg2) {
		return INDENT + op + spaces[INSTRUCTION_WIDTH - op.length()] + arg1
				+ ", " + arg2 + NEWLINE;
	}
}