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
package jsettlers.logic.objects.growing.tree;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.sound.ISoundable;
import jsettlers.logic.objects.growing.GrowingObject;

/**
 * This is a tree on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public class Tree extends GrowingObject implements ISoundable {
	private static final long serialVersionUID = 8241068714975746824L;

	public static final float GROWTH_DURATION = 7 * 60;
	public static final float DECOMPOSE_DURATION = 2 * 60;

	private static final RelativePoint[] BLOCKED = new RelativePoint[] { new RelativePoint(0, 0) };

	private transient boolean soundPlayed;

	/**
	 * Creates a new Tree.
	 * 
	 * @param pos
	 */
	public Tree(ShortPoint2D pos) {
		super(pos, EMapObjectType.TREE_GROWING);
	}

	@Override
	public RelativePoint[] getBlockedTiles() {
		return BLOCKED;
	}

	@Override
	protected float getGrowthDuration() {
		return GROWTH_DURATION;
	}

	@Override
	protected float getDecomposeDuration() {
		return DECOMPOSE_DURATION;
	}

	@Override
	public void setSoundPlayed() {
		soundPlayed = true;
	}

	@Override
	public boolean isSoundPlayed() {
		return soundPlayed;
	}

	@Override
	protected EMapObjectType getDeadState() {
		return EMapObjectType.TREE_DEAD;
	}

	@Override
	protected EMapObjectType getAdultState() {
		return EMapObjectType.TREE_ADULT;
	}
}
