package jsettlers.graphics.test;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;

public class TestSettler implements IMovable {

	private EDirection direction;
	private TestTile position;
	private short progress = 0;
	private final byte player;
	private EMaterialType material = EMaterialType.NO_MATERIAL;
	private final EMovableType type;

	public TestSettler(EDirection direction, EMovableType type, TestTile tile, byte player) {
		this.type = type;
		this.setDirection(direction);
		this.setPosition(tile);
		this.player = player;
	}

	@Override
	public EAction getAction() {
		return EAction.WALKING;
	}

	@Override
	public EDirection getDirection() {
		return this.direction;
	}

	@Override
	public EMaterialType getMaterial() {
		return this.material;
	}

	public void setMaterial(EMaterialType material) {
		this.material = material;
	}

	@Override
	public EMovableType getMovableType() {
		return this.type;
	}

	@Override
	public ISPosition2D getPos() {
		return this.position;
	}

	@Override
	public byte getPlayer() {
		return this.player;
	}

	public void increaseProgress() {
		this.progress++;
	}

	@Override
	public float getMoveProgress() {
		return Math.min(0.1f * this.progress, 1);
	}

	public boolean moveOn() {
		return (0.1f * this.progress) > 1;
	}

	public void setPosition(TestTile position) {
		this.position = position;
		this.progress = 0;
	}

	public void setDirection(EDirection direction) {
		this.direction = direction;
		this.progress = 0;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean b) {
	}

	@Override
	public final float getHealth() {
		return 0;
	}

	@Override
	public final void stopOrStartWorking(boolean stop) {
	}

	@Override
	public final boolean isRightstep() {
		return false;
	}

	@Override
	public final void setSoundPlayed() {
	}

	@Override
	public final boolean isSoundPlayed() {
		return true; // prevent playing of sound
	}
}
