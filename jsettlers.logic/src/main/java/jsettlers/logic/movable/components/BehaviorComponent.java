package jsettlers.logic.movable.components;

import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.Tick;

import static jsettlers.logic.movable.BehaviorTreeHelper.debug;

/**
 * @author homoroselaps
 */
public abstract class BehaviorComponent extends Component {
	private static final long serialVersionUID = -7388888039559869043L;

	private Tick<Context> tick;

	@Override
	protected void onWakeUp() {
		tick = new Tick<>(new Context(entity, this), new Root<>(debug("==<root: behavior>== of " + entity.getID(), createBehaviorTree())));
	}

	@Override
	protected void onUpdate() {
		tick.target.debugLevel = 0;
		tick.tick();
	}

	protected abstract Node<Context> createBehaviorTree();
}
