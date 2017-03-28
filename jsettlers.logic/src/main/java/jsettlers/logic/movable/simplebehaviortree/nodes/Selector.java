package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Selector extends Composite {

	public Selector(Node... children) {
		super(children);
	}
	
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) { 
		for (Node node : children) {
			NodeStatus status = node.execute(tick);
			if (!status.equals(NodeStatus.Failure)) 
				return status;
		}
		return NodeStatus.Failure;
	}
}
