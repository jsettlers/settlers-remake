package jsettlers.logic.movable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

/**
 * Created by jt-1 on 3/28/2017.
 */

public class SteeringComponent extends Component {
    private Path path;
    private GameFieldComponent gameC;
    private MovableComponent movC;
    private AnimationComponent aniC;

    @Override
    public void OnAwake() {
        gameC = entity.get(GameFieldComponent.class);
        movC = entity.get(MovableComponent.class);
        aniC = entity.get(AnimationComponent.class);
    }

    public boolean goToPos(ShortPoint2D targetPos) {
        path = gameC.getGrid().calculatePathTo(movC, targetPos);
        return path != null;
    }

    public void followPath(Path path) {
        assert path != null: "path mustn't be null";
        this.path = path;
    }

    public Path preSearchPath(boolean dijkstra, short centerX, short centerY, short radius, ESearchType searchType) {
        if (dijkstra) {
            return gameC.getGrid().searchDijkstra(movC, centerX, centerY, radius, searchType);
        } else {
            return gameC.getGrid().searchInArea(movC, centerX, centerY, radius, searchType);
        }
    }

    @Override
    public void OnUpdate() {
        if (path == null || !path.hasNextStep()) {
            // if path is finished, or canceled
            aniC.stopAnimation();
            path = null;
            return;
        }

        Movable blockingMovable = gameC.getGrid().getMovableAt(path.nextX(), path.nextY());
        if (blockingMovable == null) { // if we can go on to the next step
            if (gameC.getGrid().isValidNextPathPosition(movC, path.getNextPos(), path.getTargetPos())) { // next position is valid
                goSinglePathStep();

            } else { // next position is invalid
                aniC.stopAnimation();
                Path newPath = gameC.getGrid().calculatePathTo(movC, path.getTargetPos()); // try to find a new path

                if (newPath == null) { // no path found
                    path = null;
                } else {
                    this.path = newPath; // continue with new path
                    if (gameC.getGrid().hasNoMovableAt(path.nextX(), path.nextY())) { // path is valid, but maybe blocked (leaving blocked area)
                        goSinglePathStep();
                    }
                }
            }

        } else { // step not possible, so try it next time (push not supported)
            aniC.stopAnimation();
        }
    }

    private void goSinglePathStep() {
        movC.setViewDirection(EDirection.getDirection(movC.getPos(), path.getNextPos()));
        aniC.startAnimation(EMovableAction.WALKING, movC.getMovableType().getStepDurationMs());
        gameC.getGrid().leavePosition(movC.getPos(), (Movable)entity);
        gameC.getGrid().enterPosition(path.getNextPos(), (Movable)entity, false);
        movC.setPos(path.getNextPos());
        aniC.switchStep();
        path.goToNextStep();
    }
}
