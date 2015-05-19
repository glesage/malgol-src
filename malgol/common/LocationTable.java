/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.common;

import java.util.Map;
import java.util.LinkedHashMap;


/**
 * 
 * @author Will
 */
public class LocationTable {
	private Map<String, Integer> table;
	private LocationTable enclosingScope;

	public LocationTable(LocationTable enclosingScope) {
		this.enclosingScope = enclosingScope;
		table = new LinkedHashMap<String, Integer>(20);
	}
	
	public LocationTable() {
		this(null);
	}
	
	public LocationTable getEnclosingScope() {
		return enclosingScope;
	}

	public void insert(String name, Integer location) {
		table.put(name, location);
	}

	public Integer lookupInCurrentScope(String n) {
		return table.get(n);
	}
	
	public int lookup(String n) {
		Integer location = lookupInCurrentScope(n);
		if (location == null) {
			LocationTable enclosing = this.getEnclosingScope();
			if (enclosing != null)
				location = enclosingScope.lookup(n);
			else
				throw new RuntimeException("Location not found " + n);
		}
		return location;
	}
}
