package jsettlers.logic.movable.simplebehaviortree.nodes;

import org.apache.commons.lang3.StringUtils;

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
		if (CommonConstants.DEBUG_BEHAVIOR_TREES && tick.target.entity.isInDebugMode()) {
			System.out.println(indent(tick, message));
		}

		if (child != null) {
			tick.target.debugLevel++;
			NodeStatus result = child.execute(tick);
			tick.target.debugLevel--;

			if (CommonConstants.DEBUG_BEHAVIOR_TREES && tick.target.entity.isInDebugMode()) {
				System.out.println(indent(tick, message + ": " + result));
			}

			return result;
		}

		return NodeStatus.SUCCESS;
	}

	private String indent(Tick<Context> tick, String message) {
		return StringUtils.repeat('\t', tick.target.debugLevel) + message;
	}
}
