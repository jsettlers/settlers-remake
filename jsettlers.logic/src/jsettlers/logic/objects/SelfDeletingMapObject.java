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
package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;

/**
 * Object that can show any {@link EMapObjectType} on the map for a given time. When the time is finished, it removes itself from the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class SelfDeletingMapObject extends ProgressingObject implements IPlayerable {
	private static final long serialVersionUID = 261552015645456635L;

	private final EMapObjectType type;
	private final byte player;

	/**
	 * Creates and positions this map object on the map.
	 * 
	 * @param pos
	 *            position the map object should be on the grid
	 * @param type
	 *            type to be returned to jsettlers.graphics
	 */
	public SelfDeletingMapObject(ShortPoint2D pos, EMapObjectType type, float duration) {
		this(pos, type, duration, (byte) -1);
	}

	public SelfDeletingMapObject(ShortPoint2D pos, EMapObjectType type, float duration, byte player) {
		super(pos);
		this.player = player;
		this.type = type;
		super.setDuration(duration);
	}

	@Override
	public EMapObjectType getObjectType() {
		return type;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	public byte getPlayerId() {
		return player;
	}

	@Override
	protected void changeState() {
		throw new UnsupportedOperationException();
	}
}
