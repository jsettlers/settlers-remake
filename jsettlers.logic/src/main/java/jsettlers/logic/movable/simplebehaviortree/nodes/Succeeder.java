package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public final class Succeeder extends Node {
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		return NodeStatus.Success;
	}
}
