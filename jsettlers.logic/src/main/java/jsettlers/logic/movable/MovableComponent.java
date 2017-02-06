package jsettlers.logic.movable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class MovableComponent implements Component {

    public short getViewDistance() {
        //Movable
        return 0;
    }

    public boolean needsPlayersGround() {
        //Movable
        return false;
    }

    public ShortPoint2D getPos() {
        //Movable
        return null;
    }

    public byte getPlayerId() {
        //Movable
        return 0;
    }

    public EMovableType getMovableType() {
        //Movable
        return null;
    }
}
