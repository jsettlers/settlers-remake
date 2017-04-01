package jsettlers.logic.movable.simplebehaviortree;

public class Root<T> extends Node<T> {
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
