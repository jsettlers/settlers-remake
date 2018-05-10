package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.common.CommonConstants;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Debug extends Decorator<Context> {
	private static final long serialVersionUID = 9019598003328102086L;

	private final String message;

	public Debug(String message) {
		super(null);
		this.message = message;
	}

	public Debug(String message, Node<Context> child) {
		super(child);
		this.message = message;
	}

	@Override
	public NodeStatus onTick(Tick<Context> tick) {
		if (CommonConstants.DEBUG_BEHAVIOR_TREES) { System.out.println(message); }
		if (child != null) {
			NodeStatus result = child.execute(tick);
			if (CommonConstants.DEBUG_BEHAVIOR_TREES) {
				System.out.println(message + ": " + result);
			}
			return result;
		}
		return NodeStatus.SUCCESS;
	}
}
