package jsettlers.logic.movable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.input.IGuiMovable;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IAttackableMovable;
import jsettlers.logic.movable.interfaces.IDebugable;
import jsettlers.logic.timer.IScheduledTimerable;

/**
 * Created by jt-1 on 2/5/2017.
 */

public final class Movable2 extends Entity implements IPathCalculatable, IViewDistancable, IGuiMovable,
        IAttackableMovable, IDebugable, Serializable {
    private static final long serialVersionUID = 6873799805741909118L;
    private static final HashMap<Integer, Movable> movablesByID = new HashMap<>();
    private static final ConcurrentLinkedQueue<Movable> allMovables = new ConcurrentLinkedQueue<>();

    // endregion

    //region Interface Implementations
    @Override
    public short getViewDistance() {
        return this.get(MovableComponent.class).getViewDistance();
    }

    @Override
    public boolean needsPlayersGround() { return this.get(MovableComponent.class).needsPlayersGround(); }

    @Override
    public EDirection getDirection() { return this.get(MovableComponent.class).getViewDirection(); }

    @Override
    public EMovableAction getAction() { return this.get(AnimationComponent.class).getAnimation(); }

    @Override
    public float getMoveProgress() {
        return get(AnimationComponent.class).getAnimationProgress();
    }

    @Override
    public EMaterialType getMaterial() {
        return get(MaterialComponent.class).getMaterial();
    }

    @Override
    public void receiveHit(float strength, ShortPoint2D attackerPos, byte attackingPlayer) {
        get(AttackableComponent.class).receiveHit(strength, attackerPos, attackingPlayer);
    }

    @Override
    public float getHealth() {
        return get(AttackableComponent.class).getHealth();
    }

    @Override
    public boolean isRightstep() {
        return get(AnimationComponent.class).isRightstep();
    }

    @Override
    public void stopOrStartWorking(boolean stop) {
        get(WorkComponent.class).setIsWorking(!stop);
    }

    @Override
    public ShortPoint2D getPos() {
        return get(MovableComponent.class).getPos();
    }

    @Override
    public boolean isSelected() {
        return get(SelectableComponent.class).isSelected();
    }

    @Override
    public void setSelected(boolean selected) {
        get(SelectableComponent.class).setSelected(selected);
    }

    @Override
    public ESelectionType getSelectionType() {
        return get(SelectableComponent.class).getSelectionType();
    }

    @Override
    public byte getPlayerId() {
        return get(MovableComponent.class).getPlayerId();
    }

    @Override
    public boolean isAttackable() {
        return get(AttackableComponent.class).isAttackable();
    }

    @Override
    public void setSoundPlayed() {
        get(AnimationComponent.class).setSoundPlayed();
    }

    @Override
    public boolean isSoundPlayed() {
        return get(AnimationComponent.class).isSoundPlayed();
    }

    @Override
    public EMovableType getMovableType() {
        return get(MovableComponent.class).getMovableType();
    }

    @Override
    public boolean isTower() {
        return false;
    }

    @Override
    public void debug() {
        System.out.println("debug: " + this);
    }

    @Override
    public void informAboutAttackable(IAttackable attackable) {
        get(AttackableComponent.class).informAboutAttackable((Entity)attackable);
    }

    //endregion
}
