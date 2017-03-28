package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public final class Failer extends Node {
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		return NodeStatus.Failure;
	}
}
