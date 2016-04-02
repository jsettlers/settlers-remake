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
package jsettlers.logic.buildings.others;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;

/**
 * This is a stock building that can store materials.
 * 
 * @author Andreas Eberle
 * 
 */
public final class StockBuilding extends Building implements IBuilding.IStock {
	private static final long serialVersionUID = 1L;

	public StockBuilding(Player player) {
		super(EBuildingType.STOCK, player);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	protected int subTimerEvent() {
		return -1;
	}

	@Override
	protected int constructionFinishedEvent() {
		return -1;
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public boolean acceptsMaterial(EMaterialType material) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean usesGlobalSettings() {
		// TODO Auto-generated method stub
		return true;
	}
}
