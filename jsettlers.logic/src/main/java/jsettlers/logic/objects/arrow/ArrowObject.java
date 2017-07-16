/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.objects.arrow;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IArrowMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MathUtils;
import jsettlers.logic.objects.ProgressingSoundableObject;

public final class ArrowObject extends ProgressingSoundableObject implements IArrowMapObject {
	private static final long serialVersionUID = 1702902724559733166L;

	private static final float SECONDS_PER_TILE = 0.011f;
	public static final float MIN_DECOMPOSE_DELAY = 60;

	private final short sourceX;
	private final short sourceY;
	private final float hitStrength;
	private final byte shooterPlayerId;

	private final IArrowAttackableGrid grid;

	public ArrowObject(IArrowAttackableGrid grid, ShortPoint2D targetPos, ShortPoint2D shooterPos, byte shooterPlayerId, float hitStrength) {
		super(targetPos);

		this.grid = grid;
		this.sourceX = shooterPos.x;
		this.sourceY = shooterPos.y;
		this.hitStrength = hitStrength;
		this.shooterPlayerId = shooterPlayerId;

		super.setDuration((float) (SECONDS_PER_TILE * MathUtils.hypot(shooterPos.x - targetPos.x, shooterPos.y - targetPos.y)));
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.ARROW;
	}

	@Override
	public boolean cutOff() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public EDirection getDirection() {
		return EDirection.getApproxDirection(getSourceX(), getSourceY(), getTargetX(), getTargetY());
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		grid.hitWithArrowAt(this);
	}

	@Override
	public short getSourceX() {
		return sourceX;
	}

	@Override
	public short getSourceY() {
		return sourceY;
	}

	@Override
	public short getTargetX() {
		return super.getX();
	}

	@Override
	public short getTargetY() {
		return super.getY();
	}

	public float getHitStrength() {
		return hitStrength;
	}

	public ShortPoint2D getSourcePos() {
		return new ShortPoint2D(sourceX, sourceY);
	}

	public byte getShooterPlayerId() {
		return shooterPlayerId;
	}
}
