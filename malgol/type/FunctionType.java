package malgol.type;

import java.util.LinkedList;

import malgol.ast.Expression;

public class FunctionType extends Type {
	private LinkedList<Expression> paramTypes;
	private int numParams;
	private Type returnType;

	public FunctionType() {
		super(-1);
	}
	
	public LinkedList<Expression> getParamTypes(){
		return paramTypes;
	}
	
	public int getNumParams() {
		return numParams;
	}
	
	public Type getReturnType() {
		return returnType;
	}

	private static final FunctionType unique = new FunctionType();

	public static FunctionType singleton() {
		return unique;
	}

	@Override
	public boolean equals(Type t2) {
		if (!(t2 instanceof FunctionType)) {
			return false;
		}
		FunctionType at2 = (FunctionType) t2;
		return 	   (numParams == at2.numParams) 
				&& (paramTypes.equals(at2.paramTypes)) 
				&& (returnType.equals(at2.returnType));
	}

	@Override
	public boolean isFunction() {
		return true;
	}

	@Override
	public Type baseType() {
		return ((FunctionType) this).baseType();
	}

}
