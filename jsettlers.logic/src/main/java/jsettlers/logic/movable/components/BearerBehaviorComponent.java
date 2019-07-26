package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.ManageableBearerWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.DynamicGuardSelector;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysFail;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.convertTo;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAndWaitForAnimation;
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
	protected Node<Context> createBehaviorTree() {
		return new DynamicGuardSelector<>(
			triggerGuard(BearerComponent.DeliveryJob.class,
				guard("DeliveryJob", c -> c.entity.bearerComponent().hasJob(), false,
					debug("accepting delivery job", acceptDeliveryJob())
				)
			),
			triggerGuard(BearerComponent.BecomeSoldierJob.class,
				guard("BecomeSoldierJob", c -> c.entity.bearerComponent().hasJob(), false,
					debug("accepting become soldier job", acceptBecomeSoldierJob())
				)
			),
			triggerGuard(BearerComponent.BecomeWorkerJob.class,
				guard("BecomeWorkerJob", c -> c.entity.bearerComponent().hasJob(), false,
					debug("accepting become worker job", acceptBecomeWorkerJob())
				)
			),
			guard(c -> c.entity.bearerComponent().hasBecomeWorkerJob(), true,
				selector("hasBecomeWorkerJob",
					memSequence("try to fulfil the job",
						guard("grab a tool if needed", c -> c.entity.bearerComponent().materialOffer == null,
							selector(
								memSequence(
									action("go to the tool", c -> {
										c.entity.steeringComponent().setTarget(c.entity.bearerComponent().materialOffer.getPosition());
									}),
									waitForTargetReachedAndFailIfNotReachable(),
									condition("can we pick it up?", BearerBehaviorComponent::canTakeMaterial),
									startAndWaitForAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
									tryTakeMaterialFromMap(),
									startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION)
								),
								sequence("handle failure",
									action(BearerBehaviorComponent::distributionAborted),
									alwaysFail()
								)
							)
						),
						action("convert Entity to a worker", c -> {
							convertTo(c.entity, c.entity.bearerComponent().workerCreationRequest.requestedMovableType());
						})
					),
					sequence("handle failure",
						action(BearerBehaviorComponent::workerCreationRequestFailed),
						action(BearerBehaviorComponent::resetJob),
						alwaysFail()
					)
				)
			),
			guard(c -> c.entity.bearerComponent().hasDeliveryJob(), true,
				memSequence(
					selector(
						memSequence("go to materialOffer and take material",
							action(c -> {
								c.entity.steeringComponent().setTarget(c.entity.bearerComponent().materialOffer.getPosition());
							}),
							waitForTargetReachedAndFailIfNotReachable(),
							condition(BearerBehaviorComponent::canTakeMaterial),
							startAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true),
							tryTakeMaterialFromMap(),
							startAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true)
						),
						sequence("handle failure",
							action(BearerBehaviorComponent::distributionAborted),
							action(BearerBehaviorComponent::deliveryAborted),
							action(BearerBehaviorComponent::resetJob),
							alwaysFail()
						)
					),
					selector(
						memSequence("go to request & drop material",
							action(c -> {
								c.entity.steeringComponent().setTarget(c.entity.bearerComponent().deliveryRequest.getPosition());
							}),
							waitForTargetReachedAndFailIfNotReachable(),
							condition(c -> c.entity.bearerComponent().materialType.isDroppable()),
							startAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true),
							tryFulfillRequest(),
							startAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
							waitForNotification(AnimationComponent.AnimationFinishedNotification.class, true),
							action(BearerBehaviorComponent::resetJob)
						),
						sequence("handle failure",
							debug("reoffer the material", dropMaterial()),
							action(BearerBehaviorComponent::deliveryAborted),
							action(BearerBehaviorComponent::resetJob),
							alwaysFail()
						)
					)
				)
			),
			guard(c -> c.entity.bearerComponent().hasBecomeSoldierJob(), true,
				selector(
					memSequence("fullfill the job",
						action(c -> {
							c.entity.steeringComponent().setTarget(c.entity.bearerComponent().barrack.getDoor());
						}),
						waitForTargetReachedAndFailIfNotReachable(),
						tryTakeWeapon_ConvertToSoldier()
					),
					sequence("handle failure",
						action(BearerBehaviorComponent::resetJob)
					)
				)
			)
		);
	}

	private static Action<Context> acceptDeliveryJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfType(BearerComponent.DeliveryJob.class, job -> {
				job.offer.distributionAccepted();
				job.request.deliveryAccepted();
				context.entity.bearerComponent().setDeliveryJob(job);
			});
		});
	}

	private static Action<Context> acceptBecomeSoldierJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfType(BearerComponent.BecomeSoldierJob.class, context.entity.bearerComponent()::setBecomeSoldierJob);
		});
	}

	private static Action<Context> acceptBecomeWorkerJob() {
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
			if (c.entity.gameFieldComponent().movableGrid.takeMaterial(c.entity.movableComponent().getPosition(), materialToTake)) {
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
			if (c.entity.bearerComponent().deliveryRequest.isActive() && c.entity.bearerComponent().deliveryRequest.getPosition().equals(c.entity.movableComponent().getPosition())) {
				c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPosition(), c.entity.bearerComponent().materialType, false, false);
				c.entity.bearerComponent().deliveryRequest.deliveryFulfilled();
				c.entity.materialComponent().setMaterial(EMaterialType.NO_MATERIAL);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		});
	}

	private static Action<Context> dropMaterial() {
		return new Action<>(c -> {
			c.entity.gameFieldComponent().movableGrid.dropMaterial(c.entity.movableComponent().getPosition(), c.entity.bearerComponent().materialType, true, false);
			c.entity.bearerComponent().deliveryRequest.deliveryFulfilled();
			c.entity.materialComponent().setMaterial(EMaterialType.NO_MATERIAL);
		});
	}

	private static boolean canTakeMaterial(Context c) {
		EMaterialType materialToTake = c.entity.bearerComponent().materialType;
		return c.entity.gameFieldComponent().movableGrid.canTakeMaterial(c.entity.movableComponent().getPosition(), materialToTake);
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
