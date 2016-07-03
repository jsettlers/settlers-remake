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
package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public class LumberJackTargetFinder extends CuttingBuildingTargetFinder implements ITargetFinder {

	private boolean disabled;

	public LumberJackTargetFinder(
			AiStatistics aiStatistics, byte playerId, int searchDistance, int cuttableObjectsPerBuilding) {
		super(aiStatistics, playerId, searchDistance, EBuildingType.LUMBERJACK, cuttableObjectsPerBuilding, EMapObjectType.TREE_ADULT);
		disabled = false;
	}

	@Override
	public ShortPoint2D findTarget(AiPositions playerBorder, ShortPoint2D center) {
		if (disabled) {
			return null;
		}

		ShortPoint2D target = findTarget(playerBorder, center, aiStatistics.getTreesForPlayer(playerId).size());
		if (target == null) {
			disabled = true;
		}
		return target;
	}
}
