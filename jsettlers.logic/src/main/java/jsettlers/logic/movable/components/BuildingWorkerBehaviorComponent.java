package jsettlers.logic.movable.components;

import com.sun.net.httpserver.Authenticator;

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.SlaughterhouseBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.MovableWrapper;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.simplebehaviortree.IBooleanConditionFunction;
import jsettlers.logic.movable.simplebehaviortree.INodeStatusActionConsumer;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.AlwaysSucceed;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysFail;
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

@Requires({
	GameFieldComponent.class,
	MovableComponent.class,
	SteeringComponent.class,
	BuildingWorkerComponent.class,
	MaterialComponent.class,
	MarkedPositonComponent.class
})
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
				action(c->{
					IBuildingJob job = c.entity.buildingWorkerComponent().getCurrentJob();
					System.out.println(job);
					return NodeStatus.FAILURE;
				}),
				guard("has a job", c->c.entity.buildingWorkerComponent().hasJob(),
					memSelector("try execute job",
						guard(isCurrentJobType(EBuildingJobType.GO_TO),
							selector(
								memSequence("go to job pos",
									action(c->{c.entity.steeringComponent().setTarget(c.entity.buildingWorkerComponent().getCurrentJobPos());}),
									waitForTargetReachedAndFailIfNotReachable(),
									jobFinished()
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
							memSequence("wait",
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
								condition("Is building not stopped",c->c.entity.buildingWorkerComponent().getBuildingPriority() != EPriority.STOPPED),
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
									tryTakeMaterial(),
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
										DockyardBuilding dockyard = (DockyardBuilding)c.entity.buildingWorkerComponent().getBuilding();
										ShortPoint2D dockEndPosition = dockyard.getDock().getEndPosition();
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
								action("build ship action", c->{
									DockyardBuilding dockyard = (DockyardBuilding)c.entity.buildingWorkerComponent().getBuilding();
									dockyard.buildShipAction();
								}),
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
								startAndWaitForAnimation(EMovableAction.ACTION3, c->(short) (1000 * c.entity.buildingWorkerComponent().getCurrentJob().getTime()), false),
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
						guard(isCurrentJobType(EBuildingJobType.START_WORKING),
							sequence(
								selector(
									guard("is slaughterhouse", c->c.entity.buildingWorkerComponent().getBuilding() instanceof SlaughterhouseBuilding,
										action("play slaughter sound", c->{((SlaughterhouseBuilding) c.entity.buildingWorkerComponent().getBuilding()).requestSound();})
									),
									guard("is MillBuilding", c->c.entity.buildingWorkerComponent().getBuilding() instanceof MillBuilding,
										action("start rotate the mill", c->{((MillBuilding) c.entity.buildingWorkerComponent().getBuilding()).setRotating(true);})
									)
								),
								jobFinished()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.STOP_WORKING),
							sequence(
								selector(
									guard("is slaughterhouse", c->c.entity.buildingWorkerComponent().getBuilding() instanceof SlaughterhouseBuilding,
										action("play slaughter sound", c->{((SlaughterhouseBuilding) c.entity.buildingWorkerComponent().getBuilding()).requestSound();})
									),
									guard("is MillBuilding", c->c.entity.buildingWorkerComponent().getBuilding() instanceof MillBuilding,
										action("stop rotate the mill", c->{((MillBuilding) c.entity.buildingWorkerComponent().getBuilding()).setRotating(false);})
									)
								),
								jobFinished()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.PIG_IS_ADULT),
							selector(
								sequence(
									condition("is pig adult", c->c.entity.gameFieldComponent().movableGrid.isPigAdult(c.entity.buildingWorkerComponent().getCurrentJobPos())),
									jobFinished()
								),
								jobFailed()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.PIG_IS_THERE),
							selector(
								sequence(
									condition("is pig there", c->c.entity.gameFieldComponent().movableGrid.hasPigAt(c.entity.buildingWorkerComponent().getCurrentJobPos())),
									jobFinished()
								),
								jobFailed()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.PIG_PLACE),
							sequence(
								placeOrRemovePigAction(true),
								jobFinished()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.PIG_REMOVE),
							sequence(
								placeOrRemovePigAction(false),
								jobFinished()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.POP_TOOL),
							selector(
								sequence(
									popToolRequestAction(),
									jobFinished()
								),
								jobFailed()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.POP_WEAPON),
							selector(
								sequence(
									popWeaponRequestAction(),
									jobFinished()
								),
								jobFailed()
							)
						),
						guard(isCurrentJobType(EBuildingJobType.GROW_DONKEY),
							selector(
								sequence(
									growDonkeyAction(),
									jobFinished()
								),
								jobFailed()
							)
						)
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
		return debug("job finished",action(c->{c.entity.buildingWorkerComponent().jobFinished();}));
	}

	private static Node<Context> jobFailed() {
		return debug("job failed",action(c->{c.entity.buildingWorkerComponent().jobFailed();}));
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

	private static Node<Context> tryTakeMaterial() {
		return debug("try take material", new Action<>(c -> {
			final BuildingWorkerComponent bwc = c.entity.buildingWorkerComponent();
			final GameFieldComponent gfc = c.entity.gameFieldComponent();

			final EMaterialType materialToTake = bwc.getCurrentJob().getMaterial();
			final boolean takeFromMap = bwc.getCurrentJob().isTakeMaterialFromMap();
			if (gfc.movableGrid.takeMaterial(c.entity.movableComponent().getPosition(), materialToTake) || !takeFromMap) {
				c.entity.materialComponent().setMaterial(materialToTake);
				return NodeStatus.SUCCESS;
			}
			return NodeStatus.FAILURE;
		}));
	}

	private static Node<Context> placeOrRemovePigAction(boolean placePig) {
		return action(placePig ? "place " : "remove " + "pig",c-> {
			ShortPoint2D pos = c.entity.buildingWorkerComponent().getCurrentJobPos();
			c.entity.gameFieldComponent().movableGrid.placePigAt(pos, placePig);
			c.entity.buildingWorkerComponent().getBuilding().addMapObjectCleanupPosition(pos, EMapObjectType.PIG);
		});
	}

	private static Node<Context> growDonkeyAction() {
		return action("grow Donkey if at position", c->{
			ShortPoint2D pos = c.entity.buildingWorkerComponent().getCurrentJobPos();
			if (c.entity.gameFieldComponent().movableGrid.feedDonkeyAt(pos)) {
				c.entity.buildingWorkerComponent().getBuilding().addMapObjectCleanupPosition(pos, EMapObjectType.DONKEY);
				return NodeStatus.SUCCESS;
			} else {
				return NodeStatus.FAILURE;
			}
		});
	}

	private static Node<Context> popWeaponRequestAction() {
		return action("pop requested action if available",c->{
			EMaterialType poppedMaterial = c.entity.buildingWorkerComponent().getBuilding().getMaterialProduction().getWeaponToProduce();
			c.entity.buildingWorkerComponent().setPoppedMaterial(poppedMaterial);
			return NodeStatus.of(poppedMaterial != null);
		});
	}

	private static Node<Context> popToolRequestAction() {
		return action("pop requested tool if available", c->{
			IWorkerRequestBuilding building = c.entity.buildingWorkerComponent().getBuilding();
			ShortPoint2D pos = building.getDoor();

			EMaterialType poppedMaterial = building.getMaterialProduction().drawRandomAbsolutelyRequestedTool(); // first priority: Absolutely set tool production requests of user
			if (poppedMaterial == null) {
				poppedMaterial = c.entity.gameFieldComponent().movableGrid.popToolProductionRequest(pos); // second priority: Tools needed by settlers (automated production)
			}
			if (poppedMaterial == null) {
				poppedMaterial = building.getMaterialProduction().drawRandomRelativelyRequestedTool(); // third priority: Relatively set tool production requests of user
			}
			c.entity.buildingWorkerComponent().setPoppedMaterial(poppedMaterial);

			return NodeStatus.of(poppedMaterial != null);
		});
	}
}
