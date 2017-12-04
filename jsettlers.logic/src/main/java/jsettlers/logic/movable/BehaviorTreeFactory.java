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

    protected static Action<Context> Action(INodeStatusActionConsumer<Context> action) {
        return new Action<Context>(action);
    }

    protected static Action<Context> Action(INodeStatusActionFunction<Context> action) {
        return new Action<Context>(action);
    }

    protected static Condition<Context> Condition(IBooleanConditionFunction<Context> condition) {
        return new Condition<>(condition);
    }

    protected static Failer<Context> Failer() {
        return new Failer<>();
    }

    protected static Guard<Context> Guard(IBooleanConditionFunction<Context> condition, Node<Context> child) {
        return new Guard<Context>(condition, child);
    }

    protected static Guard<Context> Guard(IBooleanConditionFunction<Context> condition, boolean shouldBe, Node<Context> child) {
        return new Guard<Context>(condition, shouldBe, child);
    }

    protected static Inverter<Context> Inverter(Node<Context> child) {
        return new Inverter<>(child);
    }

    @SafeVarargs
    protected static MemSelector<Context> MemSelector(Node<Context>... children) {
        return new MemSelector<>(children);
    }

    @SafeVarargs
    protected static MemSequence<Context> MemSequence(Node<Context>... children) {
        return new MemSequence<>(children);
    }

    @SafeVarargs
    protected static Parallel<Context> Parallel(Parallel.Policy successPolicy, boolean preemptive, Node<Context>... children) {
        return new Parallel<Context>(successPolicy, preemptive, children);
    }

    protected static Repeat<Context> Repeat(Repeat.Policy policy, Node<Context> condition, Node<Context> child) {
        return new Repeat<>(policy, condition, child);
    }

    protected static Repeat<Context> Repeat(Node<Context> condition, Node<Context> child) {
        return new Repeat<>(condition, child);
    }

    @SafeVarargs
    protected static Selector<Context> Selector(Node<Context>... children) {
        return new Selector<>(children);
    }

    @SafeVarargs
    protected static Sequence<Context> Sequence(Node<Context>... children) {
        return new Sequence<>(children);
    }

    protected static Succeeder<Context> Succeeder() {
        return new Succeeder<>();
    }

    protected static Wait<Context> Wait(Node<Context> condition) {
        return new Wait<>(condition);
    }

    protected static NotificationCondition NotificationCondition(Class<? extends Notification> type) {
        return new NotificationCondition(type);
    }

    protected static NotificationCondition NotificationCondition(Class<? extends Notification> type, boolean consume) {
        return new NotificationCondition(type, consume);
    }

    protected static Node<Context> WaitForTargetReached_FailIfNotReachable() {
        // wait for targetReached, but abort if target not reachable
        return Sequence(
            Inverter(NotificationCondition(SteeringComponent.TargetNotReachedTrigger.class, true)),
            Wait(NotificationCondition(SteeringComponent.TargetReachedTrigger.class, true))
        );
    }

    protected static Property<Context, Boolean> SetAttackableWhile(boolean value, Node<Context> child) {
        return new Property<>(
            (c,v)->{c.entity.attC().isAttackable(v);},
            (c)->{return c.entity.attC().isAttackable();},
            value,
            child);
    }

    protected static Guard<Context> TriggerGuard(Class<? extends Notification> type, Node<Context> child) {
        return new Guard<>(entity -> entity.comp.getNotificationsIt(type).hasNext(), true, child);
    }

    protected static Action<Context> StartAnimation(EMovableAction animation, short duration) {
        return new Action<>(c -> {
            c.entity.aniC().startAnimation(animation, duration);
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

    protected static class Sleep extends Node<Context> {
        private static final long serialVersionUID = 8774557186392581042L;
        boolean done = false;
        int delay = -1;
        public Sleep(int milliseconds) {
            super();
            delay = milliseconds;
        }

        @Override
        protected NodeStatus onTick(Tick<Context> tick) {
            if (done) return NodeStatus.Success;
            tick.Target.getEntity().setInvocationDelay(delay);
            done = true;
            return NodeStatus.Running;
        }

        @Override
        protected void onOpen(Tick<Context> tick) {
            done = false;
        }
    }

    protected static Node<Context> WaitForNotification(Class<? extends Notification> type, boolean consume) {
        return Wait(NotificationCondition(type, consume));
    }

    protected static Debug $(String message, Node<Context> child) {
        return new Debug(message, child);
    }

    protected static Debug $(String message) {
        return new Debug(message);
    }

    protected static class Debug extends Decorator<Context> {
        private static final boolean DEBUG = true;
        private static final long serialVersionUID = 9019598003328102086L;
        private final String message;
        public Debug(String message) {
            super(null);
            this.message = message;
        }

        public Debug(String message, Node<Context> child) {
            super(child);
            this.message = message;
        }

        @Override
        protected NodeStatus onTick(Tick<Context> tick) {
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

    protected static class NotificationCondition extends Condition<Context> {
        private static final long serialVersionUID = 1780756145252644771L;

        public NotificationCondition(Class<? extends Notification> type) {
            this(type, false);
        }

        public NotificationCondition(Class<? extends Notification> type, boolean consume) {
            super((context)->{
                Iterator<? extends Notification> it = context.comp.getNotificationsIt(type);
                if (it.hasNext()) {
                    if (consume) context.comp.consumeNotification(it.next());
                    return true;
                }
                return false;
            });
        }
    }
}
