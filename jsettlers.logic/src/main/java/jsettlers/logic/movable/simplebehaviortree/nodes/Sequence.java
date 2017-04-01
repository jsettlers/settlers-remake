package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Composite;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Sequence<T> extends Composite<T> {

	@SafeVarargs
	public Sequence(Node<T>... children) {
		super(children);
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) { 
		for (Node<T> node : children) {
			NodeStatus status = node.execute(tick);
			if (!status.equals(NodeStatus.Success)) 
				return status;
		}
		return NodeStatus.Success;
	}
}
