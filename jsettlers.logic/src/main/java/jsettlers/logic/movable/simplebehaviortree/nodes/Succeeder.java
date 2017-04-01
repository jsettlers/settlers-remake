package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public final class Succeeder<T> extends Node<T> {
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		return NodeStatus.Success;
	}
}
