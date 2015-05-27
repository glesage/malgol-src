package malgol.transform;

import malgol.ast.*;
import malgol.common.*;
import malgol.type.*;
import malgol.util.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Function;

/**
 * 
 * @author WMarrero
 */
public class StaticAnalysisVisitor implements ASTVisitor {
	private SymbolTable symbolTable = null;

	@Override
	public void visit(BlockStatement s) {
		symbolTable.createNewScope();
		for (Declaration d : s.getDeclarationList()) {
			d.accept(this);
		}
		for (Statement stat : s.getStatementList()) {
			stat.accept(this);
		}
		symbolTable.dropScope();
	}

	@Override
	public void visit(AssignmentStatement s) {
		s.getLeft().accept(this);
		if (s.getLeft().getType().isArray()) {
			Error.msg("Cannot assign into an array", s);
		}
		s.getRight().accept(this);
		if (!(s.getLeft().getType().equals(s.getRight().getType()))) {
			Error.msg("Type mismatch in", s);
		}
	}

	@Override
	public void visit(SelectionStatement s) {
		s.getTest().accept(this);
		if (s.getTest().getType() != BoolType.singleton()) {
			Error.msg("if test is not boolean:", s.getTest());
		}
		s.getTrueBranch().accept(this);
		s.getFalseBranch().accept(this);
	}

	@Override
	public void visit(WhileStatement s) {
		s.getTest().accept(this);
		if (s.getTest().getType() != BoolType.singleton()) {
			Error.msg("while test is not boolean:", s.getTest());
		}
		s.getBody().accept(this);
	}

	@Override
	public void visit(PrintStatement s) {
		s.getExpression().accept(this);
		if (s.getExpression().getType() != IntType.singleton()) {
			Error.msg("print expression must be integer type", s);
		}
	}

	@Override
	public void visit(SkipStatement s) {
	}

	@Override
	public void visit(ArrayExpression e) {
		e.getArray().accept(this);
		Type t = e.getArray().getType();
		if (!t.isArray()) {
			Error.msg("Not an array", e);
		}
		ArrayType aType = (ArrayType) t;
		e.setType(aType.getElementType());
		e.getIndex().accept(this);
		if (!e.getIndex().getType().isInt()) {
			Error.msg("Array index is not an int", e);
		}
	}

	@Override
	public void visit(BinaryExpression e) {
		e.getLeft().accept(this);
		e.getRight().accept(this);
		if (e.getOperator().isArithmetic()) {
			if (e.getLeft().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getLeft());
			}
			if (e.getRight().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getRight());
			}
			e.setType(IntType.singleton());
		} else if (e.getOperator().isRelational()) {
			if (e.getLeft().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getLeft());
			}
			if (e.getRight().getType() != IntType.singleton()) {
				Error.msg("Expected integer:", e.getRight());
			}
			e.setType(BoolType.singleton());
		} else if (e.getOperator().isBoolean()) {
			if (e.getLeft().getType() != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getLeft());
			}
			if (e.getRight().getType() != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getRight());
			}
			e.setType(BoolType.singleton());
		} else {
			Error.msg("unknown operator:", e);
		}
	}

	@Override
	public void visit(UnaryExpression e) {
		e.getExpression().accept(this);
		Type type = e.getExpression().getType();
		if (e.getOperator().isBoolean()) {
			if (type != BoolType.singleton()) {
				Error.msg("Expected Boolean:", e.getExpression());
			}
			e.setType(BoolType.singleton());
		} else if (e.getOperator().isArithmetic()) {
			if (type != IntType.singleton()) {
				Error.msg("Expected integer:", e.getExpression());
			}
			e.setType(IntType.singleton());
		}
	}

	@Override
	public void visit(VariableExpression e) {
		if (e.getType() == null) {
			Symbol sym = symbolTable.lookupInAllScopes(e.getName());
			if (sym == null) {
				Error.msg("Undeclared variable: ", e);
			}
			Type t = sym.getType();
			e.setType(t);
		}
	}

	@Override
	public void visit(ConstantExpression e) {
		if (e.getType() == null) {
			Error.msg(
					"FATAL ERROR: Constant of unkown type.  Contact instructor",
					e);
		}
	}

	@Override
	public void visit(Declaration d) {
		String name = d.getName();
		if (symbolTable.lookupInCurrentScope(name) != null) {
			Error.msg(d.getName() + " already declared in this scope!!!", d);
		}
		Type type = d.getType();
		Symbol sym = Symbol.newVariableSymbol(name, type, false);
		symbolTable.insert(sym);
	}

	@Override
	public void visit(GotoStatement s) {
		throw new RuntimeException(
				"GotoStatement encountered during static analysis");
	}

	@Override
	public void visit(DereferenceExpression e) {
		throw new RuntimeException(
				"DereferenceExpression encountered during static analysis");
	}

	@Override
	public void visit(OffsetExpression e) {
		throw new RuntimeException(
				"OffsetExpression encountered during static analysis.");
	}
	
	//
	// START OF CUSTOM CODE //
	//
	@Override
	public void visit(FunctionDefinition f) {
		
		String name = f.getName();
		if (symbolTable.lookupInCurrentScope(name) != null) {
			Error.msg(name + " already declared!", f);
		}

		// Set everything we gotta know about the function
		// - Return type
		// - Number of parameters/arguments
		FunctionType type = new FunctionType();
		type.setReturnType(f.getReturnType());
		type.setNumParams(f.getParameters().size());

		Symbol sym = Symbol.newFunctionSymbol(name, type);
		symbolTable.insert(sym);

		symbolTable.createNewScope();
		for (Declaration d : f.getParameters()) {
			d.accept(this);
		}
		f.getBody().accept(this);
		symbolTable.dropScope();
	}

	@Override
	public void visit(FunctionCallExpression e) {
		String name = e.getName();
		Symbol s = symbolTable.lookupInAllScopes(name);
		if (s == null) {
			Error.msg(name + " was not declared!", e);
		} else if (!s.isFunction()) {
			Error.msg("Trying to call a non-function:", e);
		}

		FunctionType type = (FunctionType)symbolTable.lookupInAllScopes(e.getName()).getType();
		e.setType(type.getReturnType());

		if (e.getArguments().size() != type.getNumParams()) {
			Error.msg("Argument count mismatch:", e);
		}

		List<Expression> args = e.getArguments();

		for (int i=0;i<args.size(); i++) {
			args.get(i).accept(this);
		}

		//
		// NOT SURE IF WE'RE SUPPOSED TO DO SOMETHING HERE TO CALL FUNCTIONS
		//
	}

	@Override
	public void visit(ReturnStatement s) {
		//
		// NOT SURE IF I NEED TO CHECK THE RETURN TYPE HERE
		//
		Symbol symbol = Symbol.newStructSymbol("",s.getExpression().getType());
		symbolTable.insert(symbol);
		
		s.getExpression().accept(this);
	}

	@Override
	public void visit(Program p) {
		symbolTable = new SymbolTable();
		symbolTable.createNewScope();
		for (FunctionDefinition f : p.getFunctionList()) {
			f.accept(this);
		}
		symbolTable.dropScope();
	}
}
