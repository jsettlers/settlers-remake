package jsettlers.logic.movable.components;

import jsettlers.algorithms.path.Path;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.Requires;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.Tick;

import static jsettlers.logic.movable.BehaviorTreeHelper.$;
import static jsettlers.logic.movable.BehaviorTreeHelper.Action;
import static jsettlers.logic.movable.BehaviorTreeHelper.Condition;
import static jsettlers.logic.movable.BehaviorTreeHelper.Guard;
import static jsettlers.logic.movable.BehaviorTreeHelper.Selector;
import static jsettlers.logic.movable.BehaviorTreeHelper.TriggerGuard;

/**
 * @author homoroselaps
 */
@Requires({GameFieldComponent.class, MovableComponent.class, AnimationComponent.class})
public class SteeringComponent extends Component {
    private static final long serialVersionUID = 8281773945922792414L;
    private Path path;
    private GameFieldComponent gameC;
    private MovableComponent movC;
    private AnimationComponent aniC;
    private Tick<Context> tick;

    public static class TargetReachedTrigger extends Notification {}
    public static class TargetNotReachedTrigger extends Notification {}
    public static class LeavePositionRequest extends Notification {}

    @Override
    protected void onAwake() {
        gameC = entity.get(GameFieldComponent.class);
        movC = entity.get(MovableComponent.class);
        aniC = entity.get(AnimationComponent.class);
        tick = new Tick<>(new Context(entity,this), CreateBehaviorTree());
    }

    public boolean setTarget(ShortPoint2D targetPos) {
        if (movC.getPos().equals(targetPos)) {
            entity.raiseNotification(new TargetReachedTrigger());
            return true;
        }
        path = gameC.getMovableGrid().calculatePathTo(movC, targetPos);
        return path != null;
    }

    public void resetTarget() {
        path = null;
    }

    public void setPath(Path path) {
        assert path != null: "path mustn't be null";
        this.path = path;
    }

    public Path preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
        if (dijkstra) {
            return gameC.getMovableGrid().searchDijkstra(movC, centerX, centerY, radius, searchType);
        } else {
            return gameC.getMovableGrid().searchInArea(movC, centerX, centerY, radius, searchType);
        }
    }

    private Root<Context> CreateBehaviorTree() {
        return new Root<Context>($("==<Root>==",
            Selector(
                TriggerGuard(LeavePositionRequest.class,
                    $("we got pushed")
                ),
                Guard(c->path != null, true,
                    Action(c->{ followPath(); })
                ),
                Guard(c->gameC.getMovableGrid().isBlockedOrProtected(movC.getPos().x, movC.getPos().y),true,
                    Action(c->{ goToNonBlockedOrProtectedPosition(); })
                ),
                Condition(c->this.flockToDecentralize()),
                Action(c->{turnInRandomDirection();})
            )
        ));
    }

    @Override
    protected void onUpdate() {
        aniC.stopAnimation();
        tick.Tick();
    }

    private void followPath() {
        // if path is finished
        if (!path.hasNextStep()) {
            path = null;
            entity.raiseNotification(new TargetReachedTrigger());
            return;
        }

        ILogicMovable blockingMovable = gameC.getMovableGrid().getMovableAt(path.nextX(), path.nextY());
        if (blockingMovable == null) { // if we can go on to the next step
            if (gameC.getMovableGrid().isValidNextPathPosition(movC, path.getNextPos(), path.getTargetPos())) { // next position is valid
                goSingleStep(path.getNextPos());
                path.goToNextStep();
            } else { // next position is invalid

                Path newPath = gameC.getMovableGrid().calculatePathTo(movC, path.getTargetPos()); // try to find a new path

                if (newPath == null) { // no path found
                    path = null;

                    entity.raiseNotification(new TargetNotReachedTrigger());
                } else {
                    this.path = newPath; // continue with new path
                    if (gameC.getMovableGrid().hasNoMovableAt(path.nextX(), path.nextY())) { // path is valid, but maybe blocked (leaving blocked area)
                        goSingleStep(path.getNextPos());
                        path.goToNextStep();
                    }
                }
            }
        } else { // step not possible, so try it next time (push not supported)

        }
    }

    private void goToNonBlockedOrProtectedPosition() {
        Path newPath = gameC.getMovableGrid().searchDijkstra(movC, movC.getPos().x, movC.getPos().y, (short) 50, ESearchType.NON_BLOCKED_OR_PROTECTED);
        if (newPath == null) {
            entity.kill();
        } else {
            setPath(newPath);
        }
    }

    private void turnInRandomDirection() {
        int turnDirection = MatchConstants.random().nextInt(-8, 8);
        if (Math.abs(turnDirection) <= 1) {
            movC.setViewDirection(movC.getViewDirection().getNeighbor(turnDirection));
        }
    }

    private void goSingleStep(ShortPoint2D targetPosition) {
        movC.setViewDirection(EDirection.getDirection(movC.getPos(), targetPosition));
        movC.setPos(targetPosition);
        aniC.startAnimation(EMovableAction.WALKING, movC.getMovableType().getStepDurationMs());
        aniC.switchStep();
    }

    /**
     * Tries to walk the movable into a position where it has a minimum distance to others.
     *
     * @return true if the movable moves to flock, false if no flocking is required.
     */
    private boolean flockToDecentralize() {
        ShortPoint2D decentVector = gameC.getMovableGrid().calcDecentralizeVector(movC.getPos().x, movC.getPos().y);

        EDirection randomDirection = movC.getViewDirection().getNeighbor(MatchConstants.random().nextInt(-1, 1));
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
        ShortPoint2D targetPosition = direction.getNextHexPoint(movC.getPos());

        switch (mode) {
            case GO_IF_ALLOWED_WAIT_TILL_FREE: {
                movC.setViewDirection(direction);
                this.setPath(new Path(targetPosition));
                return true;
            }
            case GO_IF_ALLOWED_AND_FREE:
                if ((gameC.getMovableGrid().isValidPosition(movC, targetPosition.x, targetPosition.y) && gameC.getMovableGrid().hasNoMovableAt(targetPosition.x, targetPosition.y))) {
                    goSingleStep(targetPosition);
                    return true;
                } else {
                    break;
                }
            case GO_IF_FREE:
                if (gameC.getMovableGrid().isFreePosition(targetPosition)) {
                    goSingleStep(targetPosition);
                    return true;
                } else {
                    break;
                }
        }
        return false;
    }
}
