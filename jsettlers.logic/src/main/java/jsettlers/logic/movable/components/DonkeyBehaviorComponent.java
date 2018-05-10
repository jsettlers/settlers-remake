package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Repeat;
import jsettlers.logic.movable.strategies.trading.IDonkeyMarket;

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
	protected Root<Context> createBehaviorTree() {
		return new Root<>(debug("==<root>==",
			setAttackableWhile(false,
				setIdleBehaviorActiveWhile(false,
					selector(
						triggerGuard(AttackableComponent.ReceivedHit.class,
							debug("received hit", sequence(
								debug("unassign market", action(c -> {
									c.entity.donkeyC().resetMarket();
								})),
								debug("stop going to market", action(c -> {
									c.entity.steerC().resetTarget();
								})),
								debug("drop all materials", repeat(condition(c -> !c.entity.mmatC().isEmpty()), alwaysSucceed(tryDropMaterial())))
							))
						),
						guard(DonkeyBehaviorComponent::hasValidMarket, true,
							selector(
								debug("fulfill request", memSequence(
									debug("go to market", action(c -> {
										c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor());
									})),
									debug("wait for target reached", waitForTargetReachedAndFailIfNotReachable()),
									debug("check for pending transport jobs", condition(c -> c.entity.donkeyC().getMarket().needsDonkey())),
									debug("take material", tryTakeMaterialFromMarket()),
									debug("optionally take a second material", alwaysSucceed(tryTakeMaterialFromMarket())),
									setAttackableWhile(true,
										debug("follow waypoints", repeat(Repeat.Policy.NONPREEMPTIVE,
											condition(c -> c.entity.donkeyC().hasNextWaypoint()),
											memSequence(
												debug("go to next waypoint", action(c -> {
													c.entity.steerC().setTarget(c.entity.donkeyC().peekNextWaypoint());
												})),
												debug("wait", waitForTargetReachedAndFailIfNotReachable()),
												action(c -> {
													c.entity.donkeyC().getNextWaypoint();
												})
											)
										))
									),
									debug("drop all materials", repeat(condition(c -> !c.entity.mmatC().isEmpty()), alwaysSucceed(tryDropMaterial()))),
									selector(
										debug("try find new market", tryFindNewMarket()),
										debug("go back to market", memSequence(
											action(c -> {
												c.entity.steerC().setTarget(c.entity.donkeyC().getMarket().getDoor());
											}),
											alwaysSucceed(debug("wait", waitForTargetReachedAndFailIfNotReachable())),
											action(c -> {
												c.entity.donkeyC().resetMarket();
											})
										))
									)
								)),
								debug("resolve failures", sequence(
									debug("invalidate market", action(c -> {
										c.entity.donkeyC().resetMarket();
									})),
									debug("drop materials", repeat(condition(c -> !c.entity.mmatC().isEmpty()), alwaysSucceed(tryDropMaterial())))
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
			)
		)
		);
	}

	private static Action<Context> tryDropMaterial() {
		return new Action<>(c -> {
			EMaterialType material = c.entity.mmatC().removeMaterial();
			if (material == EMaterialType.NO_MATERIAL) { return NodeStatus.FAILURE; }
			c.entity.gameC().getMovableGrid().dropMaterial(c.entity.movC().getPos(), material, true, true);
			return NodeStatus.SUCCESS;
		});
	}

	private static Action<Context> tryTakeMaterialFromMarket() {
		return new Action<>(c -> {
			EMaterialType material = c.entity.donkeyC().getMarket().tryToTakeDonkeyMaterial();
			if (material == null || material == EMaterialType.NO_MATERIAL) { return NodeStatus.FAILURE; }
			c.entity.mmatC().addMaterial(material);
			return NodeStatus.SUCCESS;
		});
	}

	private static Action<Context> tryFindNewMarket() {
		return new Action<>(c -> {
			IDonkeyMarket market = c.entity.donkeyC().findNextMarketNeedingDonkey();
			if (market == null) { return NodeStatus.FAILURE; }
			c.entity.donkeyC().setMarket(market);
			return NodeStatus.SUCCESS;
		});
	}

	private static boolean hasValidMarket(Context c) {
		return c.entity.donkeyC().getMarket() != null;
	}
}
