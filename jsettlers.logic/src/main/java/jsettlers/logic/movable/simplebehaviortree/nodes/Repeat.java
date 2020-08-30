package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Repeat<T> extends Decorator<T> {
    /*
        Run the child if condition=SUCCESS
        Preemptive = check condition every tick before executing the child
        NonPreemptive = as long as the child is running the condition is not checked

        Return RUNNING if child=RUNNING || condition=RUNNING
        Return SUCCESS if condition=FAILURE
        Return FAILURE if child=FAILURE
     */

	public enum Policy {
		PREEMPTIVE,
		NONPREEMPTIVE
	}

	private static final long serialVersionUID = -661870259301299858L;

	private final Node<T> condition;
	private final Policy  policy;
	private       boolean childRunning;

	public Repeat(Node<T> condition, Node<T> child) {
		this(Policy.NONPREEMPTIVE, condition, child);
	}

	public Repeat(Policy policy, Node<T> condition, Node<T> child) {
		super(child);
		this.condition = condition;
		this.policy = policy;
	}

	@Override
	protected NodeStatus onTick(Tick<T> tick) {
		while (true) {
			if (policy.equals(Policy.PREEMPTIVE) || !childRunning) {
				NodeStatus cond = condition.execute(tick);
				switch (cond) {
					case RUNNING:
						return NodeStatus.RUNNING;
					case FAILURE:
						return NodeStatus.SUCCESS;
					case SUCCESS:
						break;
				}
			}

			NodeStatus status = child.execute(tick);
			switch (status) {
				case SUCCESS:
					childRunning = false;
					break;
				case RUNNING:
					childRunning = true;
					return NodeStatus.RUNNING;
				case FAILURE:
					childRunning = false;
					return NodeStatus.FAILURE;
			}
		}
	}

	@Override
	protected void onOpen(Tick<T> tick) {
		childRunning = false;
	}
}