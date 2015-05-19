package malgol.type;

public class BoolType extends Type {
	private static final BoolType unique = new BoolType();

	public static BoolType singleton() {
		return unique;
	}

	private BoolType() {
		super(4);
	}

	@Override
	public boolean isBool() {
		return true;
	}
}
