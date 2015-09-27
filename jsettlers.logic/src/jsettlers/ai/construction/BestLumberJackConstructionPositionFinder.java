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
package jsettlers.ai.construction;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;

/**
 * Assumptions: trees are placed as groups or as a single tree on the map
 * 
 * Algorithm: find all possible construction points within the borders of the player - calculates a score based on the distance from the most near
 * tree of the possible construction position - takes the position with the best score (lowest distance to the most near tree)
 * 
 * @author codingberlin
 */
public class BestLumberJackConstructionPositionFinder extends BestWorkareaConstructionPositionFinder {

	public BestLumberJackConstructionPositionFinder(EBuildingType buildingType) {
		super(buildingType);
	}

	@Override
	protected AiPositions getRelevantObjects(AiStatistics aiStatistics, byte playerId) {
		return aiStatistics.getTreesForPlayer(playerId);
	}
}
