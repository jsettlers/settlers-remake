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
package jsettlers.graphics.test;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;

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
	public ShortPoint2D getPos() {
		return this.position.getPos();
	}

	@Override
	public byte getPlayerId() {
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

	@Override
	public ESelectionType getSelectionType() {
		return ESelectionType.PEOPLE;
	}

	@Override
	public int getID() {
		return 0;
	}
}
