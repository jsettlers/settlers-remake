package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.BehaviorTreeHelper;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.ManageableBearerWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.convertTo;
import static jsettlers.logic.movable.BehaviorTreeHelper.failer;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForNotification;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReachedAndFailIfNotReachable;

/**
 * @author homoroselaps
 */

@Requires({
	MaterialComponent.class,
	BearerComponent.class,
	SteeringComponent.class,
	GameFieldComponent.class,
	AnimationComponent.class,
	MovableComponent.class
})
public final class BearerBehaviorComponent extends BehaviorComponent {
	private static final long serialVersionUID = -4581600901753172458L;

	@Override
	protected Root<Context> createBehaviorTree() {
		return new Root<>(selector(
			triggerGuard(BearerComponent.DeliveryJob.class,
				guard(c -> c.entity.bearerComponent().hasJob(), false,
					accept_SaveDeliveryJob()
				)
			),
			triggerGuard(BearerComponent.BecomeSoldierJob.class,
				guard(c -> c.entity.bearerComponent().hasJob(), false,
					accept_SaveBecomeSoldierJob()
				)
			),
			triggerGuard(BearerComponent.BecomeWorkerJob.class,
				guard(c -> c.entity.bearerComponent().hasJob(), false,
					accept_SaveBecomeWorkerJob()
				)
			),
			guard(c -> c.entity.bearerComponent().hasBecomeWorkerJob(), true,
				selector(
					BehaviorTreeHelper.debug("try to fulfil the job", memSequence(
						BehaviorTreeHelper.debug("grab a tool if needed", BehaviorTreeHelper.guard(c -> c.entity.bearerComponent().materialOffer == null,
							selector(
								memSequence(
									BehaviorTreeHelper.debug("go to the tool", action(c -> {
										c.entity.steeringComponent().setTarget(c.entity.bearerComponent().materialOffer.getPos());
									})),
									waitForTargetReachedAndFailIfNotReachable(),
									BehaviorTreeHelper.debug("can we pick it up?", condition(BearerBehaviorComponent::canTakeMaterial)),
									startAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
									waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
									tryTakeMaterialFromMap(),
									startAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
									waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
								),
								BehaviorTreeHelper.debug("handle failure", sequence(
									action(BearerBehaviorComponent::distributionAborted),
									failer()
								))
							)
						)),
						BehaviorTreeHelper.debug("convert Entity to a worker", action(c -> {
							convertTo(c.entity, c.entity.bearerComponent().workerCreationRequest.requestedMovableType());
						}))
					)),
					BehaviorTreeHelper.debug("handle failure", sequence(
						action(BearerBehaviorComponent::workerCreationRequestFailed),
						action(BearerBehaviorComponent::resetJob),
						failer()
					))
				)
			),
			guard(c -> c.entity.bearerComponent().hasDeliveryJob(), true,
				memSequence(
					selector(
						BehaviorTreeHelper.debug("go to materialOffer and take material", memSequence(
							action(c -> {
								c.entity.steeringComponent().setTarget(c.entity.bearerComponent().materialOffer.getPos());
							}),
							waitForTargetReachedAndFailIfNotReachable(),
							condition(BearerBehaviorComponent::canTakeMaterial),
							startAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
							tryTakeMaterialFromMap(),
							startAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
						)),
						BehaviorTreeHelper.debug("handle failure", sequence(
							action(BearerBehaviorComponent::distributionAborted),
							action(BearerBehaviorComponent::deliveryAborted),
							action(BearerBehaviorComponent::resetJob),
							failer()
						))
					),
					selector(
						BehaviorTreeHelper.debug("go to request & drop material", memSequence(
							action(c -> {
								c.entity.steeringComponent().setTarget(c.entity.bearerComponent().deliveryRequest.getPos());
							}),
							waitForTargetReachedAndFailIfNotReachable(),
							condition(c -> c.entity.bearerComponent().materialType.isDroppable()),
							startAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
							tryFulfillRequest(),
							startAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
							action(BearerBehaviorComponent::resetJob)
						)),
						BehaviorTreeHelper.debug("handle failure", sequence(
							BehaviorTreeHelper.debug("reoffer the material", dropMaterial()),
							action(BearerBehaviorComponent::deliveryAborted),
							action(BearerBehaviorComponent::resetJob),
							failer()
						))
					)
				)
			),
			guard(c -> c.entity.bearerComponent().hasBecomeSoldierJob(), true,
				selector(
					BehaviorTreeHelper.debug("fullfill the job", memSequence(
						action(c -> {
							c.entity.steeringComponent().setTarget(c.entity.bearerComponent().barrack.getDoor());
						}),
						waitForTargetReachedAndFailIfNotReachable(),
						tryTakeWeapon_ConvertToSoldier()
					)),
					BehaviorTreeHelper.debug("handle failure", sequence(
						action(BearerBehaviorComponent::resetJob)
					))
				)
			)
		));
	}

	private static Action<Context> accept_SaveDeliveryJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfType(BearerComponent.DeliveryJob.class, job -> {
				job.offer.distributionAccepted();
				job.request.deliveryAccepted();
				context.entity.bearerComponent().setDeliveryJob(job);
			});
		});
	}

	private static Action<Context> accept_SaveBecomeSoldierJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfType(BearerComponent.BecomeSoldierJob.class, context.entity.bearerComponent()::setBecomeSoldierJob);
		});
	}

	private static Action<Context> accept_SaveBecomeWorkerJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfType(BearerComponent.BecomeWorkerJob.class, job -> {
				job.offer.distributionAccepted();
				context.entity.bearerComponent().setBecomeWorkerJob(job);
			});
		});
	}

	private static Action<Context> tryTakeMaterialFromMap() {
		return new Action<>(c -> {
			EMaterialType materialToTake = c.entity.bearerComponent().materialType;
			if (c.entity.gameFieldComponent().movableGrid.takeMaterial(c.entity.movableComponent().getPos(), materialToTake)) {
				c.entity.materialComponent().setMaterial(materialToTake);
				c.entity.bearerComponent().materialOffer.offerTaken();
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		});
	}

	private static Action<Context> tryTakeWeapon_ConvertToSoldier() {
		return new Action<>(c -> {
			ShortPoint2D targetPosition = c.entity.bearerComponent().barrack.getSoldierTargetPosition();
			EMovableType type = c.entity.bearerComponent().barrack.popWeaponForBearer();
			if (type != null) {
				convertTo(c.entity, type);
				c.entity.steeringComponent().setTarget(targetPosition);
				c.entity.movableComponent().getPlayer().getEndgameStatistic().incrementAmountOfProducedSoldiers();
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		});
	}

	private static Action<Context> tryFulfillRequest() {
		return new Action<>(c -> {
			if (c.entity.bearerComponent().deliveryRequest.isActive() && c.entity.bearerComponent().deliveryRequest.getPos().equals(c.entity.movableComponent().getPos())) {
				c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPos(), c.entity.bearerComponent().materialType, false, false);
				c.entity.bearerComponent().deliveryRequest.deliveryFulfilled();
				c.entity.materialComponent().setMaterial(EMaterialType.NO_MATERIAL);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		});
	}

	private static Action<Context> dropMaterial() {
		return new Action<>(c -> {
			c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPos(), c.entity.bearerComponent().materialType, true, false);
			c.entity.bearerComponent().deliveryRequest.deliveryFulfilled();
			c.entity.materialComponent().setMaterial(EMaterialType.NO_MATERIAL);
		});
	}

	private static boolean canTakeMaterial(Context c) {
		EMaterialType materialToTake = c.entity.bearerComponent().materialType;
		return c.entity.gameFieldComponent().movableGrid.canTakeMaterial(c.entity.movableComponent().getPos(), materialToTake);
	}

	private static void resetJob(Context c) {
		c.entity.bearerComponent().resetJob();
		c.entity.gameFieldComponent().movableGrid.addJobless(new ManageableBearerWrapper(c.entity));
	}

	private static void distributionAborted(Context c) {
		c.entity.bearerComponent().materialOffer.distributionAborted();
	}

	private static void deliveryAborted(Context c) {
		c.entity.bearerComponent().deliveryRequest.deliveryAborted();
	}

	private static void workerCreationRequestFailed(Context c) {
		c.entity.bearerComponent().workerRequester.workerCreationRequestFailed(c.entity.bearerComponent().workerCreationRequest);
	}
}
