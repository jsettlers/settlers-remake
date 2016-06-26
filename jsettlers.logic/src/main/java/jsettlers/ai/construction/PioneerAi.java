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
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public class PioneerAi {

	public static final int FISH_NEEDED_BY_FISHER = 10;
	public static final int MAX_SEARCH_DISTANCE = 900;

	private final AiStatistics aiStatistics;
	private final byte playerId;
	private final int searchRadius;

	private ShortPoint2D lastResourceTarget;
	private boolean enoughTreesFoundAlready;

	public PioneerAi(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.searchRadius = aiStatistics.getMainGrid().getWidth() / 2;
		this.lastResourceTarget = aiStatistics.getPositionOfPartition(playerId);
		this.enoughTreesFoundAlready = false;
	}

	public ShortPoint2D findResourceTarget() {
		ShortPoint2D newTarget = findResourceTargetNearLastTarget();
		if (newTarget != null) {
			lastResourceTarget = newTarget;
		}
		return newTarget;
	}

	private ShortPoint2D findResourceTargetNearLastTarget() {
		// TODO wenn turmbau dann richtung feinde (in der naehe) ansonsten richtung wald\Stein in der naehe
		// TODO isEndgame setyen, wenn livinghouses nicht mehr gebaut werden koennen
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);

		if (!enoughTreesFoundAlready) {
			ShortPoint2D treeTarget = targetForCuttingBuilding(myBorder, EMapObjectType.TREE_ADULT,
					EBuildingType.LUMBERJACK, aiStatistics.getTreesForPlayer(playerId), 10);
			if (treeTarget == null)
				enoughTreesFoundAlready = true;
			else
				return treeTarget;
		}

		ShortPoint2D target = targetForNearStoneFields();
		if (target != null)
			return target;

		target = targetForCuttingBuilding(myBorder, EMapObjectType.STONE, EBuildingType.STONECUTTER,
				aiStatistics.getStonesForPlayer(playerId), 4);
		if (target != null)
			return target;

		target = targetForOtherPartition(myBorder);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.COAL, EBuildingType.COALMINE);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.IRONORE, EBuildingType.IRONMINE);
		if (target != null)
			return target;

		target = targetForRivers(myBorder);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.GOLDORE, EBuildingType.GOLDMINE);
		if (target != null)
			return target;

		return targetForFish(myBorder);
	}

	private ShortPoint2D targetForOtherPartition(AiPositions myBorder) {
		AiPositions otherPartitionBorder = aiStatistics.getOtherPartitionBorderOf(playerId);
		if (otherPartitionBorder.size() == 0) {
			return null;
		}

		ShortPoint2D nearestOtherPartitionBorderPoint = otherPartitionBorder.getNearestPoint(lastResourceTarget,
				aiStatistics.getMainGrid().getWidth());
		int searchDistance = nearestOtherPartitionBorderPoint.getOnGridDistTo(lastResourceTarget) + 10;
		return myBorder.getNearestPoint(nearestOtherPartitionBorderPoint, searchDistance);
	}

	private ShortPoint2D targetForNearStoneFields() {
		return aiStatistics.getStonesNearBy(playerId).getNearestPoint(lastResourceTarget, MAX_SEARCH_DISTANCE);
	}

	private ShortPoint2D targetForCuttingBuilding(AiPositions myBorder, EMapObjectType cuttableObjectType, EBuildingType cuttingBuildingType,
			AiPositions cuttableObjectsOfPlayer, int factor) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(cuttingBuildingType, playerId) + 1;
		if (cuttableObjectsOfPlayer.size() > buildingCount * factor)
			return null;

		ShortPoint2D nearestCuttableObject = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(
				lastResourceTarget, cuttableObjectType, MAX_SEARCH_DISTANCE);
		if (nearestCuttableObject == null)
			return null;

		return myBorder.getNearestPoint(nearestCuttableObject, CommonConstants.TOWER_RADIUS);
	}

	private ShortPoint2D targetForRivers(AiPositions myBorder) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.WATERWORKS, playerId) + 1;
		if (aiStatistics.getRiversForPlayer(playerId).size() > buildingCount * 5)
			return null;

		ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(lastResourceTarget, MAX_SEARCH_DISTANCE);
		if (nearestRiver == null)
			return null;

		return myBorder.getNearestPoint(nearestRiver, searchRadius);
	}

	private ShortPoint2D targetForMine(AiPositions myBorder, EResourceType resourceType, EBuildingType buildingType) {
		if (aiStatistics.resourceCountInDefaultPartition(resourceType) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) + 1;
		int tiles = buildingType.getProtectedTiles().length * 2;

		if (aiStatistics.resourceCountOfPlayer(resourceType, playerId) > tiles * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestResourcePointInDefaultPartitionFor(lastResourceTarget, resourceType, MAX_SEARCH_DISTANCE);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	private ShortPoint2D targetForFish(AiPositions myBorder) {
		if (aiStatistics.resourceCountInDefaultPartition(EResourceType.FISH) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FISHER, playerId) + 1;

		if (aiStatistics.resourceCountOfPlayer(EResourceType.FISH, playerId) > FISH_NEEDED_BY_FISHER * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestFishPointForPlayer(lastResourceTarget, playerId, MAX_SEARCH_DISTANCE);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	public ShortPoint2D findBroadenTarget() {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		ShortPoint2D target = myBorder.getNearestPoint(centroid(), searchRadius);
		return target;
	}

	private ShortPoint2D centroid() {
		AiPositions landForPlayer = aiStatistics.getLandForPlayer(playerId);
		long x = 0;
		long y = 0;
		for (int i = 0; i < landForPlayer.size(); i += 50) {
			ShortPoint2D position = landForPlayer.get(i);
			x += position.x;
			y += position.y;
		}
		int divisor = landForPlayer.size() / 50;
		return new ShortPoint2D((int) (x / divisor), (int) (y / divisor));
	}

}
