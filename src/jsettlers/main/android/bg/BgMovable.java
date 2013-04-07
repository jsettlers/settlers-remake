package jsettlers.main.android.bg;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

public class BgMovable implements IMovable {

	@Override
	public byte getPlayerId() {
		return 0;
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
	}

	@Override
	public void stopOrStartWorking(boolean stop) {
	}

	@Override
	public ESelectionType getSelectionType() {
		return null;
	}

	@Override
	public void setSoundPlayed() {
	}

	@Override
	public boolean isSoundPlayed() {
		return false;
	}

	@Override
	public EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	public EAction getAction() {
		return EAction.NO_ACTION;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.SOUTH_EAST;
	}

	@Override
	public float getMoveProgress() {
		return 0;
	}

	@Override
	public EMaterialType getMaterial() {
		return null;
	}

	@Override
	public ShortPoint2D getPos() {
		return null;
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public boolean isRightstep() {
		return false;
	}

}
