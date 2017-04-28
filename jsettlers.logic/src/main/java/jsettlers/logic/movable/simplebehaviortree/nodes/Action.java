package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Action<T> extends Node<T> {
	private static final long serialVersionUID = -4535362950446826714L;
	private final INodeStatusActionFunction<T> action;

	public Action(INodeStatusActionFunction<T> action) {
		super();
		this.action = action;
	}
	public Action(INodeStatusActionConsumer<T> action) {
		super();
		this.action = (t)->{ action.accept(t); return NodeStatus.Success;};
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		return action.apply(tick.Target);
	}
}
