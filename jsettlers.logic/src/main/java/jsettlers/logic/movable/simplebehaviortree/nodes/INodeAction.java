package jsettlers.logic.movable.simplebehaviortree.nodes;

public interface INodeAction<T> {
	NodeStatus run(T target);
}
