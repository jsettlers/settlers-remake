package jsettlers.logic.movable.simplebehaviortree;

public class Operator<T> extends Node<T> {
	private static final long serialVersionUID = -8709396730753474299L;

	public Node<T> left,right;

	public Operator(Node<T> left, Node<T> right) {
		super(left, right);
	}

}
