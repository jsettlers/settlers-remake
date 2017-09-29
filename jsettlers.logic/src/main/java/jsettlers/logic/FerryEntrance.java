package jsettlers.logic;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.Movable;

/**
 * Created by Rudolf Polzer on 14.06.17.
 */

public class FerryEntrance {
    private final Movable ferry;
    private final ShortPoint2D entrance;

    public FerryEntrance (Movable ferry, ShortPoint2D entrance) {
        this.ferry = ferry;
        this.entrance = entrance;
    }

    public Movable getFerry() {
        return this.ferry;
    }

    public ShortPoint2D getEntrance() {
        return this.entrance;
    }
}
