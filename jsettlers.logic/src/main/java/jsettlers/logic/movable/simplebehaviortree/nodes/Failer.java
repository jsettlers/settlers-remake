package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public final class Failer<T> extends Node<T> {
    private static final long serialVersionUID = 5577842967150867903L;

    @Override
	protected NodeStatus onTick(Tick<T> tick) {
		return NodeStatus.Failure;
	}
}
