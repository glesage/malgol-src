/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.common;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 * @author Will
 */
public class SymbolTable {
	private final LinkedList<Map<String, Symbol>> table;

	public SymbolTable(Map<String, Symbol> outerScope) {
		this();
		table.add(outerScope);
	}
	
	public SymbolTable() {
		table = new LinkedList<Map<String, Symbol>>();
	}
	
	public void createNewScope() {
		table.push(new LinkedHashMap<String, Symbol>(20));
	}
	
	public void dropScope() {
		table.pop();
	}

	public void insert(Symbol s) {
		table.getFirst().put(s.getName(),  s);
	}

	public Symbol lookupInCurrentScope(String n) {
		return table.getFirst().get(n);
	}
	
	public Symbol lookupInAllScopes(String n) {
		for (Map<String, Symbol> scope : table) {
				Symbol result = scope.get(n);
				if (result != null)
					return result;
		}
		return null;
	}

}
