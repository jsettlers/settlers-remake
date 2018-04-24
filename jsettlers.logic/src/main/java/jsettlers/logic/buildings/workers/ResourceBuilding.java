/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.logic.buildings.workers;

import jsettlers.algorithms.datastructures.BooleanMovingAverage;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.player.Player;

/**
 * A {@link WorkerBuilding} implementing the {@link IResourceBuilding} interface to supply productivity information to the UI.
 * 
 * @author Andreas Eberle
 *
 */
public class ResourceBuilding extends WorkerBuilding implements IBuilding.IResourceBuilding {
	private static final long serialVersionUID = -828476867565686860L;
	private static final int NUMBER_OF_MOVING_AVERAGE_ELEMENTS = 12;

	private final BooleanMovingAverage movingAverage;

	public ResourceBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
		movingAverage = new BooleanMovingAverage(NUMBER_OF_MOVING_AVERAGE_ELEMENTS, false);
	}

	@Override
	public float getProductivity() {
		return movingAverage.getAverage();
	}

	@Override
	public int getRemainingResourceAmount() {
		return -1;
	}

	protected void productivityActionExecuted(boolean successfully) {
		movingAverage.insertValue(successfully);
	}
}
