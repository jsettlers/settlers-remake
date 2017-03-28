package jsettlers.logic.movable;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Root;

/**
 * Created by jt-1 on 3/28/2017.
 */

public class BehaviorComponent extends Component {
    private Root root;
    private Tick<Entity> tick;
    public BehaviorComponent(Root behaviorTree) {
        root = behaviorTree;
    }

    @Override
    public void OnAwake() {
        tick = new Tick<>(entity, root);
    }

    @Override
    public void OnUpdate() {
        NodeStatus status = tick.Tick();
    }
}
