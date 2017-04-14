package jsettlers.logic.movable;

import java.util.function.Function;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Succeeder;
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
        return new Action<>(e->{ e.aniC().startAnimation(animation, duration); });
    }

    protected static void convertTo(Entity entity, EMovableType type) {
        Entity blueprint = EntityFactory.CreateEntity(entity.gameC().getMovableGrid(), type, entity.movC().getPos(), entity.movC().getPlayer());
        entity.convertTo(blueprint);
    }

    protected static <T> Node<T> Optional(Node<T> child) {
        return new Selector<T>(child, new Succeeder<T>());
    }

    protected static class Wait extends Node<Entity> {
        boolean done = false;
        int delay = -1;
        public Wait(int milliseconds) {
            super();
            delay = milliseconds;
        }

        @Override
        protected NodeStatus onTick(Tick<Entity> tick) {
            if (done) return NodeStatus.Success;
            tick.Target.setInvokationDelay(delay);
            done = true;
            return NodeStatus.Running();
        }
    }
}
