package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Condition<T> extends Node<T> {
	private static final long serialVersionUID = -5811980322685099119L;
	private final IBooleanConditionFunction<T> condition;

	public Condition(IBooleanConditionFunction<T> condition) {
		super();
		this.condition = condition;
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		return condition.apply(tick.Target) ? NodeStatus.Success : NodeStatus.Failure;
	}
}
