package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Action extends Node {
	private INodeAction action;

	public Action(INodeAction action) {
		super();
		this.action = action;
	}
	
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		return action.run(tick.Target);
	}
}
