package jsettlers.logic.movable;

import java.util.Iterator;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.constants.MatchConstants;
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

public final class BehaviorTreeHelper {

	/* --- Node Factory --- */

	public static Action<Context> Action(INodeStatusActionConsumer<Context> action) {
		return new Action<>(action);
	}

	public static Action<Context> Action(INodeStatusActionFunction<Context> action) {
		return new Action<>(action);
	}

	public static Condition<Context> Condition(IBooleanConditionFunction<Context> condition) {
		return new Condition<>(condition);
	}

	public static Failer<Context> Failer() {
		return new Failer<>();
	}

	public static Guard<Context> Guard(IBooleanConditionFunction<Context> condition, Node<Context> child) {
		return new Guard<>(condition, child);
	}

	public static Guard<Context> Guard(IBooleanConditionFunction<Context> condition, boolean shouldBe, Node<Context> child) {
		return new Guard<>(condition, shouldBe, child);
	}

	public static Inverter<Context> Inverter(Node<Context> child) {
		return new Inverter<>(child);
	}

	@SafeVarargs
	public static MemSelector<Context> MemSelector(Node<Context>... children) {
		return new MemSelector<>(children);
	}

	@SafeVarargs
	public static MemSequence<Context> MemSequence(Node<Context>... children) {
		return new MemSequence<>(children);
	}

	@SafeVarargs
	public static Parallel<Context> Parallel(Parallel.Policy successPolicy, boolean preemptive, Node<Context>... children) {
		return new Parallel<>(successPolicy, preemptive, children);
	}

	public static Repeat<Context> Repeat(Repeat.Policy policy, Node<Context> condition, Node<Context> child) {
		return new Repeat<>(policy, condition, child);
	}

	public static Repeat<Context> Repeat(Node<Context> condition, Node<Context> child) {
		return new Repeat<>(condition, child);
	}

	@SafeVarargs
	public static Selector<Context> Selector(Node<Context>... children) {
		return new Selector<>(children);
	}

	@SafeVarargs
	public static Sequence<Context> Sequence(Node<Context>... children) {
		return new Sequence<>(children);
	}

	public static Succeeder<Context> Succeeder() {
		return new Succeeder<>();
	}

	public static Wait<Context> Wait(Node<Context> condition) {
		return new Wait<>(condition);
	}

	public static NotificationCondition NotificationCondition(Class<? extends Notification> type) {
		return new NotificationCondition(type);
	}

	public static NotificationCondition NotificationCondition(Class<? extends Notification> type, boolean consume) {
		return new NotificationCondition(type, consume);
	}

	public static Node<Context> WaitForTargetReached_FailIfNotReachable() {
		// wait for targetReached, but abort if target not reachable
		return Sequence(Inverter(NotificationCondition(SteeringComponent.TargetNotReachedTrigger.class, true)), Wait(NotificationCondition(SteeringComponent.TargetReachedTrigger.class, true)));
	}

	public static Property<Context, Boolean> SetAttackableWhile(boolean value, Node<Context> child) {
		return new Property<>((c, v) -> c.entity.attC().IsAttackable(v), (c) -> c.entity.attC().IsAttackable(), value, child);
	}

	public static Property<Context, Boolean> SetIdleBehaviorActiveWhile(boolean value, Node<Context> child) {
		return new Property<>((c, v) -> c.entity.steerC().IsIdleBehaviorActive(v), (c) -> c.entity.steerC().IsIdleBehaviorActive(), value, child);
	}

	public static Guard<Context> TriggerGuard(Class<? extends Notification> type, Node<Context> child) {
		return new Guard<>(entity -> entity.comp.getNotificationsIt(type).hasNext(), true, child);
	}

	public static Action<Context> StartAnimation(EMovableAction animation, short duration) {
		return new Action<>(c -> {
			c.entity.aniC().startAnimation(animation, duration);
		});
	}

	public static void convertTo(Entity entity, EMovableType type) {
		Entity blueprint = EntityFactory.CreateEntity(entity.gameC().getMovableGrid(), type, entity.movC().getPos(), entity.movC().getPlayer());
		entity.convertTo(blueprint);
	}

	public static <T> Node<T> Optional(Node<T> child) {
		return new Selector<>(child, new Succeeder<>());
	}

	public static Sleep Sleep(int milliseconds) {
		return new Sleep(milliseconds);
	}

	public static class Sleep extends Node<Context> {
		private static final long serialVersionUID = 8774557186392581042L;
		final                int  delay;
		int endTime;

		public Sleep(int milliseconds) {
			super();
			delay = milliseconds;
		}

		@Override
		public NodeStatus onTick(Tick<Context> tick) {
			int remaining = endTime - MatchConstants.clock().getTime();
			if (remaining <= 0) { return NodeStatus.Success; }
			tick.Target.entity.setInvocationDelay(remaining);
			return NodeStatus.Running;
		}

		@Override
		public void onOpen(Tick<Context> tick) {
			endTime = MatchConstants.clock().getTime() + delay;
		}
	}

	public static Node<Context> WaitForNotification(Class<? extends Notification> type, boolean consume) {
		return Wait(NotificationCondition(type, consume));
	}

	public static Debug $(String msg, Node<Context> child) {
		return new Debug(msg, child);
	}

	public static Debug $(String msg) {
		return new Debug(msg);
	}

	public static class Debug extends Decorator<Context> {
		private static final boolean DEBUG            = false;
		private static final long    serialVersionUID = 9019598003328102086L;
		private final        String  message;

		public Debug(String message) {
			super(null);
			this.message = message;
		}

		public Debug(String message, Node<Context> child) {
			super(child);
			this.message = message;
		}

		@Override
		public NodeStatus onTick(Tick<Context> tick) {
			if (DEBUG) { System.out.println(message); }
			if (child != null) {
				NodeStatus result = child.execute(tick);
				String res = result == NodeStatus.Success ? "Success" : result == NodeStatus.Failure ? "Failure" : "Running";
				if (DEBUG) { System.out.println(message + ": " + res); }
				return result;
			}
			return NodeStatus.Success;
		}
	}

	public static class NotificationCondition extends Condition<Context> {
		private static final long serialVersionUID = 1780756145252644771L;

		public NotificationCondition(Class<? extends Notification> type) {
			this(type, false);
		}

		public NotificationCondition(Class<? extends Notification> type, boolean consume) {
			super((context) -> {
				Iterator<? extends Notification> it = context.comp.getNotificationsIt(type);
				if (it.hasNext()) {
					if (consume) {
						context.comp.consumeNotification(it.next());
					}
					return true;
				}
				return false;
			});
		}
	}
}
