package malgol.type;

public class IntType extends Type {
	private static final IntType unique = new IntType();

	public static IntType singleton() {
		return unique;
	}

	private IntType() {
		super(4);
	}

	@Override
	public boolean isInt() {
		return true;
	}
	
}
