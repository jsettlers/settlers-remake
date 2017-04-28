package jsettlers.logic.movable.simplebehaviortree;

public class Composite<T> extends Node<T> {
	private static final long serialVersionUID = 8795400757387672902L;

	@SafeVarargs
    protected Composite(Node<T>... children) {
		super(children);
	}

}
