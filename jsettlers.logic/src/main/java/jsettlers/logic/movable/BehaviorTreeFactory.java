package jsettlers.logic.movable;

import java.io.Serializable;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.Inverter;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Succeeder;
import jsettlers.logic.movable.simplebehaviortree.nodes.WaitFor;

/**
 * @author homoroselaps
 */

public abstract class BehaviorTreeFactory implements Serializable {
    private static final long serialVersionUID = 8396039806339873520L;

    protected static Node<Entity> WaitForTargetReached_FailIfNot() {
        // wait for targetReached, but abort if target not reachable
        return new Sequence<>(
            new Inverter<>(TriggerCondition(SteeringComponent.TargetNotReachedTrigger.class)),
            new WaitFor(SteeringComponent.TargetReachedTrigger.class, true)
        );
    }

    protected static Guard<Entity> TriggerGuard(Class<? extends Notification> type, Node<Entity> child) {
        return new Guard<>(entity -> entity.getNotificationsIt(type).hasNext(), true, child);
    }

    protected static Action<Entity> StartAnimation(EMovableAction animation, short duration) {
        return new Action<>(e -> {
            e.aniC().startAnimation(animation, duration);
        });
    }

    protected static void convertTo(Entity entity, EMovableType type) {
        Entity blueprint = EntityFactory.CreateEntity(entity.gameC().getMovableGrid(), type, entity.movC().getPos(), entity.movC().getPlayer());
        entity.convertTo(blueprint);
    }

    protected static <T> Node<T> Optional(Node<T> child) {
        return new Selector<T>(child, new Succeeder<T>());
    }

    protected static class Wait extends Node<Entity> {
        private static final long serialVersionUID = 8774557186392581042L;
        boolean done = false;
        int delay = -1;
        public Wait(int milliseconds) {
            super();
            delay = milliseconds;
        }

        @Override
        protected NodeStatus onTick(Tick<Entity> tick) {
            if (done) return NodeStatus.Success;
            tick.Target.setInvocationDelay(delay);
            done = true;
            return NodeStatus.Running();
        }

        @Override
        protected void onOpen(Tick<Entity> tick) {
            done = false;
        }
    }

    protected static Condition<Entity> TriggerCondition(Class<? extends Notification> type) {
        return new Condition<>(entity -> entity.getNotificationsIt(type).hasNext());
    }

    protected static Debug $(String message, Node<Entity> child) {
        return new Debug(message, child);
    }

    protected static Debug $(String message) {
        return new Debug(message);
    }

    protected static class Debug extends Decorator<Entity> {
        private static final boolean DEBUG = true;
        private static final long serialVersionUID = 9019598003328102086L;
        private final String message;
        public Debug(String message) {
            super(null);
            this.message = message;
        }

        public Debug(String message, Node<Entity> child) {
            super(child);
            this.message = message;
        }

        @Override
        protected NodeStatus onTick(Tick<Entity> tick) {
            if (DEBUG) System.out.println(message);
            if (child != null) {
                NodeStatus result = child.execute(tick);
                String res = result == NodeStatus.Success ? "Success" : result == NodeStatus.Failure ? "Failure" : "Running";
                if (DEBUG) System.out.println(message + ": " + res);
                return result;
            }
            return NodeStatus.Success;
        }
    }
}
