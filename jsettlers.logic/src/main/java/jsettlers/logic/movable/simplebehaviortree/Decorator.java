package jsettlers.logic.movable.simplebehaviortree;

public class Decorator<T> extends Node<T> {
	private static final long serialVersionUID = 2453864576230160564L;

	public final Node<T> child;
	public Decorator(Node<T> child) {
		super(child);
		this.child = child;
	}
}
