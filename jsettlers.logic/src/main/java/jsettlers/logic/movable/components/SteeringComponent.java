package jsettlers.logic.movable.components;

import java8.util.Optional;
import jsettlers.algorithms.path.Path;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.BehaviorTreeHelper;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.Tick;

import static jsettlers.logic.movable.BehaviorTreeHelper.action;
import static jsettlers.logic.movable.BehaviorTreeHelper.condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.debug;
import static jsettlers.logic.movable.BehaviorTreeHelper.guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.memSequence;
import static jsettlers.logic.movable.BehaviorTreeHelper.selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.sleep;
import static jsettlers.logic.movable.BehaviorTreeHelper.alwaysSucceed;
import static jsettlers.logic.movable.BehaviorTreeHelper.triggerGuard;

/**
 * @author homoroselaps
 */
@Requires({
	GameFieldComponent.class,
	MovableComponent.class,
	AnimationComponent.class
})
public class SteeringComponent extends Component {
	private static final long serialVersionUID = 8281773945922792414L;

	private Path               path;
	private GameFieldComponent gameFieldComponent;
	private MovableComponent   movableComponent;
	private AnimationComponent animationComponent;
	private Tick<Context>      tick;
	private boolean            isIdleBehaviorActive = false;

	public boolean IsIdleBehaviorActive() { return isIdleBehaviorActive; }

	public void IsIdleBehaviorActive(boolean value) { isIdleBehaviorActive = value; }

	public static class TargetReachedNotification extends Notification {}

	public static class TargetNotReachedNotification extends Notification {}

	public static class LeavePositionRequest extends Notification {
		public final ILocatable sender;

		public LeavePositionRequest(ILocatable sender) {
			this.sender = sender;
		}
	}

	@Override
	protected void onWakeUp() {
		gameFieldComponent = entity.getComponent(GameFieldComponent.class);
		movableComponent = entity.getComponent(MovableComponent.class);
		animationComponent = entity.getComponent(AnimationComponent.class);
		tick = new Tick<>(new Context(entity, this), new Root<>(debug("==<root: steering>== of " + entity.getID(), createBehaviorTree())));
	}

	public boolean setTarget(ShortPoint2D targetPos) {
		if (movableComponent.getPosition().equals(targetPos)) {
			entity.raiseNotification(new TargetReachedNotification());
			return true;
		}
		path = gameFieldComponent.movableGrid.calculatePathTo(movableComponent, targetPos);
		return path != null;
	}

	public void resetTarget() {
		path = null;
	}

	public void setPath(Path path) {
		assert path != null : "path must not be null";
		this.path = path;
	}

	public Path preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
		if (dijkstra) {
			return gameFieldComponent.movableGrid.searchDijkstra(movableComponent, centerX, centerY, radius, searchType);
		} else {
			return gameFieldComponent.movableGrid.searchInArea(movableComponent, centerX, centerY, radius, searchType);
		}
	}

	protected Node<Context> createBehaviorTree() {
		return selector(
			guard(c -> path != null, true,
				debug("follow path", action(c -> { followPath(); }))
			),
			BehaviorTreeHelper.guard(c -> ((SteeringComponent)c.component).isIdleBehaviorActive,
				selector(
					guard(c -> gameFieldComponent.movableGrid.isBlockedOrProtected(movableComponent.getPosition().x, movableComponent.getPosition().y), true,
						action(c -> {
							goToNonBlockedOrProtectedPosition();
						})
					),
					debug("if LeavePositionRequest", triggerGuard(LeavePositionRequest.class,
						debug("try go in random direction", action(context -> {
							Optional<LeavePositionRequest> note = context.component.getNextNotification(LeavePositionRequest.class, false);
							if (note.isPresent() && goToRandomDirection(note.get().sender)) {
								context.component.consumeNotification(note.get());
								return NodeStatus.SUCCESS;
							}
							return NodeStatus.FAILURE;
						}))
					)),
					debug("move away from other movables", memSequence(
						condition(c -> this.flockToDecentralize()),
						sleep(500)
					)),
					debug("turn in a random direction", memSequence(
						action(c -> {
							turnInRandomDirection();
						}),
						sleep(1000)
					))
				)
			),
			debug("nothing to do", alwaysSucceed())
		);
	}

	@Override
	protected void onUpdate() {
		tick.tick();
	}

	private boolean goToRandomDirection(ILocatable pushingMovable) {
		int offset = MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS);
		EDirection pushedFromDir = EDirection.getDirection(movableComponent.getPosition(), pushingMovable.getPosition());

		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			EDirection currDir = EDirection.VALUES[(i + offset) % EDirection.NUMBER_OF_DIRECTIONS];
			if (currDir != pushedFromDir && goInDirection(currDir, EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE)) {
				return true;
			}
		}

		return false;
	}

	private void followPath() {
		// if path is finished
		if (!path.hasNextStep()) {
			path = null;
			entity.raiseNotification(new TargetReachedNotification());
			return;
		}

		ILogicMovable blockingMovable = gameFieldComponent.movableGrid.getMovableAt(path.nextX(), path.nextY());
		if (blockingMovable == null) { // if we can go on to the next step
			if (gameFieldComponent.movableGrid.isValidNextPathPosition(movableComponent, path.getNextPos(), path.getTargetPosition())) { // next position is valid
				goSingleStep(path.getNextPos());
				path.goToNextStep();
			} else { // next position is invalid

				Path newPath = gameFieldComponent.movableGrid.calculatePathTo(movableComponent, path.getTargetPosition()); // try to find a new path

				if (newPath == null) { // no path found
					path = null;
					entity.raiseNotification(new TargetNotReachedNotification());
				} else {
					this.path = newPath; // continue with new path
					if (gameFieldComponent.movableGrid.hasNoMovableAt(path.nextX(), path.nextY())) { // path is valid, but maybe blocked (leaving blocked area)
						goSingleStep(path.getNextPos());
						path.goToNextStep();
					}
				}
			}
		} else { // step not possible, so try it next time (push not supported)
			blockingMovable.push(movableComponent.getMovableWrapper());
		}
	}

	private void goToNonBlockedOrProtectedPosition() {
		Path newPath = gameFieldComponent.movableGrid.searchDijkstra(movableComponent, movableComponent.getPosition().x, movableComponent.getPosition().y, (short) 50, ESearchType
			.NON_BLOCKED_OR_PROTECTED);
		if (newPath == null) {
			entity.kill();
		} else {
			setPath(newPath);
		}
	}

	private void turnInRandomDirection() {
		int turnDirection = MatchConstants.random().nextInt(-8, 8);
		if (Math.abs(turnDirection) <= 1) {
			movableComponent.setViewDirection(movableComponent.getViewDirection().getNeighbor(turnDirection));
		}
	}

	private void goSingleStep(ShortPoint2D targetPosition) {
		movableComponent.setViewDirection(EDirection.getDirection(movableComponent.getPosition(), targetPosition));
		movableComponent.setPos(targetPosition);
		animationComponent.startAnimation(EMovableAction.WALKING, movableComponent.getMovableType().getStepDurationMs(), false);
		animationComponent.switchStep();
	}

	/**
	 * Tries to walk the movable into a position where it has a minimum distance to others.
	 *
	 * @return true if the movable moves to flock, false if no flocking is required.
	 */
	private boolean flockToDecentralize() {
		ShortPoint2D decentVector = gameFieldComponent.movableGrid.calcDecentralizeVector(movableComponent.getPosition().x, movableComponent.getPosition().y);

		EDirection randomDirection = movableComponent.getViewDirection().getNeighbor(MatchConstants.random().nextInt(-1, 1));
		int dx = randomDirection.gridDeltaX + decentVector.x;
		int dy = randomDirection.gridDeltaY + decentVector.y;

		if (ShortPoint2D.getOnGridDist(dx, dy) >= 2) {
			return goInDirection(EDirection.getApproxDirection(0, 0, dx, dy), EGoInDirectionMode.GO_IF_ALLOWED_AND_FREE);
		} else {
			return false;
		}
	}

	/**
	 * Tries to go a step in the given direction.
	 *
	 * @param direction
	 *            direction to go
	 * @param mode
	 *            Use the given mode to go.<br>
	 * @return true if the step can and will immediately be executed. <br>
	 *         false if the target position is generally blocked or a movable occupies that position.
	 */
	final boolean goInDirection(EDirection direction, EGoInDirectionMode mode) {
		ShortPoint2D targetPosition = direction.getNextHexPoint(movableComponent.getPosition());

		switch (mode) {
			case GO_IF_ALLOWED_WAIT_TILL_FREE: {
				movableComponent.setViewDirection(direction);
				this.setPath(new Path(targetPosition));
				return true;
			}
			case GO_IF_ALLOWED_AND_FREE:
				if ((gameFieldComponent.movableGrid.isValidPosition(movableComponent, targetPosition.x, targetPosition.y)
					&& gameFieldComponent.movableGrid.hasNoMovableAt(targetPosition.x, targetPosition.y))) {
					goSingleStep(targetPosition);
					return true;
				} else {
					break;
				}
			case GO_IF_FREE:
				if (gameFieldComponent.movableGrid.isFreePosition(targetPosition.x, targetPosition.y)) {
					goSingleStep(targetPosition);
					return true;
				} else {
					break;
				}
		}
		return false;
	}
}
