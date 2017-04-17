package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Inverter<T> extends Decorator<T> {
	private static final long serialVersionUID = -3568446114722874065L;

	public Inverter(Node<T> child) {
		super(child);
	}

	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		NodeStatus result = child.execute(tick);
		if (result.equals(NodeStatus.Success)) {
			return NodeStatus.Failure;
		} else if (result.equals(NodeStatus.Failure)) {
			return NodeStatus.Success; 
		} else {
			return result;
		}
	}
}
