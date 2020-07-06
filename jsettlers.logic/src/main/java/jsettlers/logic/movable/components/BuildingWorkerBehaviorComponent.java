package jsettlers.logic.movable.components;

import com.sun.net.httpserver.Authenticator;

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.MovableWrapper;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysSucceed;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysSucceed;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.defaultIdleBehavior;
import static jsettlers.logic.movable.BehaviorTreeHelper.dropMaterial;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSelector;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.setIdleBehaviorActiveWhile;
import static jsettlers.logic.movable.BehaviorTreeHelper.sleep;
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
					dropMaterial(c->c.entity.materialComponent().getMaterial()),
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
							guard(isCurrentJobType(EBuildingJobType.TRY_TAKING_RESOURCE),
								sequence("try taking resource",
									action(c->{ c.entity.markedPositonComponent().clearMark();}),
									selector(
										sequence(
											tryTakingResource(),
											jobFinished()),
										jobFailed()
									)
								)
							),
							guard(isCurrentJobType(EBuildingJobType.TRY_TAKING_FOOD),
								selector(
									sequence(
										tryTakingFood(),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.WAIT),
								sequence(
									sleep(c -> (short)c.entity.buildingWorkerComponent().getCurrentJob().getTime() * 1000),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.WALK),
								memSequence("walk in direction of job",
									action(c->{c.entity.steeringComponent().goInDirection(c.entity.buildingWorkerComponent().getCurrentJob().getDirection(), EGoInDirectionMode.GO_IF_ALLOWED_WAIT_TILL_FREE);}),
									waitForTargetReachedAndFailIfNotReachable(),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.SHOW),
								sequence(
									condition("Succeed if building priority stopped",c->c.entity.buildingWorkerComponent().getBuildingPriority() == EPriority.STOPPED),
									debug("show", show()),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.HIDE),
								sequence(
									action("hide", c->{c.entity.movableComponent().setVisible(false);}),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.SET_MATERIAL),
								sequence(
									action("set material", c->{
										entity.materialComponent().setMaterial(entity.buildingWorkerComponent().getCurrentJob().getMaterial());}
									),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.TAKE),
								selector(
									memSequence("try take material",
										debug("can take material", condition(BuildingWorkerBehaviorComponent::canTakeMaterial)),
										startAndWaitForAnimation(EMovableAction.BEND_DOWN, c->Constants.MOVABLE_BEND_DURATION, true),
										tryTakeMaterialFromMap(),
										startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.DROP),
								memSequence("drop Material",
									startAndWaitForAnimation(EMovableAction.BEND_DOWN, c->Constants.MOVABLE_BEND_DURATION, true),
									dropMaterial(c->c.entity.buildingWorkerComponent().getCurrentJob().getMaterial()),
									alwaysSucceed(guard("increment gold count if goldmelt",c->c.entity.buildingWorkerComponent().getBuildingType() == EBuildingType.GOLDMELT,
										action(c->{c.entity.movableComponent().getPlayer().getEndgameStatistic().incrementAmountOfProducedGold();})
									)),
									startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.DROP_POPPED),
								memSequence("drop Material",
									startAndWaitForAnimation(EMovableAction.BEND_DOWN, c->Constants.MOVABLE_BEND_DURATION, true),
									dropMaterial(c->c.entity.buildingWorkerComponent().getPoppedMaterial()),
									startAndWaitForAnimation(EMovableAction.RAISE_UP, Constants.MOVABLE_BEND_DURATION),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.PRE_SEARCH),
								selector(
									sequence("search for work",
										condition("find path to work",c->c.entity.buildingWorkerComponent().preSearchPath(false)),
										action("building can work", c->{c.entity.buildingWorkerComponent().workSearchSucceeded();}),
										jobFinished()
									),
									sequence("no work found",
										action("building cannot work", c->{c.entity.buildingWorkerComponent().workSearchFailed();}),
										jobFailed()
									)
								)

							),
							guard(isCurrentJobType(EBuildingJobType.PRE_SEARCH_IN_AREA),
								selector(
									sequence("search for work",
										condition("found path to work",c->c.entity.buildingWorkerComponent().preSearchPath(true)),
										action("building can work", c->{c.entity.buildingWorkerComponent().workSearchSucceeded();}),
										jobFinished()
									),
									sequence("no work found",
										action("building cannot work", c->{c.entity.buildingWorkerComponent().workSearchFailed();}),
										jobFailed()
									)
								)
							),
							guard(isCurrentJobType(EBuildingJobType.FOLLOW_SEARCHED),
								selector(
									memSequence("follow presearched path",
										action(c->{
											Path path = c.entity.buildingWorkerComponent().getPreSearchedPath();
											c.entity.buildingWorkerComponent().mark(path.getTargetPosition());
											c.entity.steeringComponent().setPath(path);
										}),
										waitForTargetReachedAndFailIfNotReachable(),
										jobFinished()
									),
									sequence("path aborted",
										guard("has current Job", c->c.entity.buildingWorkerComponent().getCurrentJob() != null,
											jobFailed()
										),
										action(c->{c.entity.buildingWorkerComponent().clearMark();})
									)
								)
							),
							guard(isCurrentJobType(EBuildingJobType.LOOK_AT_SEARCHED),
								selector(
									sequence(
										action("Look in direction",c->{
											EDirection direction = c.entity.gameFieldComponent().movableGrid.getDirectionOfSearched(c.entity.movableComponent().getPosition(), c.entity.buildingWorkerComponent().getCurrentJob().getSearchType());
											if (direction != null) {
												c.entity.movableComponent().setViewDirection(direction);
												return NodeStatus.SUCCESS;
											}
											return NodeStatus.FAILURE;
										}),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.GO_TO_DOCK),
								selector(
									memSequence("go to docks",
										condition("set target position", c-> {
											ShortPoint2D dockEndPosition = c.entity.buildingWorkerComponent().getDockyard().getDock().getEndPosition();
											return c.entity.steeringComponent().setTarget(dockEndPosition);
										}),
										waitForTargetReachedAndFailIfNotReachable(),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.BUILD_SHIP),
								sequence(
									action("build ship action", c->{c.entity.buildingWorkerComponent().getDockyard().buildShipAction();}),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.LOOK_AT),
								sequence(
									action("look in job direction", c->{c.entity.movableComponent().setViewDirection(c.entity.buildingWorkerComponent().getCurrentJob().getDirection());}),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.EXECUTE),
								sequence(
									action("clear Mark", c->{c.entity.buildingWorkerComponent().clearMark();}),
									selector(
										sequence(
											action("execute Search", c-> {
												ShortPoint2D pos = c.entity.movableComponent().getPosition();
												MovableWrapper movable = c.entity.movableComponent().getMovableWrapper();
												c.entity.gameFieldComponent().movableGrid.executeSearchType(movable, pos, c.entity.buildingWorkerComponent().getCurrentJob().getSearchType());
											}),
											jobFinished()
										),
										jobFailed()
									)
								)
							),
							guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION1),
								sequence("play action 1",
									startAndWaitForAnimation(EMovableAction.ACTION1, c->(short) (1000 * c.entity.buildingWorkerComponent().getCurrentJob().getTime()), false),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION2),
								sequence("play action 2",
									startAndWaitForAnimation(EMovableAction.ACTION2, c->(short) (1000 * c.entity.buildingWorkerComponent().getCurrentJob().getTime()), false),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.PLAY_ACTION3),
								sequence("play action 3",
									startAndWaitForAnimation(EMovableAction.ACTION2, c->(short) (1000 * c.entity.buildingWorkerComponent().getCurrentJob().getTime()), false),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.AVAILABLE),
								selector(
									sequence(
										condition("is material available", c->
											c.entity.gameFieldComponent().movableGrid.canTakeMaterial(c.entity.buildingWorkerComponent().getCurrentJobPos(), c.entity.buildingWorkerComponent().getCurrentJob().getMaterial())
										),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.NOT_FULL),
								selector(
									sequence(
										condition("is material available", c->
											c.entity.gameFieldComponent().movableGrid.canPushMaterial(c.entity.buildingWorkerComponent().getCurrentJobPos())
										),
										jobFinished()
									),
									jobFailed()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.SMOKE_ON),
								sequence(
									placeSmoke(true),
									jobFinished()
								)
							),
							guard(isCurrentJobType(EBuildingJobType.SMOKE_OFF),
								sequence(
									placeSmoke(false),
									jobFinished()
								)
							),
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

	private static Node<Context> placeSmoke(boolean onOrOff) {
		return action("place smoke",c->{
			c.entity.gameFieldComponent().movableGrid.placeSmoke(c.entity.buildingWorkerComponent().getCurrentJobPos(), onOrOff);
			c.entity.buildingWorkerComponent().addMapObjectCleanupPosition(c.entity.buildingWorkerComponent().getCurrentJobPos(), EMapObjectType.SMOKE);
		});
	}

	private static IBooleanConditionFunction<Context> isCurrentJobType(EBuildingJobType type) {
		return c->c.entity.buildingWorkerComponent().getCurrentJob().getType() == type;
	}

	private static Node<Context> jobFinished() {
		return new Action<>(c->{c.entity.buildingWorkerComponent().jobFinished();});
	}

	private static Node<Context> jobFailed() {
		return new Action<>(c->{c.entity.buildingWorkerComponent().jobFailed();});
	}

	private static Node<Context> tryTakingResource() {
		return new Action<>(c->{return NodeStatus.of(c.entity.buildingWorkerComponent().tryTakingResource());});
	}

	private static Node<Context> tryTakingFood() {
		return new Action<>(c->{return NodeStatus.of(c.entity.buildingWorkerComponent().tryTakingFood());});
	}

	private static Node<Context> show() {
		return new Action<>(c->{
			BuildingWorkerComponent bwc = c.entity.buildingWorkerComponent();
			MovableComponent mc = c.entity.movableComponent();

			ShortPoint2D pos = bwc.getCurrentJobPos();
			if (bwc.getCurrentJob().getDirection() != null) {
				mc.setViewDirection(bwc.getCurrentJob().getDirection());
			}
			mc.setPos(pos);
			mc.setVisible(true);
		});
	}

	private static boolean canTakeMaterial(Context c) {
		EMaterialType materialToTake = c.entity.buildingWorkerComponent().getCurrentJob().getMaterial();
		boolean takeFromMap = c.entity.buildingWorkerComponent().getCurrentJob().isTakeMaterialFromMap();
		return !takeFromMap || c.entity.gameFieldComponent().movableGrid.canTakeMaterial(c.entity.movableComponent().getPosition(), materialToTake);
	}

	private static Node<Context> tryTakeMaterialFromMap() {
		return debug("try take material", new Action<>(c -> {
			EMaterialType materialToTake = c.entity.buildingWorkerComponent().getCurrentJob().getMaterial();
			if (c.entity.gameFieldComponent().movableGrid.takeMaterial(c.entity.movableComponent().getPosition(), materialToTake)) {
				c.entity.materialComponent().setMaterial(materialToTake);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		}));
	}
}
