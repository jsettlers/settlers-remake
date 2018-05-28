package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Composite;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Sequence<T> extends Composite<T> {
	private static final long serialVersionUID = -1764866628237365366L;

	@SafeVarargs
	public Sequence(Node<T>... children) {
		super(children);
	}

	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		for (Node<T> node : children) {
			NodeStatus status = node.execute(tick);
			if (status != NodeStatus.SUCCESS) {
				return status;
			}
		}
		return NodeStatus.SUCCESS;
	}
}
