package jsettlers.logic.movable.components;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.BehaviorTreeFactory;
import jsettlers.logic.movable.Entity;
import jsettlers.logic.movable.ManageableBearerWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Condition;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSelector;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;

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

    public BearerBehaviorComponent() {
        super(BearerBehaviorTreeFactory.create());
    }

    private static abstract class BearerBehaviorTreeFactory extends BehaviorTreeFactory {
        private static final long serialVersionUID = -3944410718150713949L;

        private static Action<Entity> accept_SaveDeliveryJob() {
            return new Action<>(e -> {
                BearerComponent.DeliveryJob job = e.getNotificationsIt(BearerComponent.DeliveryJob.class).next();
                job.offer.distributionAccepted();
                job.request.deliveryAccepted();
                e.get(BearerComponent.class).setDeliveryJob(job);
            });
        }

        private static Action<Entity> accept_SaveBecomeSoldierJob() {
            return new Action<>(e -> {
                BearerComponent.BecomeSoldierJob job = e.getNotificationsIt(BearerComponent.BecomeSoldierJob.class).next();
                e.get(BearerComponent.class).setBecomeSoldierJob(job);
            });
        }

        private static Action<Entity> accept_SaveBecomeWorkerJob() {
            return new Action<>(e -> {
                BearerComponent.BecomeWorkerJob job = e.getNotificationsIt(BearerComponent.BecomeWorkerJob.class).next();
                job.offer.distributionAccepted();
                e.get(BearerComponent.class).setBecomeWorkerJob(job);
            });
        }

        private static Action<Entity> tryTakeMaterialFromMap() {
            return new Action<>(e -> {
                EMaterialType materialToTake = e.bearerC().materialType;
                if (e.gameC().getMovableGrid().takeMaterial(e.movC().getPos(), materialToTake)) {
                    e.matC().setMaterial(materialToTake);
                    e.bearerC().materialOffer.offerTaken();
                    return NodeStatus.Success;
                }
                return NodeStatus.Failure;
            });
        }

        private static Action<Entity> tryTakeWeapon_ConvertToSoldier() {
            return new Action<>(e -> {
                ShortPoint2D targetPosition = e.bearerC().barrack.getSoldierTargetPosition();
                EMovableType type = e.bearerC().barrack.popWeaponForBearer();
                if (type != null) {
                    convertTo(e, type);
                    e.steerC().setTarget(targetPosition);
                    e.movC().getPlayer().getEndgameStatistic().incrementAmountOfProducedSoldiers();
                    return NodeStatus.Success;
                }
                return NodeStatus.Failure;
            });
        }

        private static Action<Entity> tryFulfillRequest() {
            return new Action<>(e -> {
                if (e.bearerC().deliveryRequest.isActive() && e.bearerC().deliveryRequest.getPos().equals(e.movC().getPos())) {
                    e.gameC().getMovableGrid().dropMaterial(e.movC().getPos(), e.bearerC().materialType, false, false);
                    e.bearerC().deliveryRequest.deliveryFulfilled();
                    e.matC().setMaterial(EMaterialType.NO_MATERIAL);
                    return NodeStatus.Success;
                }
                return NodeStatus.Failure;
            });
        }

        private static Action<Entity> dropMaterial() {
            return new Action<>(e -> {
                e.gameC().getMovableGrid().dropMaterial(e.movC().getPos(), e.bearerC().materialType, true, false);
                e.bearerC().deliveryRequest.deliveryFulfilled();
                e.matC().setMaterial(EMaterialType.NO_MATERIAL);
            });
        }

        private static boolean canTakeMaterial(Entity entity) {
            EMaterialType materialToTake = entity.bearerC().materialType;
            return entity.gameC().getMovableGrid().canTakeMaterial(entity.movC().getPos(), materialToTake);
        }

        private static void resetJob(Entity entity) {
            entity.bearerC().resetJob();
            entity.gameC().getMovableGrid().addJobless(new ManageableBearerWrapper(entity));
        }

        private static void distributionAborted(Entity entity) {
            entity.bearerC().materialOffer.distributionAborted();
        }

        private static void deliveryAborted(Entity entity) {
            entity.bearerC().deliveryRequest.deliveryAborted();
        }

        private static void workerCreationRequestFailed(Entity entity) {
            entity.bearerC().workerRequester.workerCreationRequestFailed(entity.bearerC().workerCreationRequest);
        }

        public static Root<Entity> create() {
            return new Root<>(Selector(
                TriggerGuard(BearerComponent.DeliveryJob.class,
                    Guard(e -> e.bearerC().hasJob(), false,
                        accept_SaveDeliveryJob())
                ),
                TriggerGuard(BearerComponent.BecomeSoldierJob.class,
                    Guard(e -> e.bearerC().hasJob(), false,
                        accept_SaveBecomeSoldierJob())
                ),
                TriggerGuard(BearerComponent.BecomeWorkerJob.class,
                    Guard(e -> e.bearerC().hasJob(), false,
                        accept_SaveBecomeWorkerJob())
                ),
                Guard(e -> e.bearerC().hasBecomeWorkerJob(), true,
                    Selector(
                        $("try to fulfil the job", MemSequence(
                            $("grab a tool if needed", Guard(e -> e.bearerC().materialOffer == null,
                                Selector(
                                    MemSequence(
                                        $("go to the tool", Action(e -> { e.steerC().setTarget(e.bearerC().materialOffer.getPos()); })),
                                        WaitForTargetReached_FailIfNotReachable(),
                                        $("can we pick it up?", Condition(BearerBehaviorTreeFactory::canTakeMaterial)),
                                        StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                                        WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                                        tryTakeMaterialFromMap(),
                                        StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                                        WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
                                    ),
                                    $("handle failure", Sequence(
                                        Action(BearerBehaviorTreeFactory::distributionAborted),
                                        Failer()
                                    ))
                                )
                            )),
                            $("convert Entity to a worker", Action(e->{ convertTo(e, e.bearerC().workerCreationRequest.requestedMovableType()); }))
                        )),
                        $("handle failure", Sequence(
                            Action(BearerBehaviorTreeFactory::workerCreationRequestFailed),
                            Action(BearerBehaviorTreeFactory::resetJob),
                            Failer()
                        ))
                    )
                ),
                Guard(e -> e.bearerC().hasDeliveryJob(), true,
                    MemSequence(
                        Selector(
                            $("go to materialOffer and take material", MemSequence(
                                Action(e -> {
                                    e.steerC().setTarget(e.bearerC().materialOffer.getPos());
                                }),
                                WaitForTargetReached_FailIfNotReachable(),
                                Condition(BearerBehaviorTreeFactory::canTakeMaterial),
                                StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                                WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                                tryTakeMaterialFromMap(),
                                StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                                WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true)
                            )),
                            $("handle failure", Sequence(
                                Action(BearerBehaviorTreeFactory::distributionAborted),
                                Action(BearerBehaviorTreeFactory::deliveryAborted),
                                Action(BearerBehaviorTreeFactory::resetJob),
                                Failer()
                            ))
                        ),
                        Selector(
                            $("go to request & drop material", MemSequence(
                                Action(e -> { e.steerC().setTarget(e.bearerC().deliveryRequest.getPos()); }),
                                WaitForTargetReached_FailIfNotReachable(),
                                Condition(e -> e.bearerC().materialType.isDroppable()),
                                StartAnimation(EMovableAction.BEND_DOWN, Constants.MOVABLE_BEND_DURATION),
                                WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                                tryFulfillRequest(),
                                StartAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
                                WaitForNotification(AnimationComponent.AnimationFinishedTrigger.class, true),
                                Action(BearerBehaviorTreeFactory::resetJob)
                            )),
                            $("handle failure", Sequence(
                                $("reoffer the material", dropMaterial()),
                                Action(BearerBehaviorTreeFactory::deliveryAborted),
                                Action(BearerBehaviorTreeFactory::resetJob),
                                Failer()
                            ))
                        )
                    )
                ),
                Guard(e -> e.bearerC().hasBecomeSoldierJob(), true,
                    Selector(
                        $("fullfill the job", MemSequence(
                            Action(e -> { e.steerC().setTarget(e.bearerC().barrack.getDoor()); }),
                            WaitForTargetReached_FailIfNotReachable(),
                            tryTakeWeapon_ConvertToSoldier()
                        )),
                        $("handle failure", Sequence(
                            Action(BearerBehaviorTreeFactory::resetJob)
                        ))
                    )
                )
            ));
        }
    }
}
