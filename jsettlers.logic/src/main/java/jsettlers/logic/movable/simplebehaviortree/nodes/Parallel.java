package jsettlers.logic.movable.simplebehaviortree.nodes;
import jsettlers.logic.movable.simplebehaviortree.Composite;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Parallel<T> extends Composite<T> {
	private static final long serialVersionUID = 3614671053589100247L;

    /*
        Run each child until it's completed
        Return Success when
        | ONE -> one child was successful
        | ALL -> all children were successful

        Return Running when
        | preemptive = true -> successPolicy not fullfilled && any child of it's children still running
        | preemptive = false -> if any of it's children is still running

        Return Failure in all other cases
    */

	public enum Policy {
		ONE,
		ALL
	}
	public static Policy ONE = Policy.ONE;
	public static Policy ALL = Policy.ALL;
	
	private final Policy successPolicy;
	private final NodeStatus[] childStatus;
    private final boolean preemptive;
	private int successCount;

	@SafeVarargs
	public Parallel(Policy successPolicy, boolean preemptive, Node<T>... children) {
		super(children);
		childStatus = new NodeStatus[this.children.size()];
		this.successPolicy = successPolicy;
        this.preemptive = preemptive;
	}
	
	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		boolean anyRunning = false;
		for (int index = 0; index < children.size(); index++) {
			if (!childStatus[index].equals(NodeStatus.Running)) {
				continue;
			}
			NodeStatus status = children.get(index).execute(tick);
			childStatus[index] = status;
			if (status.equals(NodeStatus.Success)) successCount++;
			else if (status.equals(NodeStatus.Running)) anyRunning = true;
		}
		boolean successCondition =
            successPolicy == Policy.ONE && successCount >= 1 ||
            successPolicy == Policy.ALL && successCount == children.size();

        if (anyRunning && preemptive && !successCondition) {
                return NodeStatus.Running;
        } else if (anyRunning && !preemptive) {
            return NodeStatus.Running;
        }

		if (successCondition)  {
				return NodeStatus.Success;
		} else {
			return NodeStatus.Failure;
		}
	}
	
	@Override
	protected void onOpen(Tick<T> tick) {
		for (int i = 0; i < childStatus.length; i++) {
			childStatus[i] = NodeStatus.Running;
		}
		successCount = 0;
	}
}
