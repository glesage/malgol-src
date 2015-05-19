/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.common;

import malgol.type.Type;

/**
 * 
 * @author Will
 */
public class Symbol {
	private enum Kind {VARIABLE, FUNCTION, STRUCT};
	
	private String name;
	private Type type;
	private int location;
	private boolean parameter;
	private Kind kind;
	
	public static Symbol newVariableSymbol(String n, Type t, boolean p, int l) {
		return new Symbol(n, t, p, l, Kind.VARIABLE);
	}
	
	public static Symbol newVariableSymbol(String n, Type t, boolean p) {
		return new Symbol(n, t, p, Integer.MAX_VALUE, Kind.VARIABLE);
	}
	
	public static Symbol newFunctionSymbol(String n, Type t) {
		return new Symbol(n, t, false, Integer.MIN_VALUE, Kind.FUNCTION);
	}
	
	public static Symbol newStructSymbol(String n, Type t) {
		return new Symbol(n, t, false, Integer.MIN_VALUE, Kind.STRUCT);
	}

	private Symbol(String n, Type t, boolean p, int l, Kind k) {
		name = n;
		type = t;
		parameter = p;
		location = l;
		kind = k;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public void setLocation(int l) {
		location = l;
	}

	public int getLocation() {
		if (location == Integer.MAX_VALUE)
			throw new RuntimeException("This variable's location was never set!!!!");
		return location;
	}

	public boolean isParameter() {
		return parameter;
	}
	
	public boolean isVariable() {
		return kind == Kind.VARIABLE;
	}
	
	public boolean isFunction() {
		return kind == Kind.FUNCTION;
	}
	
	public boolean isStruct() {
		return kind == Kind.STRUCT;
	}
}
