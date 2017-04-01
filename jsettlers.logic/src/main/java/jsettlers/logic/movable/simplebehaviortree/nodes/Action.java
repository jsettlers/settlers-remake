package jsettlers.logic.movable.simplebehaviortree.nodes;
import java.util.function.Consumer;
import java.util.function.Function;

import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Action<T> extends Node<T> {
	private Function<T,NodeStatus> action;

	public Action(Function<T,NodeStatus> action) {
		super();
		this.action = action;
	}
	public Action(Consumer<T> action) {
		super();
		this.action = (t)->{ action.accept(t); return NodeStatus.Success;};
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		return action.apply(tick.Target);
	}
}
