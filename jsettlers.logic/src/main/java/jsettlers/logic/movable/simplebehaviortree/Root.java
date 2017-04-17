package jsettlers.logic.movable.simplebehaviortree;

public class Root<T> extends Node<T> {
	private static final long serialVersionUID = 4857616270171506110L;

	protected Node<T> child;
	private int maxID = -1;
	public int getChildrenCount() {
		return maxID+1;
	}
	public Root(Node<T> child) {
		super(child);
		this.child = child;
	}
	@Override
	protected NodeStatus onTick(Tick<T> tick) { 
		return child.execute(tick); 
	}
	public Root<T> init() {
		maxID = initiate(-1); 
		return this;
	}
}
