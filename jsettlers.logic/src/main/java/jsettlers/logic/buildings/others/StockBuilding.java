/*******************************************************************************
 * Copyright (c) 2014 - 2017
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
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.multi.MultiRequestAndOfferStack;
import jsettlers.logic.buildings.stack.multi.MaterialRequestSettings;
import jsettlers.logic.buildings.stack.multi.MultiRequestStackSharedData;
import jsettlers.logic.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Andreas Eberle.
 */
public class StockBuilding extends Building implements IBuilding.IStock {
	private final MaterialRequestSettings stockSettings;

	public StockBuilding(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.STOCK, player, position, buildingsGrid);
		stockSettings = new MaterialRequestSettings(buildingsGrid.getRequestStackGrid().getPartitionStockSettings(position));
	}

	@Override
	protected List<IRequestStack> createWorkStacks() {
		List<IRequestStack> newStacks = new LinkedList<>();

		MultiRequestStackSharedData sharedData = new MultiRequestStackSharedData(stockSettings);

		for (RelativeStack stack : type.getRequestStacks()) {
			newStacks.add(
					new MultiRequestAndOfferStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), type, super.getPriority(), sharedData));
		}

		return newStacks;
	}

	public void setAcceptedMaterial(EMaterialType materialType, boolean accept) {
		stockSettings.setRequestedAmount(materialType, accept ? Short.MAX_VALUE : 0);
	}

	@Override
	public MaterialRequestSettings getStockSettings() {
		return stockSettings;
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
	public boolean isOccupied() {
		return true;
	}
}
