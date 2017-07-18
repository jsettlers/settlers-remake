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
package jsettlers.logic.objects.building;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayer;
import jsettlers.common.player.IPlayerable;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;

public final class FlagMapObject extends AbstractHexMapObject implements IPlayerable {
	private static final long serialVersionUID = 3898658161529753794L;

	private final EMapObjectType flagType;
	private final IPlayer player;

	public FlagMapObject(EMapObjectType flagType, IPlayer player) {
		this.flagType = flagType;
		this.player = player;
		assert flagType == EMapObjectType.FLAG_DOOR || flagType == EMapObjectType.FLAG_ROOF : "flag must be a flag";
	}

	@Override
	public EMapObjectType getObjectType() {
		return flagType;
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
	public boolean isBlocking() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	@Override
	public IPlayer getPlayer() {
		return player;
	}
}
