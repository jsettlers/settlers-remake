package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Parallel extends Composite {
	public enum Policy {
		ONE,
		ALL
	}
	public static Policy ONE = Policy.ONE;
	public static Policy ALL = Policy.ALL;
	
	private Policy successPolicy;
	private NodeStatus[] childStatus;
	private int successCount;

	public Parallel(Policy successPolicy, Node... children) {
		super(children);
		childStatus = new NodeStatus[this.children.size()];
		this.successPolicy = successPolicy;
	}
	
	@Override
	protected <T> NodeStatus onTick(Tick<T> tick) {
		boolean allFinished = true;
		for (int index = 0; index < children.size(); index++) {
			if (!childStatus[index].equals(NodeStatus.Running)) {
				allFinished = false;
				continue;
			}
			NodeStatus status = children.get(index).execute(tick);
			childStatus[index] = status;
			if (status.equals(NodeStatus.Success))
				successCount++;
		}
		if (allFinished){
			if (successPolicy == Policy.ONE && successCount >= 1 ||
				successPolicy == Policy.ALL && successCount == children.size())
				return NodeStatus.Success;
			
			return NodeStatus.Failure;
		}
		else {
			return NodeStatus.Running;
		}
	}
	
	@Override
	protected <T> void onOpen(Tick<T> tick) {
		for (int i = 0; i < childStatus.length; i++) {
			childStatus[i] = NodeStatus.Running;
		}
		successCount = 0;
	}
}
