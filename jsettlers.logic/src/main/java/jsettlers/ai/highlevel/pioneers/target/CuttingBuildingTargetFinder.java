/*******************************************************************************
 * Copyright (c) 2016
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public abstract class CuttingBuildingTargetFinder extends AbstractPioneerTargetFinder {

	protected final EBuildingType buildingType;
	protected final int cuttableObjectsPerBuilding;
	protected final EMapObjectType cuttableObjectType;

	public CuttingBuildingTargetFinder(AiStatistics aiStatistics, byte playerId, int searchDistance, EBuildingType buildingType,
			int cuttableObjectsPerBuilding, EMapObjectType cuttableObjectType) {
		super(aiStatistics, playerId, searchDistance);
		this.buildingType = buildingType;
		this.cuttableObjectsPerBuilding = cuttableObjectsPerBuilding;
		this.cuttableObjectType = cuttableObjectType;
	}

	protected ShortPoint2D findTarget(AiPositions playerBorder, ShortPoint2D center, int cuttableObjectsCount) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) + 1;
		if (cuttableObjectsCount > buildingCount * cuttableObjectsPerBuilding)
			return null;

		ShortPoint2D nearestCuttableObject = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(
				center, cuttableObjectType, searchDistance, new SameBlockedPartitionLikePlayerFilter(aiStatistics, playerId));
		if (nearestCuttableObject == null)
			return null;

		return playerBorder.getNearestPoint(nearestCuttableObject, CommonConstants.TOWER_RADIUS);
	}
}
