package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

/**
 * @author homoroselaps
 */

public class Wait<T> extends Decorator<T> {
	private static final long serialVersionUID = -6025244799010530015L;

	public Wait(Node<T> condition) {
		super(condition);
	}

	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		NodeStatus status = child.execute(tick);
		switch (status) {
			case SUCCESS:
				return NodeStatus.SUCCESS;
			default:
			case FAILURE:
			case RUNNING:
				return NodeStatus.RUNNING;
		}
	}
}
