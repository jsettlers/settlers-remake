package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Tick;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysFail;
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysRunning;
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysSucceed;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Debug;
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
import jsettlers.logic.movable.simplebehaviortree.nodes.Wait;

public final class BehaviorTreeHelper {

	/* --- Node Factory --- */

	public static Action<Context> action(INodeStatusActionConsumer<Context> action) {
		return new Action<>(action);
	}

	public static Node<Context> action(String debugMessage, INodeStatusActionConsumer<Context> action) {
		return debug(debugMessage, action(action));
	}

	public static Action<Context> action(INodeStatusActionFunction<Context> action) {
		return new Action<>(action);
	}

	public static Node<Context> action(String debugMessage, INodeStatusActionFunction<Context> action) {
		return debug(debugMessage, action(action));
	}

	public static Condition<Context> condition(IBooleanConditionFunction<Context> condition) {
		return new Condition<>(condition);
	}

	public static Node<Context> condition(String debugMessage, IBooleanConditionFunction<Context> condition) {
		return debug(debugMessage, condition(condition));
	}

	public static AlwaysFail<Context> alwaysFail() {
		return new AlwaysFail<>();
	}

	public static AlwaysSucceed<Context> alwaysSucceed() {
		return new AlwaysSucceed<>();
	}

	public static Guard<Context> guard(IBooleanConditionFunction<Context> condition, Node<Context> child) {
		return guard(condition, true, child);
	}

	public static Node<Context> guard(String debugMessage, IBooleanConditionFunction<Context> condition, Node<Context> child) {
		return guard(debugMessage, condition, true, child);
	}

	public static Guard<Context> guard(IBooleanConditionFunction<Context> condition, boolean shouldBe, Node<Context> child) {
		return new Guard<>(condition, shouldBe, child);
	}

	public static Node<Context> guard(String debugMessage, IBooleanConditionFunction<Context> condition, boolean shouldBe, Node<Context> child) {
		return debug(debugMessage, guard(condition, shouldBe, child));
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
	public static Node<Context> memSequence(String debugMessage, Node<Context>... children) {
		return debug(debugMessage, memSequence(children));
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
	public static Node<Context> selector(String debugMessage, Node<Context>... children) {
		return debug(debugMessage, new Selector<>(children));
	}

	@SafeVarargs
	public static Sequence<Context> sequence(Node<Context>... children) {
		return new Sequence<>(children);
	}

	@SafeVarargs
	public static Node<Context> sequence(String debugMessage, Node<Context>... children) {
		return debug(debugMessage, sequence(children));
	}

	public static Wait<Context> wait(Node<Context> condition) {
		return new Wait<>(condition);
	}

	public static <T extends Notification> NotificationCondition notificationCondition(Class<T> type) {
		return new NotificationCondition(type);
	}

	public static <T extends Notification> NotificationCondition notificationCondition(Class<T> type, boolean consume) {
		return new NotificationCondition(type, consume);
	}

	public static <T extends Notification> NotificationCondition notificationCondition(Class<T> type, IBooleanConditionFunction<T> predicate, boolean consume) {
		return new NotificationCondition(type, predicate, consume);
	}

	public static Node<Context> waitForTargetReachedAndFailIfNotReachable() {
		return sequence("waitForTargetReachedAndFailIfNotReachable",
			inverter(
				notificationCondition(SteeringComponent.TargetNotReachedNotification.class, true)
			),
			wait(
				notificationCondition(SteeringComponent.TargetReachedNotification.class, true)
			)
		);
	}

	public static Node<Context> waitForPathFinished(Node<Context> targetReachedChild, Node<Context> targetNotReachedChild) {
		return debug("waitForPathFinished", selector(
			triggerGuard(SteeringComponent.TargetReachedNotification.class, debug("TargetReachedNotification", targetReachedChild)),
			triggerGuard(SteeringComponent.TargetNotReachedNotification.class, debug("TargetNotReachedNotification", targetNotReachedChild)),
			debug("path not finished yet", new AlwaysRunning<>())
		));
	}

	public static Node<Context> waitForPathFinished(Node<Context> targetReachedChild, Node<Context> targetNotReachedChild, Node<Context> whenPathFinished) {
		return sequence(
			waitForPathFinished(targetReachedChild, targetNotReachedChild),
			debug("path finished", whenPathFinished)
		);
	}

	public static Property<Context, Boolean> setAttackableWhile(boolean value, Node<Context> child) {
		return new Property<>(
			(context, v) -> context.entity.attackableComponent().isAttackable(v),
			(context) -> context.entity.attackableComponent().isAttackable(), value, child
		);
	}

	public static Node<Context> defaultIdleBehavior() {
		return debug("idle behavior",
			setIdleBehaviorActiveWhile(true,
				alwaysSucceed()
			)
		);
	}

	public static Property<Context, Boolean> setIdleBehaviorActiveWhile(boolean value, Node<Context> child) {
		return new Property<>(
			(context, v) -> context.entity.steeringComponent().IsIdleBehaviorActive(v),
			(context) -> context.entity.steeringComponent().IsIdleBehaviorActive(), value, child
		);
	}

	public static Guard<Context> triggerGuard(Class<? extends Notification> type, Node<Context> child) {
		return new Guard<>(entity -> entity.component.hasNotificationOfType(type), true, child);
	}

	public static Action<Context> startAnimation(EMovableAction animation, short duration, boolean isChained) {
		return new Action<>(context -> {
			context.entity.getAnimationComponent().startAnimation(animation, duration, isChained);
		});
	}

	public static Node<Context> startAndWaitForAnimation(EMovableAction animation, short duration) {
		return startAndWaitForAnimation(animation, duration, false);
	}

	public static Node<Context> startAndWaitForAnimation(EMovableAction animation, short duration, boolean isChained) {
		return memSequence("startAndWaitForAnimation with " + animation + " for " + duration + "ms",
			debug("start animation", startAnimation(animation, duration, isChained)),
			debug("wait for animation to finish", waitForNotification(AnimationComponent.AnimationFinishedNotification.class, n -> n.type == animation, true))
		);
	}

	public static void convertTo(Entity entity, EMovableType type) {
		Entity blueprint = EntityFactory.createEntity(entity.gameFieldComponent().movableGrid, type, entity.movableComponent().getPosition(), entity.movableComponent().getPlayer());
		entity.convertTo(blueprint);
	}

	public static <T> Node<T> alwaysSucceed(Node<T> child) {
		return new Selector<>(child, new AlwaysSucceed<>());
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

	public static <T extends Notification> Node<Context> waitForNotification(Class<T> type, IBooleanConditionFunction<T> predicate, boolean consume) {
		return wait(notificationCondition(type, predicate, consume));
	}

	public static Debug debug(String msg, Node<Context> child) {
		return new Debug(msg, child);
	}

	public static Debug debug(String msg) {
		return new Debug(msg);
	}

	public static Action<Context> dropMaterial() {
		return new Action<>(c -> {
			if (c.entity.materialComponent().getMaterial().isDroppable()) {
				c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPosition(), c.entity.materialComponent().getMaterial(), true, false);
			}
			c.entity.materialComponent().setMaterial(EMaterialType.NO_MATERIAL);
		});
	}
}
