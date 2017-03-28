package jsettlers.logic.movable;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class MovableComponent extends Component {
    private final EMovableType movableType;
    private byte playerId;
    private ShortPoint2D position;
    private EDirection viewDirection;

    public MovableComponent(EMovableType movableType, byte playerId, ShortPoint2D position, EDirection viewDirection) {
        this.movableType = movableType;
        this.playerId = playerId;
        this.position = position;
        this.viewDirection = viewDirection;
    }

    public void setViewDirection(EDirection viewDirection) {
        this.viewDirection = viewDirection;
    }

    public EDirection getViewDirection() {
        return viewDirection;
    }

    public short getViewDistance() {
        return Constants.MOVABLE_VIEW_DISTANCE;
    }

    public boolean needsPlayersGround() {
        return movableType.needsPlayersGround();
    }

    public ShortPoint2D getPos() {
        return position;
    }

    public void setPos(ShortPoint2D position) {
        this.position = position;
    }

    public byte getPlayerId() {
        return playerId;
    }

    public void setPlayerId(byte playerId) {
        this.playerId = playerId;
    }

    public EMovableType getMovableType() {
        return movableType;
    }
}
