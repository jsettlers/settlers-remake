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
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysSucceed;
import jsettlers.logic.movable.simplebehaviortree.nodes.DynamicGuardSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.NotificationCondition;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysFail;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysSucceed;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.convertTo;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.dropMaterial;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.notificationCondition;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.setIdleBehaviorActiveWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.sleep;
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
		return setIdleBehaviorActiveWhile(false,
			selector(
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
							alwaysSucceed(guard("grab a tool if needed", c -> c.entity.bearerComponent().materialOffer != null,
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
							)),
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
								action(c->{c.entity.steeringComponent().setTarget(c.entity.bearerComponent().materialOffer.getPosition());}),
								waitForTargetReachedAndFailIfNotReachable(),
								debug("can take material", condition(BearerBehaviorComponent::canTakeMaterial)),
								startAndWaitForAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION, true),
								tryTakeMaterialFromMap(),
								startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION)
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
								startAndWaitForAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION, true),
								guard(BearerBehaviorComponent::canFulfillRequest, sequence(
									dropMaterial(),
									action(BearerBehaviorComponent::deliveryFulfilled)
								)),
								startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
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
						memSequence("become a soldier",
							action(c -> {
								c.entity.steeringComponent().setTarget(c.entity.bearerComponent().barrack.getDoor());
							}),
							waitForTargetReachedAndFailIfNotReachable(),
							tryTakeWeapon_ConvertToSoldier()
						),
						sequence("handle failure",
							action(BearerBehaviorComponent::bearerRequestFailed),
							action(BearerBehaviorComponent::resetJob),
							alwaysFail()
						)
					)
				),
				debug("idle behavior",
					setIdleBehaviorActiveWhile(true,
						sleep(1000)
					)
				)
			)
		);
	}

	private static Action<Context> acceptDeliveryJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfTypeC(BearerComponent.DeliveryJob.class, job -> {
				job.offer.distributionAccepted();
				job.request.deliveryAccepted();
				context.entity.bearerComponent().setDeliveryJob(job);
			}, true);
		});
	}

	private static Action<Context> acceptBecomeSoldierJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfTypeC(BearerComponent.BecomeSoldierJob.class, context.entity.bearerComponent()::setBecomeSoldierJob, true);
		});
	}

	private static Action<Context> acceptBecomeWorkerJob() {
		return new Action<>(context -> {
			context.component.forFirstNotificationOfTypeC(BearerComponent.BecomeWorkerJob.class, job -> {
				if (job.offer != null) job.offer.distributionAccepted();
				context.entity.bearerComponent().setBecomeWorkerJob(job);
			}, true);
		});
	}

	private static Node<Context> tryTakeMaterialFromMap() {
		return debug("try take material", new Action<>(c -> {
			EMaterialType materialToTake = c.entity.bearerComponent().materialType;
			if (c.entity.gameFieldComponent().movableGrid.takeMaterial(c.entity.movableComponent().getPosition(), materialToTake)) {
				c.entity.materialComponent().setMaterial(materialToTake);
				c.entity.bearerComponent().materialOffer.offerTaken();
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		}));
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

	private static boolean canFulfillRequest(Context c) {
		return c.entity.bearerComponent().deliveryRequest.isActive() && c.entity.bearerComponent().deliveryRequest.getPosition().equals(c.entity.movableComponent().getPosition());
	}

	private static boolean canTakeMaterial(Context c) {
		EMaterialType materialToTake = c.entity.bearerComponent().materialType;
		return c.entity.gameFieldComponent().movableGrid.canTakeMaterial(c.entity.movableComponent().getPosition(), materialToTake);
	}

	private static void bearerRequestFailed(Context c) {
		c.entity.bearerComponent().barrack.bearerRequestFailed();
	}

	private static void resetJob(Context c) {
		c.entity.bearerComponent().resetJob();
		c.entity.gameFieldComponent().movableGrid.addJobless(new ManageableBearerWrapper(c.entity));
	}

	private static void distributionAborted(Context c) {
		c.entity.bearerComponent().materialOffer.distributionAborted();
	}

	private static void deliveryFulfilled(Context c) {
		c.entity.bearerComponent().deliveryRequest.deliveryFulfilled();
	}

	private static void deliveryAborted(Context c) {
		c.entity.bearerComponent().deliveryRequest.deliveryAborted();
	}

	private static void workerCreationRequestFailed(Context c) {
		c.entity.bearerComponent().workerRequester.workerCreationRequestFailed(c.entity.bearerComponent().workerCreationRequest);
	}
}
