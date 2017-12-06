package jsettlers.logic.movable.components;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.Root;

/**
 * @author homoroselaps
 */

public abstract class BehaviorComponent extends Component {
    private static final long serialVersionUID = -7388888039559869043L;
    private Tick<Context> tick;

    @Override
    protected void onAwake() {
        tick = new Tick<>(new Context(entity,this), CreateBehaviorTree());
    }

    @Override
    protected void onUpdate() {
        tick.Tick();
    }

    protected abstract Root<Context> CreateBehaviorTree();
}
