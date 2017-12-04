package jsettlers.logic.movable.components;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;

/**
 * @author homoroselaps
 */

public class BehaviorComponent extends Component {
    private static final long serialVersionUID = -7388888039559869043L;
    private final Root<Context> root;
    private Tick<Context> tick;
    public BehaviorComponent(Root<Context> behaviorTree) {
        root = behaviorTree;
    }

    @Override
    protected void onAwake() {
        tick = new Tick<>(new Context(entity,this), root);
    }

    @Override
    protected void onUpdate() {
        NodeStatus status = tick.Tick();
    }
}
