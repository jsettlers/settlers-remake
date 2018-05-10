package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

public class Repeat<T> extends Decorator<T> {
    /*
        Run the child if condition=Success
        As long as the child is running the condition is not checked

        Return Running if child=Running || condition=Running
        Return Success if condition=Failure
        Return Failure if child=Failure
     */

    public enum Policy {
        PREEMPTIVE,
        NONPREEMPTIVE
    }
    public static Policy ONE = Policy.PREEMPTIVE;
    public static Policy ALL = Policy.NONPREEMPTIVE;

    private static final long serialVersionUID = -661870259301299858L;
    private final Node<T> condition;
    private final Policy policy;
    private boolean childRunning;

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
                    case Running:
                        return NodeStatus.Running;
                    case Failure:
                        return NodeStatus.Success;
                    case Success:
                        break;
                }
            }

            NodeStatus status = child.execute(tick);
            switch (status) {
                case Success: childRunning = false; break;
                case Running: childRunning = true;  return NodeStatus.Running;
                case Failure: childRunning = false; return NodeStatus.Failure;
            }
        }
    }

    @Override
    protected void onOpen(Tick<T> tick) {
        childRunning = false;
    }
}


/*

repeat
    condition
    action
    wait
 */