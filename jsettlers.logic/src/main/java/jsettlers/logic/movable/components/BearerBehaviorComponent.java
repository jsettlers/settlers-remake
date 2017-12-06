package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.BehaviorTreeHelper.*;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.ManageableBearerWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;

import static jsettlers.logic.movable.BehaviorTreeHelper.$;
import static jsettlers.logic.movable.BehaviorTreeHelper.Condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.Failer;
import static jsettlers.logic.movable.BehaviorTreeHelper.Guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.MemSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.Selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.Sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.Action;
import static jsettlers.logic.movable.BehaviorTreeHelper.StartAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.TriggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.WaitForNotification;
import static jsettlers.logic.movable.BehaviorTreeHelper.WaitForTargetReached_FailIfNotReachable;
import static jsettlers.logic.movable.BehaviorTreeHelper.convertTo;

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
    protected Root<Context> CreateBehaviorTree() {
        return new Root<>(Selector(
            TriggerGuard(BearerComponent.DeliveryJob.class,
                Guard(c -> c.entity.bearerC().hasJob(), false,
                    accept_SaveDeliveryJob())
            ),
            TriggerGuard(BearerComponent.BecomeSoldierJob.class,
                Guard(c -> c.entity.bearerC().hasJob(), false,
                    accept_SaveBecomeSoldierJob())
            ),
            TriggerGuard(BearerComponent.BecomeWorkerJob.class,
                Guard(c -> c.entity.bearerC().hasJob(), false,
                    accept_SaveBecomeWorkerJob())
            ),
            Guard(c -> c.entity.bearerC().hasBecomeWorkerJob(), true,
                Selector(
                    $("try to fulfil the job", MemSequence(
                        $("grab a tool if needed", Guard(c -> c.entity.bearerC().materialOffer == null,
                            Selector(
                                MemSequence(
                                    $("go to the tool", Action(c -> { c.entity.steerC().setTarget(c.entity.bearerC().materialOffer.getPos()); })),
                                    WaitForTargetReached_FailIfNotReachable(),
                                    $("can we pick it up?", Condition(BearerBehaviorComponent::canTakeMaterial)),
                                    StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                                    WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                                    tryTakeMaterialFromMap(),
                                    StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                                    WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
                                ),
                                $("handle failure", Sequence(
                                    Action(BearerBehaviorComponent::distributionAborted),
                                    Failer()
                                ))
                            )
                        )),
                        $("convert Entity to a worker", Action(c->{ convertTo(c.entity, c.entity.bearerC().workerCreationRequest.requestedMovableType()); }))
                    )),
                    $("handle failure", Sequence(
                        Action(BearerBehaviorComponent::workerCreationRequestFailed),
                        Action(BearerBehaviorComponent::resetJob),
                        Failer()
                    ))
                )
            ),
            Guard(c -> c.entity.bearerC().hasDeliveryJob(), true,
                MemSequence(
                    Selector(
                        $("go to materialOffer and take material", MemSequence(
                            Action(c -> {
                                c.entity.steerC().setTarget(c.entity.bearerC().materialOffer.getPos());
                            }),
                            WaitForTargetReached_FailIfNotReachable(),
                            Condition(BearerBehaviorComponent::canTakeMaterial),
                            StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                            WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                            tryTakeMaterialFromMap(),
                            StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                            WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
                        )),
                        $("handle failure", Sequence(
                            Action(BearerBehaviorComponent::distributionAborted),
                            Action(BearerBehaviorComponent::deliveryAborted),
                            Action(BearerBehaviorComponent::resetJob),
                            Failer()
                        ))
                    ),
                    Selector(
                        $("go to request & drop material", MemSequence(
                            Action(c -> { c.entity.steerC().setTarget(c.entity.bearerC().deliveryRequest.getPos()); }),
                            WaitForTargetReached_FailIfNotReachable(),
                            Condition(c -> c.entity.bearerC().materialType.isDroppable()),
                            StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                            WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                            tryFulfillRequest(),
                            StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                            WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                            Action(BearerBehaviorComponent::resetJob)
                        )),
                        $("handle failure", Sequence(
                            $("reoffer the material", dropMaterial()),
                            Action(BearerBehaviorComponent::deliveryAborted),
                            Action(BearerBehaviorComponent::resetJob),
                            Failer()
                        ))
                    )
                )
            ),
            Guard(c -> c.entity.bearerC().hasBecomeSoldierJob(), true,
                Selector(
                    $("fullfill the job", MemSequence(
                        Action(c -> { c.entity.steerC().setTarget(c.entity.bearerC().barrack.getDoor()); }),
                        WaitForTargetReached_FailIfNotReachable(),
                        tryTakeWeapon_ConvertToSoldier()
                    )),
                    $("handle failure", Sequence(
                        Action(BearerBehaviorComponent::resetJob)
                    ))
                )
            )
        ));
    }

    private static Action<Context> accept_SaveDeliveryJob() {
        return new Action<>(c -> {
            BearerComponent.DeliveryJob job = c.comp.getNotificationsIt(BearerComponent.DeliveryJob.class).next();
            job.offer.distributionAccepted();
            job.request.deliveryAccepted();
            c.entity.bearerC().setDeliveryJob(job);
        });
    }

    private static Action<Context> accept_SaveBecomeSoldierJob() {
        return new Action<>(c -> {
            BearerComponent.BecomeSoldierJob job = c.comp.getNotificationsIt(BearerComponent.BecomeSoldierJob.class).next();
            c.entity.bearerC().setBecomeSoldierJob(job);
        });
    }

    private static Action<Context> accept_SaveBecomeWorkerJob() {
        return new Action<>(c -> {
            BearerComponent.BecomeWorkerJob job = c.comp.getNotificationsIt(BearerComponent.BecomeWorkerJob.class).next();
            job.offer.distributionAccepted();
            c.entity.bearerC().setBecomeWorkerJob(job);
        });
    }

    private static Action<Context> tryTakeMaterialFromMap() {
        return new Action<>(c -> {
            EMaterialType materialToTake = c.entity.bearerC().materialType;
            if (c.entity.gameC().getMovableGrid().takeMaterial(c.entity.movC().getPos(), materialToTake)) {
                c.entity.matC().setMaterial(materialToTake);
                c.entity.bearerC().materialOffer.offerTaken();
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
        });
    }

    private static Action<Context> tryTakeWeapon_ConvertToSoldier() {
        return new Action<>(c -> {
            ShortPoint2D targetPosition = c.entity.bearerC().barrack.getSoldierTargetPosition();
            EMovableType type = c.entity.bearerC().barrack.popWeaponForBearer();
            if (type != null) {
                convertTo(c.entity, type);
                c.entity.steerC().setTarget(targetPosition);
                c.entity.movC().getPlayer().getEndgameStatistic().incrementAmountOfProducedSoldiers();
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
        });
    }

    private static Action<Context> tryFulfillRequest() {
        return new Action<>(c -> {
            if (c.entity.bearerC().deliveryRequest.isActive() && c.entity.bearerC().deliveryRequest.getPos().equals(c.entity.movC().getPos())) {
                c.entity.gameC().getMovableGrid().dropMaterial(c.entity.movC().getPos(), c.entity.bearerC().materialType, false, false);
                c.entity.bearerC().deliveryRequest.deliveryFulfilled();
                c.entity.matC().setMaterial(EMaterialType.NO_MATERIAL);
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
        });
    }

    private static Action<Context> dropMaterial() {
        return new Action<>(c -> {
            c.entity.gameC().getMovableGrid().dropMaterial(c.entity.movC().getPos(), c.entity.bearerC().materialType, true, false);
            c.entity.bearerC().deliveryRequest.deliveryFulfilled();
            c.entity.matC().setMaterial(EMaterialType.NO_MATERIAL);
        });
    }

    private static boolean canTakeMaterial(Context c) {
        EMaterialType materialToTake = c.entity.bearerC().materialType;
        return c.entity.gameC().getMovableGrid().canTakeMaterial(c.entity.movC().getPos(), materialToTake);
    }

    private static void resetJob(Context c) {
        c.entity.bearerC().resetJob();
        c.entity.gameC().getMovableGrid().addJobless(new ManageableBearerWrapper(c.entity));
    }

    private static void distributionAborted(Context c) {
        c.entity.bearerC().materialOffer.distributionAborted();
    }

    private static void deliveryAborted(Context c) {
        c.entity.bearerC().deliveryRequest.deliveryAborted();
    }

    private static void workerCreationRequestFailed(Context c) {
        c.entity.bearerC().workerRequester.workerCreationRequestFailed(c.entity.bearerC().workerCreationRequest);
    }
}
