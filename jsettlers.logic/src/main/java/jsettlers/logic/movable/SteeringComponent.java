package jsettlers.logic.movable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.ILogicMovable;

/**
 * Created by jt-1 on 3/28/2017.
 */

public class SteeringComponent extends Component {
    private Path path;
    private GameFieldComponent gameC;
    private MovableComponent movC;
    private AnimationComponent aniC;

    public static class TargetReachedTrigger extends Notification {}
    public static class TargetNotReachedTrigger extends Notification {}

    @Override
    public void OnAwake() {
        gameC = entity.get(GameFieldComponent.class);
        movC = entity.get(MovableComponent.class);
        aniC = entity.get(AnimationComponent.class);
    }

    public boolean goToPos(ShortPoint2D targetPos) {
        //TODO: rename to moveTo
        path = gameC.getMovableGrid().calculatePathTo(movC, targetPos);
        return path != null;
    }

    public void followPath(Path path) {
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

    @Override
    public void OnUpdate() {
        aniC.stopAnimation();
        if (path == null || !path.hasNextStep()) {
            // if path is finished, or canceled
            path = null;
            entity.raiseNotification(new TargetReachedTrigger());
            return;
        }

        ILogicMovable blockingMovable = gameC.getMovableGrid().getMovableAt(path.nextX(), path.nextY());
        if (blockingMovable == null) { // if we can go on to the next step
            if (gameC.getMovableGrid().isValidNextPathPosition(movC, path.getNextPos(), path.getTargetPos())) { // next position is valid
                goSinglePathStep();

            } else { // next position is invalid

                Path newPath = gameC.getMovableGrid().calculatePathTo(movC, path.getTargetPos()); // try to find a new path

                if (newPath == null) { // no path found
                    path = null;
                    entity.raiseNotification(new TargetNotReachedTrigger());
                } else {
                    this.path = newPath; // continue with new path
                    if (gameC.getMovableGrid().hasNoMovableAt(path.nextX(), path.nextY())) { // path is valid, but maybe blocked (leaving blocked area)
                        goSinglePathStep();
                    }
                }
            }

        } else { // step not possible, so try it next time (push not supported)

        }
    }

    private void goSinglePathStep() {
        movC.setViewDirection(EDirection.getDirection(movC.getPos(), path.getNextPos()));
        aniC.startAnimation(EMovableAction.WALKING, movC.getMovableType().getStepDurationMs());
        gameC.getMovableGrid().leavePosition(movC.getPos(), new MovableWrapper(entity));
        gameC.getMovableGrid().enterPosition(path.getNextPos(), new MovableWrapper(entity), false);
        movC.setPos(path.getNextPos());
        aniC.switchStep();
        path.goToNextStep();
    }
}
