package jsettlers.logic.movable;

import java.io.Serializable;
import java.util.Iterator;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.Decorator;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.Inverter;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Parallel;
import jsettlers.logic.movable.simplebehaviortree.nodes.Property;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Succeeder;
import jsettlers.logic.movable.simplebehaviortree.nodes.Wait;

public abstract class BehaviorTreeFactory implements Serializable {
    private static final long serialVersionUID = 8396039806339873520L;

    /* --- Node Factory --- */

    protected static Action<Entity> Action(INodeStatusActionConsumer<Entity> action) {
        return new Action<Entity>(action);
    }

    protected static Action<Entity> Action(INodeStatusActionFunction<Entity> action) {
        return new Action<Entity>(action);
    }

    protected static Condition<Entity> Condition(IBooleanConditionFunction<Entity> condition) {
        return new Condition<>(condition);
    }

    protected static Failer<Entity> Failer() {
        return new Failer<>();
    }

    protected static Guard<Entity> Guard(IBooleanConditionFunction<Entity> condition, Node<Entity> child) {
        return new Guard<Entity>(condition, child);
    }

    protected static Guard<Entity> Guard(IBooleanConditionFunction<Entity> condition, boolean shouldBe, Node<Entity> child) {
        return new Guard<Entity>(condition, shouldBe, child);
    }

    protected static Inverter<Entity> Inverter(Node<Entity> child) {
        return new Inverter<>(child);
    }

    @SafeVarargs
    protected static MemSelector<Entity> MemSelector(Node<Entity>... children) {
        return new MemSelector<>(children);
    }

    @SafeVarargs
    protected static MemSequence<Entity> MemSequence(Node<Entity>... children) {
        return new MemSequence<>(children);
    }

    @SafeVarargs
    protected static Parallel<Entity> Parallel(Parallel.Policy successPolicy, boolean preemptive, Node<Entity>... children) {
        return new Parallel<Entity>(successPolicy, preemptive, children);
    }

    protected static Repeat<Entity> Repeat(Repeat.Policy policy, Node<Entity> condition, Node<Entity> child) {
        return new Repeat<>(policy, condition, child);
    }

    protected static Repeat<Entity> Repeat(Node<Entity> condition, Node<Entity> child) {
        return new Repeat<>(condition, child);
    }

    @SafeVarargs
    protected static Selector<Entity> Selector(Node<Entity>... children) {
        return new Selector<>(children);
    }

    @SafeVarargs
    protected static Sequence<Entity> Sequence(Node<Entity>... children) {
        return new Sequence<>(children);
    }

    protected static Succeeder<Entity> Succeeder() {
        return new Succeeder<>();
    }

    protected static Wait<Entity> Wait(Node<Entity> condition) {
        return new Wait<>(condition);
    }

    protected static NotificationCondition NotificationCondition(Class<? extends Notification> type) {
        return new NotificationCondition(type);
    }

    protected static NotificationCondition NotificationCondition(Class<? extends Notification> type, boolean consume) {
        return new NotificationCondition(type, consume);
    }

    protected static Node<Entity> WaitForTargetReached_FailIfNotReachable() {
        // wait for targetReached, but abort if target not reachable
        return Sequence(
            Inverter(NotificationCondition(SteeringComponent.TargetNotReachedTrigger.class, true)),
            Wait(NotificationCondition(SteeringComponent.TargetReachedTrigger.class, true))
        );
    }

    protected static Property<Entity, Boolean> SetAttackableWhile(boolean value, Node<Entity> child) {
        return new Property<>(
            (e,v)->{e.attC().isAttackable(v);},
            (e)->{return e.attC().isAttackable();},
            value,
            child);
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

    protected static Sleep Sleep(int milliseconds) {
        return new Sleep(milliseconds);
    }

    protected static class Sleep extends Node<Entity> {
        private static final long serialVersionUID = 8774557186392581042L;
        boolean done = false;
        int delay = -1;
        public Sleep(int milliseconds) {
            super();
            delay = milliseconds;
        }

        @Override
        protected NodeStatus onTick(Tick<Entity> tick) {
            if (done) return NodeStatus.Success;
            tick.Target.setInvocationDelay(delay);
            done = true;
            return NodeStatus.Running;
        }

        @Override
        protected void onOpen(Tick<Entity> tick) {
            done = false;
        }
    }

    protected static Node<Entity> WaitForNotification(Class<? extends Notification> type, boolean consume) {
        return Wait(NotificationCondition(type, consume));
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

    protected static class NotificationCondition extends Condition<Entity> {
        private static final long serialVersionUID = 1780756145252644771L;

        public NotificationCondition(Class<? extends Notification> type) {
            this(type, false);
        }

        public NotificationCondition(Class<? extends Notification> type, boolean consume) {
            super((entity)->{
                Iterator<? extends Notification> it = entity.getNotificationsIt(type);
                if (it.hasNext()) {
                    if (consume) entity.consumeNotification(it.next());
                    return true;
                }
                return false;
            });
        }
    }
}
