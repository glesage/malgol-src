package malgol.type;

import malgol.common.Symbol;

public abstract class Type {
	private final int byteSize;
		
	public Type(int size) {
		byteSize = size;
	}

	public boolean equals(Type t2) {
		// Default behavior the same as object.
		return this == t2;
	}

	@Override
	public int hashCode() {
		// Equals has been overriden. This silly hash function is safe but
		// needs to be overridden due to ineffeciency.
		assert (false) : "Should override hashCode for types";
		return 0;
	}

	public Type baseType() {
		return this;
	}

	public int getByteSize() {
		return byteSize;
	}
	
	public String dimensions() {
		// Default dimensions is the empty string.
		// ArrayType will need to override this to provide array dimension
		// notation.
		return "";
	}

	public boolean isBool() {
		return (this == (Type) BoolType.singleton());
	}

	public boolean isInt() {
		return (this == (Type) IntType.singleton());
	}
	
	public boolean isArray() {
		return false;
	}

	public boolean isLocation() {
		return false;
	}
	
	public boolean isFunction() {
		return false;
	}
	
	public boolean isTypeName() {
		return false;
	}
	
	public Symbol getField(String name) {
		throw new RuntimeException("Trying to get field of a simple type.");
	}
}
