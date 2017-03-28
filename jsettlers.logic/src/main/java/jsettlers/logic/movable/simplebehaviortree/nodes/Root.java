package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Root extends Node {
	protected Node child;
	private int maxID = -1;
	public int getChildrenCount() {
		return maxID+1;
	}
	public Root(Node child) {
		super(child);
		this.child = child;
	}
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) { 
		return child.execute(tick); 
	}
	public Root init() {
		maxID = initiate(-1); 
		return this;
	}
}
