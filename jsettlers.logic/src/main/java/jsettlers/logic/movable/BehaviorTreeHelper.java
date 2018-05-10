package jsettlers.logic.movable;

import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Debug;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.Inverter;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.NotificationCondition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Parallel;
import jsettlers.logic.movable.simplebehaviortree.nodes.Property;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Succeeder;
import jsettlers.logic.movable.simplebehaviortree.nodes.Wait;

public final class BehaviorTreeHelper {

	/* --- Node Factory --- */

	public static Action<Context> action(INodeStatusActionConsumer<Context> action) {
		return new Action<>(action);
	}

	public static Action<Context> action(INodeStatusActionFunction<Context> action) {
		return new Action<>(action);
	}

	public static Condition<Context> condition(IBooleanConditionFunction<Context> condition) {
		return new Condition<>(condition);
	}

	public static Failer<Context> failer() {
		return new Failer<>();
	}

	public static Succeeder<Context> succeeder() {
		return new Succeeder<>();
	}

	public static Guard<Context> guard(IBooleanConditionFunction<Context> condition, Node<Context> child) {
		return new Guard<>(condition, child);
	}

	public static Guard<Context> guard(IBooleanConditionFunction<Context> condition, boolean shouldBe, Node<Context> child) {
		return new Guard<>(condition, shouldBe, child);
	}

	public static Inverter<Context> inverter(Node<Context> child) {
		return new Inverter<>(child);
	}

	@SafeVarargs
	public static MemSelector<Context> memSelector(Node<Context>... children) {
		return new MemSelector<>(children);
	}

	@SafeVarargs
	public static MemSequence<Context> memSequence(Node<Context>... children) {
		return new MemSequence<>(children);
	}

	@SafeVarargs
	public static Parallel<Context> parallel(Parallel.Policy successPolicy, boolean preemptive, Node<Context>... children) {
		return new Parallel<>(successPolicy, preemptive, children);
	}

	public static Repeat<Context> repeat(Repeat.Policy policy, Node<Context> condition, Node<Context> child) {
		return new Repeat<>(policy, condition, child);
	}

	public static Repeat<Context> repeat(Node<Context> condition, Node<Context> child) {
		return new Repeat<>(condition, child);
	}

	@SafeVarargs
	public static Selector<Context> selector(Node<Context>... children) {
		return new Selector<>(children);
	}

	@SafeVarargs
	public static Sequence<Context> sequence(Node<Context>... children) {
		return new Sequence<>(children);
	}

	public static Wait<Context> wait(Node<Context> condition) {
		return new Wait<>(condition);
	}

	public static NotificationCondition notificationCondition(Class<? extends Notification> type) {
		return new NotificationCondition(type);
	}

	public static NotificationCondition notificationCondition(Class<? extends Notification> type, boolean consume) {
		return new NotificationCondition(type, consume);
	}

	public static Node<Context> waitForTargetReachedAndFailIfNotReachable() {
		return sequence(inverter(notificationCondition(SteeringComponent.TargetNotReachedTrigger.class, true)),
			wait(notificationCondition(SteeringComponent.TargetReachedTrigger.class, true))
		);
	}

	public static Property<Context, Boolean> setAttackableWhile(boolean value, Node<Context> child) {
		return new Property<>(
			(context, v) -> context.entity.attC().IsAttackable(v),
			(context) -> context.entity.attC().IsAttackable(), value, child
		);
	}

	public static Property<Context, Boolean> setIdleBehaviorActiveWhile(boolean value, Node<Context> child) {
		return new Property<>(
			(context, v) -> context.entity.steerC().IsIdleBehaviorActive(v),
			(context) -> context.entity.steerC().IsIdleBehaviorActive(), value, child
		);
	}

	public static Guard<Context> triggerGuard(Class<? extends Notification> type, Node<Context> child) {
		return new Guard<>(entity -> entity.component.getNotificationsIterator(type).hasNext(), true, child);
	}

	public static Action<Context> startAnimation(EMovableAction animation, short duration) {
		return new Action<>(context -> {
			context.entity.getAnimationComponent().startAnimation(animation, duration);
		});
	}

	public static void convertTo(Entity entity, EMovableType type) {
		Entity blueprint = EntityFactory.createEntity(entity.gameC().getMovableGrid(), type, entity.movC().getPos(), entity.movC().getPlayer());
		entity.convertTo(blueprint);
	}

	public static <T> Node<T> alwaysSucceed(Node<T> child) {
		return new Selector<>(child, new Succeeder<>());
	}

	public static Sleep sleep(int milliseconds) {
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
			if (remaining <= 0) { return NodeStatus.SUCCESS; }
			tick.target.entity.setInvocationDelay(remaining);
			return NodeStatus.RUNNING;
		}

		@Override
		public void onOpen(Tick<Context> tick) {
			endTime = MatchConstants.clock().getTime() + delay;
		}
	}

	public static Node<Context> waitForNotification(Class<? extends Notification> type, boolean consume) {
		return wait(notificationCondition(type, consume));
	}

	public static Debug debug(String msg, Node<Context> child) {
		return new Debug(msg, child);
	}

	public static Debug debug(String msg) {
		return new Debug(msg);
	}
}
