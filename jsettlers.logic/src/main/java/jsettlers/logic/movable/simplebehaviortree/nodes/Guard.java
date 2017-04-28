package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Guard<T> extends Node<T> {
	private static final long serialVersionUID = -4675927057210755053L;
	protected final IBooleanConditionFunction<T> condition;
	protected final Node<T> child;
	protected final boolean value;

	public Guard(IBooleanConditionFunction<T> condition, Node<T> child) {
		super(child);
		this.condition = condition;
		this.child = child;
		value = true;
	}
	
	public Guard(IBooleanConditionFunction<T> condition, boolean shouldBe, Node<T> child) {
		super(child);
		this.condition = condition;
		this.child = child;
		value = shouldBe;
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		boolean result = condition.apply(tick.Target);
		if (result == value) 
			return child.execute(tick);
		else 
			return NodeStatus.Failure;
	}
}
