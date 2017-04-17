package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Composite;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class MemSelector<T> extends Composite<T> {
	private static final long serialVersionUID = -4098732000225742833L;
	private int index = 0;

	@SafeVarargs
	public MemSelector(Node<T>... children) {
		super(children);
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) { 
		for (; index < children.size(); index++) {
			NodeStatus status = children.get(index).execute(tick);
			if (!status.equals(NodeStatus.Failure)) 
				return status;
		}
		return NodeStatus.Failure;
	}
	
	@Override
	protected void onOpen(Tick<T> tick) {
		index = 0;
	}
}
