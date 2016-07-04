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
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public class MineTargetFinder extends AbstractTargetFinder implements ITargetFinder {

	private final EResourceType resourceType;
	private final EBuildingType mineBuildingType;

	public MineTargetFinder(AiStatistics aiStatistics, byte playerId, int searchDistance, EResourceType resourceType,
			EBuildingType mineBuildingType) {
		super(aiStatistics, playerId, searchDistance);
		this.resourceType = resourceType;
		this.mineBuildingType = mineBuildingType;
	}

	@Override
	public ShortPoint2D findTarget(AiPositions playerBorder, ShortPoint2D center) {
		if (aiStatistics.resourceCountInDefaultPartition(resourceType) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(mineBuildingType, playerId) + 1;
		int tiles = mineBuildingType.getProtectedTiles().length * 2;

		if (aiStatistics.resourceCountOfPlayer(resourceType, playerId) > tiles * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestResourcePointInDefaultPartitionFor(center, resourceType, searchDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = playerBorder.getNearestPoint(nearestResourceAbroad, searchDistance);
		return target;
	}
}