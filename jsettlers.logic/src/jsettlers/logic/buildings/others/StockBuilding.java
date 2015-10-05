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
import java.util.BitSet;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
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

	private final ArrayList<StockBuildingStackGroup> stockStacks = new ArrayList<>();

	private final BitSet acceptedMaterials = new BitSet();

	public StockBuilding(Player player) {
		super(EBuildingType.STOCK, player);

		acceptedMaterials.set(EMaterialType.GOLD.ordinal);
		acceptedMaterials.set(EMaterialType.GEMS.ordinal);
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
		return -1;
	}

	@Override
	protected int constructionFinishedEvent() {
		IRequestsStackGrid grid = getGrid().getRequestStackGrid();
		for (RelativeStack stack : getBuildingType().getRequestStacks()) {
			ShortPoint2D pos = stack.calculatePoint(this.getPos());
			stockStacks.add(new StockBuildingStackGroup(grid, pos, getBuildingType()));
		}
		updateStockStackRequests();
		return -1;
	}

	private void updateStockStackRequests() {
		EMaterialType[] materials = new EMaterialType[acceptedMaterials.cardinality()];
		for (int i = acceptedMaterials.nextSetBit(0), j = 0; i >= 0; i = acceptedMaterials.nextSetBit(i + 1), j++) {
			materials[j] = EMaterialType.values[i];
		}
		for (StockBuildingStackGroup stack : stockStacks) {
			stack.setAcceptedMaterials(materials);
		}
	}

	@Override
	protected void killedEvent() {
		super.killedEvent();

		for (StockBuildingStackGroup stack : stockStacks) {
			stack.releaseAll();
		}
	}

	@Override
	protected EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_DOOR;
	}

	@Override
	public boolean acceptsMaterial(EMaterialType material) {
		return acceptedMaterials.get(material.ordinal);
	}

	@Override
	public boolean usesGlobalSettings() {
		// TODO Auto-generated method stub
		return false;
	}
}
