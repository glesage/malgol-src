package malgol.ast;

import malgol.common.Label;
import malgol.node.Token;
import java.util.List;
import java.util.LinkedList;

public abstract class Statement extends ASTNode {
	private List<Label> labels;

	public Statement(Token firstToken) {
		super(firstToken);
		labels = new LinkedList<Label>();
	}
	
	public List<Label> getLabels() {
		return labels;
	}
	
	public void addLabel(Label l) {
		labels.add(l);
	}
	
	public void addLabels(List<Label> list) {
		labels.addAll(list);
	}
}
