package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Composite;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Selector<T> extends Composite<T> {

	@SafeVarargs
	public Selector(Node<T>... children) {
		super(children);
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) { 
		for (Node<T> node : children) {
			NodeStatus status = node.execute(tick);
			if (!status.equals(NodeStatus.Failure)) 
				return status;
		}
		return NodeStatus.Failure;
	}
}
