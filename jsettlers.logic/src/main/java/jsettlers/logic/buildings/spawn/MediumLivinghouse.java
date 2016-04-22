/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.logic.buildings.spawn;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.player.Player;

/**
 * This class represents a medium apartment house that spawns {@link #PRODUCE_LIMIT} ({@value #PRODUCE_LIMIT}) new bearers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MediumLivinghouse extends SpawnBuilding {
	private static final long serialVersionUID = 6182479871695461138L;

	private static final byte PRODUCE_LIMIT = 30;

	public MediumLivinghouse(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.MEDIUM_LIVINGHOUSE, player, position, buildingsGrid);
	}

	@Override
	protected EMovableType getMovableType() {
		return EMovableType.BEARER;
	}

	@Override
	protected byte getProduceLimit() {
		return PRODUCE_LIMIT;
	}

}
