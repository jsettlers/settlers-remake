package jsettlers.logic.movable.components;

import java8.util.Optional;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.strategies.trading.ITradeBuilding;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysSucceed;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.repeat;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.setAttackableWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.setIdleBehaviorActiveWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.sleep;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReachedAndFailIfNotReachable;

/**
 * @author homoroselaps
 */

@Requires({
	MultiMaterialComponent.class,
	DonkeyComponent.class,
	SteeringComponent.class,
	AttackableComponent.class,
	GameFieldComponent.class,
	AnimationComponent.class,
	MovableComponent.class
})
public final class DonkeyBehaviorComponent extends BehaviorComponent {
	private static final long serialVersionUID = -9105595769767841134L;

	@Override
	protected Node<Context> createBehaviorTree() {
		return setAttackableWhile(false,
			setIdleBehaviorActiveWhile(false,
				selector(
					triggerGuard(AttackableComponent.ReceivedHit.class,
						debug("received hit", sequence(
							debug("unassign market", action(c -> {
								c.entity.donkeyComponent().resetMarket();
							})),
							debug("stop going to market", action(c -> {
								c.entity.steeringComponent().resetTarget();
							})),
							debug("drop all materials", repeat(condition(c -> !c.entity.multiMaterialComponent().isEmpty()), alwaysSucceed(tryDropMaterial())))
						))
					),
					guard(DonkeyBehaviorComponent::hasValidMarket, true,
						selector(
							debug("fulfill request", memSequence(
								debug("go to market", action(c -> {
									c.entity.steeringComponent().setTarget(c.entity.donkeyComponent().getMarket().getPickUpPosition());
								})),
								debug("wait for target reached", waitForTargetReachedAndFailIfNotReachable()),
								debug("check for pending transport jobs", condition(c -> c.entity.donkeyComponent().getMarket().needsTrader())),
								debug("take material", tryTakeMaterialFromMarket()),
								debug("optionally take a second material", alwaysSucceed(tryTakeMaterialFromMarket())),
								setAttackableWhile(true,
									debug("follow waypoints", repeat(Repeat.Policy.NONPREEMPTIVE,
										condition(c -> c.entity.donkeyComponent().hasNextWaypoint()),
										memSequence(
											debug("go to next waypoint", action(c -> {
												c.entity.steeringComponent().setTarget(c.entity.donkeyComponent().peekNextWaypoint());
											})),
											debug("wait", waitForTargetReachedAndFailIfNotReachable()),
											action(c -> {
												c.entity.donkeyComponent().getNextWaypoint();
											})
										)
									))
								),
								debug("drop all materials", repeat(condition(c -> !c.entity.multiMaterialComponent().isEmpty()), alwaysSucceed(tryDropMaterial()))),
								selector(
									debug("try find new market", tryFindNewMarket()),
									debug("go back to market", memSequence(
										action(c -> {
											c.entity.steeringComponent().setTarget(c.entity.donkeyComponent().getMarket().getPickUpPosition());
										}),
										alwaysSucceed(debug("wait", waitForTargetReachedAndFailIfNotReachable())),
										action(c -> {
											c.entity.donkeyComponent().resetMarket();
										})
									))
								)
							)),
							debug("resolve failures", sequence(
								debug("invalidate market", action(c -> {
									c.entity.donkeyComponent().resetMarket();
								})),
								debug("drop materials", repeat(condition(c -> !c.entity.multiMaterialComponent().isEmpty()), alwaysSucceed(tryDropMaterial())))
							))
						)
					),
					// if no market in need then wait for second
					guard(DonkeyBehaviorComponent::hasValidMarket, false,
						setIdleBehaviorActiveWhile(true,
							debug("search for valid market", new MemSelector<>(
								tryFindNewMarket(),
								sleep(1000)
							))
						)
					)
				)
			)
		);
	}

	private static Action<Context> tryDropMaterial() {
		return new Action<>(c -> {
			EMaterialType material = c.entity.multiMaterialComponent().removeMaterial();
			if (material == EMaterialType.NO_MATERIAL) { return NodeStatus.FAILURE; }
			c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPosition(), material, true, true);
			return NodeStatus.SUCCESS;
		});
	}

	private static Action<Context> tryTakeMaterialFromMarket() {
		return new Action<>(c -> {
			Optional<ITradeBuilding.MaterialTypeWithCount> material = c.entity.donkeyComponent().getMarket().tryToTakeMaterial(1);
			if (!material.isPresent()) { return NodeStatus.FAILURE; }
			c.entity.multiMaterialComponent().addMaterial(material.get().materialType);
			return NodeStatus.SUCCESS;
		});
	}

	private static Action<Context> tryFindNewMarket() {
		return new Action<>(c -> {
			ITradeBuilding market = c.entity.donkeyComponent().findTradeBuildingWithWork();
			if (market == null) { return NodeStatus.FAILURE; }
			c.entity.donkeyComponent().setMarket(market);
			return NodeStatus.SUCCESS;
		});
	}

	private static boolean hasValidMarket(Context c) {
		return c.entity.donkeyComponent().getMarket() != null;
	}
}
