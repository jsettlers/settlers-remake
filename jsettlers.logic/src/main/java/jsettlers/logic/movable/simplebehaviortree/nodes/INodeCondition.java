package jsettlers.logic.movable.simplebehaviortree.nodes;

public interface INodeCondition<T> {
	boolean run(T target);
}
