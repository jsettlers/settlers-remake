package jsettlers.logic.movable;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.WaitFor;

/**
 * @author homoroselaps
 */

public abstract class BehaviorTreeFactory {
    protected static Node<Entity> WaitForTargetReached_FailIfNot() {
        // wait for targetReached, but abort if target not reachable
        return new Sequence<>(
            TriggerGuard(SteeringComponent.TargetNotReachedTrigger.class, new Failer<>()),
            new WaitFor(SteeringComponent.TargetReachedTrigger.class)
        );
    }

    protected static Guard<Entity> TriggerGuard(Class<? extends Notification> type, Node<Entity> child) {
        return new Guard<>(entity -> entity.getNotificationsIt(type).hasNext(), true, child);
    }

    protected static Action<Entity> StartAnimation(EMovableAction animation, float duration) {
        return new Action<>(t->{ t.get(AnimationComponent.class).startAnimation(animation, duration); });
    }

    protected static void convertTo(Entity entity, EMovableType type) {
        Entity blueprint = EntityFactory.CreateEntity(entity.gameC().getMovableGrid(), type, entity.movC().getPos(), entity.movC().getPlayer());
        entity.convertTo(blueprint);
    }
}
