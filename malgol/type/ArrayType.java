package malgol.type;

public class ArrayType extends Type {
	private final Type elements;
	private final int size;

	public ArrayType(Type t, int s) {
		super(t.getByteSize() * s);
		elements = t;
		size = s;
	}
	
	public Type getElementType() {
		return elements;
	}
	
	public int getSize() {
		return size;
	}

	@Override
	public boolean equals(Type t2) {
		if (!(t2 instanceof ArrayType)) {
			return false;
		}
		ArrayType at2 = (ArrayType) t2;
		return (size == at2.size) && (elements.equals(at2.elements));
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public Type baseType() {
		return elements.baseType();
	}

	@Override
	public String dimensions() {
		return "[" + size + "]" + elements.dimensions();
	}

}
