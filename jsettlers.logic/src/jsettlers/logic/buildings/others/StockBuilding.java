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

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.MaterialSet;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.IRequestsStackGrid;
import jsettlers.logic.stack.StockBuildingStackGroup;

/**
 * This is a stock building that can store materials.
 * 
 * @author Andreas Eberle
 * 
 */
public final class StockBuilding extends Building implements IBuilding.IStock {
	private static final long serialVersionUID = 1L;

	private static final int UPDATE_PERIOD = 5000;

	private final ArrayList<StockBuildingStackGroup> stockStacks = new ArrayList<>();

	private final MaterialSet acceptedMaterials = null;

	public StockBuilding(Player player) {
		super(EBuildingType.STOCK, player);

		// acceptedMaterials.set(EMaterialType.GOLD, true);
		// acceptedMaterials.set(EMaterialType.GEMS, true);
	}

	@Override
	public boolean isOccupied() {
		return false;
	}

	@Override
	protected void positionedEvent(ShortPoint2D pos) {
	}

	@Override
	protected int subTimerEvent() {
		if (usesGlobalSettings()) {
			updateStockStackRequests();
		}
		return UPDATE_PERIOD;
	}

	@Override
	protected int constructionFinishedEvent() {
		IRequestsStackGrid grid = getGrid().getRequestStackGrid();
		for (RelativeStack stack : getBuildingType().getRequestStacks()) {
			ShortPoint2D pos = stack.calculatePoint(this.getPos());
			stockStacks.add(new StockBuildingStackGroup(grid, pos, getBuildingType()));
		}
		updateStockStackRequests();
		return UPDATE_PERIOD;
	}

	private void updateStockStackRequests() {
		MaterialSet materials = getAcceptedMaterials();
		for (StockBuildingStackGroup stack : stockStacks) {
			stack.setAcceptedMaterials(materials);
		}
	}

	@Override
	protected void killedEvent() {
		super.killedEvent();

		for (StockBuildingStackGroup stack : stockStacks) {
			stack.killEvent();
		}
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public boolean acceptsMaterial(EMaterialType material) {
		return getAcceptedMaterials().contains(material);
	}

	private MaterialSet getAcceptedMaterials() {
		MaterialSet materials;
		if (acceptedMaterials != null) {
			materials = acceptedMaterials;
		} else {
			materials = getGrid().getRequestStackGrid().getDefaultStockMaterials(getPos());
		}
		return materials;
	}

	@Override
	public boolean usesGlobalSettings() {
		return acceptedMaterials == null;
	}
}
