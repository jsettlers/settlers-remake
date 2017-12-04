package jsettlers.logic.movable.simplebehaviortree.nodes;

import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;

/**
 * @author homoroselaps
 */

public class Wait<T> extends Decorator<T> {
    private static final long serialVersionUID = -6025244799010530015L;

    /*
    Wait for a condition to become true,
     */

    public Wait(Node<T> condition) {
        super(condition);
    }

    @Override
    protected NodeStatus onTick(Tick<T> tick) {
        NodeStatus status = child.execute(tick);
        switch (status) {
            case Success: return NodeStatus.Success;
            default:
            case Failure:
            case Running: return NodeStatus.Running;
        }
    }
}
