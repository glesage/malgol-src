package malgol.common;

public class Label {

	private static int count = 1;
	private final String name;

	public Label(String n) {
		name = n;
	}
	
	public String getName() {
		return name;
	}

	public static Label freshLabel() {
		return freshLabel("");
	}

	public static Label freshLabel(String info) {
		Label result = new Label("L_" + info + count);
		count++;
		return result;
	}

	public String toString() {
		return name;
	}
}