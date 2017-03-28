package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Inverter extends Node {
	protected Node child;

	public Inverter(Node child) {
		super();
		this.child = child;
	}
	
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		NodeStatus result = child.execute(tick);
		if (result.equals(NodeStatus.Success)) {
			return NodeStatus.Failure;
		} else if (result.equals(NodeStatus.Failure)) {
			return NodeStatus.Success; 
		} else {
			return result;
		}
	}
}
