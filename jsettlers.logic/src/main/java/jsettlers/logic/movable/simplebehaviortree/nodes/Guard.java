package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Guard extends Node {
	protected INodeCondition condition;
	protected Node child;
	protected boolean value;

	public Guard(INodeCondition condition, Node child) {
		super(child);
		this.condition = condition;
		this.child = child;
		value = true;
	}
	
	public Guard(INodeCondition condition, boolean shouldBe, Node child) {
		super(child);
		this.condition = condition;
		this.child = child;
		value = shouldBe;
	}
	
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		boolean result = condition.run(tick.Target);
		if (result == value) 
			return child.execute(tick);
		else 
			return NodeStatus.Failure;
	}
}
