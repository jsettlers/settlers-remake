package jsettlers.logic.movable;

import jsettlers.common.position.ShortPoint2D;

/**
 * Created by jt-1 on 2/6/2017.
 */

public class AttackableComponent extends Component {
    private float health;
    private boolean isAttackable;
    private Entity informedAboutAttackableEvent;

    public AttackableComponent(boolean isAttackable) {
        this.isAttackable = isAttackable;
    }

    public void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer) {
        health -= strength;
    }

    public float getHealth() {
        return health;
    }

    public boolean isAttackable() {
        return isAttackable;
    }

    public void setAttackable(boolean isAttackable) {
        this.isAttackable = isAttackable;
    }

    public void informAboutAttackable(Entity other) {
        informedAboutAttackableEvent = other;
    }

    @Override
    protected void OnLateUpdate() {
        informedAboutAttackableEvent = null;
    }
}
