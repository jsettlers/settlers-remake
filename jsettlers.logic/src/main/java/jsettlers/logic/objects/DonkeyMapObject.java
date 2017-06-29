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
package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.objects.AbstractObjectsManagerObject;

/**
 * This is a donkey in the donkey farm. It can be fed 4 times until it is grown up. Growing up takes time.
 * 
 * @author Michael Zangl
 */
public class DonkeyMapObject extends AbstractObjectsManagerObject implements IPlayerable {
	private static final long serialVersionUID = -3400083703317328589L;

	public static final float FEED_TIME = 60;
	private static final byte FEED_TIMES = 4;

	private final IPlayer player;
	private boolean feedable;
	private byte feedTimes;

	public DonkeyMapObject(ShortPoint2D pos, IPlayer player) {
		super(pos);
		this.player = player;
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.DONKEY;
	}

	@Override
	public float getStateProgress() {
		return 0;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	protected void changeState() {
		feedable = true;
	}

	/**
	 * Feed this donkey.
	 * 
	 * @return <code>true</code> if feeding was successful.
	 */
	public boolean feed() {
		if (feedable) {
			feedable = false;
			feedTimes++;
			return true;
		} else {
			return false;
		}
	}

	public boolean isFullyFed() {
		return feedTimes >= FEED_TIMES;
	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}
}
