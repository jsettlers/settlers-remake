package jsettlers.logic.movable.components;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysSucceed;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.defaultIdleBehavior;
import static jsettlers.logic.movable.BehaviorTreeHelper.dropMaterial;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSelector;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.setIdleBehaviorActiveWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.startAndWaitForAnimation;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForPathFinished;
import static jsettlers.logic.movable.BehaviorTreeHelper.waitForTargetReachedAndFailIfNotReachable;

public class BuildingWorkerBehaviorComponent extends BehaviorComponent {
    private static final long serialVersionUID = -5394764830129769392L;

    @Override
    protected Node<Context> createBehaviorTree() {
        return setIdleBehaviorActiveWhile(false,
            selector(
                triggerGuard(BuildingWorkerComponent.BuildingDestroyed.class, sequence(
                    action("handle building destroyed", c-> {
                        c.entity.movableComponent().setVisible(true);
                        c.entity.steeringComponent().resetTarget();
                    }),
                    action(c->{ c.entity.buildingWorkerComponent().reportAsJobless(); }),
                    dropMaterial(),
                    action(c->{ c.entity.markedPositonComponent().clearMark();})
                )),
                guard("has a job", c->c.entity.buildingWorkerComponent().hasJob(),
                    sequence("try execute job",
                        memSelector(
                            guard(isCurrentJobType(EBuildingJobType.GO_TO),
                                selector(
                                    memSequence("go to job pos",
                                        action(c->{c.entity.steeringComponent().setTarget(c.entity.buildingWorkerComponent().getCurrentJobPos());}),
                                        waitForTargetReachedAndFailIfNotReachable()
                                    ),
                                    jobFailed()
                                )
                            ),
                            guard(isCurrentJobType(EBuildingJobType.TRY_TAKING_RESOURCE),null),
                            guard(isCurrentJobType(EBuildingJobType.TRY_TAKING_FOOD),null),
                            guard(isCurrentJobType(EBuildingJobType.WAIT),null),
                            guard(isCurrentJobType(EBuildingJobType.WALK),null),
                            guard(isCurrentJobType(EBuildingJobType.SHOW),null),
                            guard(isCurrentJobType(EBuildingJobType.HIDE),null),
                            guard(isCurrentJobType(EBuildingJobType.SET_MATERIAL),null),
                            guard(isCurrentJobType(EBuildingJobType.TAKE),null),
                            guard(isCurrentJobType(EBuildingJobType.DROP),null),
                            guard(isCurrentJobType(EBuildingJobType.DROP_POPPED),null),
                            guard(isCurrentJobType(EBuildingJobType.PRE_SEARCH),null),
                            guard(isCurrentJobType(EBuildingJobType.PRE_SEARCH_IN_AREA),null),
                            guard(isCurrentJobType(EBuildingJobType.FOLLOW_SEARCHED),null),
                            guard(isCurrentJobType(EBuildingJobType.LOOK_AT_SEARCHED),null),
                            guard(isCurrentJobType(EBuildingJobType.GO_TO_DOCK),null),
                            guard(isCurrentJobType(EBuildingJobType.BUILD_SHIP),null),
                            guard(isCurrentJobType(EBuildingJobType.LOOK_AT),null),
                            guard(isCurrentJobType(EBuildingJobType.EXECUTE),null),
                            guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION1),null),
                            guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION2),null),
                            guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION3),null),
                            guard(isCurrentJobType(EBuildingJobType.AVAILABLE),null),
                            guard(isCurrentJobType(EBuildingJobType.NOT_FULL),null),
                            guard(isCurrentJobType(EBuildingJobType.SMOKE_ON),null),
                            guard(isCurrentJobType(EBuildingJobType.SMOKE_OFF),null),
                            guard(isCurrentJobType(EBuildingJobType.START_WORKING),null),
                            guard(isCurrentJobType(EBuildingJobType.STOP_WORKING),null),
                            guard(isCurrentJobType(EBuildingJobType.PIG_IS_ADULT),null),
                            guard(isCurrentJobType(EBuildingJobType.PIG_IS_THERE),null),
                            guard(isCurrentJobType(EBuildingJobType.PIG_PLACE),null),
                            guard(isCurrentJobType(EBuildingJobType.PIG_REMOVE),null),
                            guard(isCurrentJobType(EBuildingJobType.POP_TOOL),null),
                            guard(isCurrentJobType(EBuildingJobType.POP_WEAPON),null),
                            guard(isCurrentJobType(EBuildingJobType.GROW_DONKEY),null)
                        ),
                        debug("job finished", jobFinished())
                    )
                ),
                defaultIdleBehavior()
            )
        );
    }

    private IBooleanConditionFunction<Context> isCurrentJobType(EBuildingJobType type) {
        return c->c.entity.buildingWorkerComponent().getCurrentJob().getType() == type;
    }

    private Node<Context> jobFinished() {
        return new Action<>(c->{c.entity.buildingWorkerComponent().jobFinished();});
    }

    private Node<Context> jobFailed() {
        return new Action<>(c->{c.entity.buildingWorkerComponent().jobFailed();});
    }
}
