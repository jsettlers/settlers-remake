package jsettlers.logic.movable;

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

    public MovableComponent(EMovableType movableType, byte playerId, ShortPoint2D position) {
        this.movableType = movableType;
        this.playerId = playerId;
        this.position = position;
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
