/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.android.bg;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

public class BgMovable implements IMovable {

	private ShortPoint2D point;

	public BgMovable(ShortPoint2D point) {
		this.point = point;
	}

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
		return EMaterialType.NO_MATERIAL;
	}

	@Override
	public ShortPoint2D getPos() {
		return point;
	}

	@Override
	public float getHealth() {
		return 0;
	}

	@Override
	public boolean isRightstep() {
		return false;
	}

	@Override
	public int getID() {
		return 0;
	}

}
