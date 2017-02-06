package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.IAttackable;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class AttackableComponent implements Component {
    public void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer) {
        //Attackable
    }

    public float getHealth() {
        //Attackable
        return 0;
    }

    public boolean isAttackable() {
        //Attackable
        return false;
    }

    public boolean isTower() {
        //Attackable
        return false;
    }

    public void informAboutAttackable(IAttackable attackable) {
        //AttackableComponent
    }
}
