/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package malgol.type;

/**
 * 
 * @author WMarrero
 */
public class LocationType extends Type {
	private static final LocationType unique = new LocationType();

	public static LocationType singleton() {
		return unique;
	}

	private LocationType() {
		super(4);
	}

	@Override
	public boolean isLocation() {
		return true;
	}

	@Override
	public Type baseType() {
		return this;
	}
}
