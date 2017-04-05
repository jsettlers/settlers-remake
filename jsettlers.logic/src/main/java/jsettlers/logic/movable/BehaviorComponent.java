package jsettlers.logic.movable;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;

/**
 * @author homoroselaps
 */

public class BehaviorComponent extends Component {
    private static final long serialVersionUID = -7388888039559869043L;
    private Root<Entity> root;
    private Tick<Entity> tick;
    public BehaviorComponent(Root<Entity> behaviorTree) {
        root = behaviorTree;
    }

    @Override
    public void onAwake() {
        tick = new Tick<>(entity, root);
    }

    @Override
    public void onUpdate() {
        NodeStatus status = tick.Tick();
    }
}
