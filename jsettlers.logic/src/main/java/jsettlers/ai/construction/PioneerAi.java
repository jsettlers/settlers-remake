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
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

import java.util.List;

/**
 * @author codingberlin
 */
public class PioneerAi {

	public static final int FISH_NEEDED_BY_FISHER = 10;

	public static ShortPoint2D findResourceTarget(AiStatistics aiStatistics, byte playerId) {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		ShortPoint2D myCenter = aiStatistics.getPositionOfPartition(playerId);
int maxDistance =	halfDistanceToNearestEnemy(aiStatistics, playerId, myCenter);

		ShortPoint2D target = targetForCuttingBuilding(aiStatistics, myBorder, myCenter, playerId, EMapObjectType.TREE_ADULT,
				EBuildingType.LUMBERJACK, aiStatistics.getTreesForPlayer(playerId), 6, maxDistance);
		if (target != null)
			return target;

		target = targetForCuttingBuilding(aiStatistics, myBorder, myCenter, playerId, EMapObjectType.STONE, EBuildingType.STONECUTTER,
				aiStatistics.getStonesForPlayer(playerId), 4, maxDistance);
		if (target != null)
			return target;

		target = targetForNearStoneFields(aiStatistics, myBorder, playerId);
		if (target != null)
			return target;

		target = targetForMine(aiStatistics, myBorder, myCenter, playerId, EResourceType.COAL, EBuildingType.COALMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(aiStatistics, myBorder, myCenter, playerId, EResourceType.IRONORE, EBuildingType.IRONMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForRivers(aiStatistics, myBorder, myCenter, playerId, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(aiStatistics, myBorder, myCenter, playerId, EResourceType.GOLDORE, EBuildingType.GOLDMINE, maxDistance);
		if (target != null)
			return target;

		return targetForFish(aiStatistics, myBorder, myCenter, playerId, maxDistance);
	}

	private static ShortPoint2D targetForNearStoneFields(AiStatistics aiStatistics, AiPositions myBorder, byte playerId) {
		for (ShortPoint2D stonePosition : aiStatistics.getStonesInDefaultPosition()) {
			ShortPoint2D target = myBorder.getNearestPoint(stonePosition, 10, null);
			if (target != null) {
				return target;
			}
		}
		return null;
	}

	private static int halfDistanceToNearestEnemy(AiStatistics aiStatistics, byte playerId, ShortPoint2D myCenter) {
		int distance = Integer.MAX_VALUE;
		for (byte enemyId :
		aiStatistics.getAliveEnemiesOf(playerId)){
			int enemyDistance = myCenter.getOnGridDistTo(aiStatistics.getPositionOfPartition(enemyId));
			if (enemyDistance < distance) {
				distance = enemyDistance;
			}
		}
		return (int) Math.ceil(distance / 1.9F);
	}

	public static ShortPoint2D targetForCuttingBuilding(AiStatistics aiStatistics, AiPositions myBorder, ShortPoint2D myCenter, byte playerId,
			EMapObjectType cuttableObjectType, EBuildingType cuttingBuildingType, AiPositions cuttableObjectsOfPlayer, int factor, int maxDistance) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(cuttingBuildingType, playerId) + 1;
		if (cuttableObjectsOfPlayer.size() > buildingCount * factor)
			return null;

		List<ShortPoint2D> cuttingBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(cuttingBuildingType, playerId);
		ShortPoint2D referencePoint = cuttingBuildings.size() > 0 ? cuttingBuildings.get(0) : myCenter;
		ShortPoint2D nearestCuttableObject = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(referencePoint, cuttableObjectType,
				maxDistance);
		if (nearestCuttableObject == null)
			return null;

		return myBorder.getNearestPoint(nearestCuttableObject);
	}

	public static ShortPoint2D targetForRivers(AiStatistics aiStatistics, AiPositions myBorder, ShortPoint2D myCenter, byte playerId, int maxDistance) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.WATERWORKS, playerId) + 1;
		if (aiStatistics.getRiversForPlayer(playerId).size() > buildingCount * 5)
			return null;

		ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(myCenter, maxDistance);
		if (nearestRiver == null)
			return null;

		return myBorder.getNearestPoint(nearestRiver);
	}

	public static ShortPoint2D targetForMine(AiStatistics aiStatistics, AiPositions myBorder, ShortPoint2D myCenter, byte playerId,
			EResourceType resourceType, EBuildingType buildingType, int maxDistance) {
		if (aiStatistics.resourceCountInDefaultPartition(resourceType) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) + 1;
		int tiles = buildingType.getProtectedTiles().length * 2;

		if (aiStatistics.resourceCountOfPlayer(resourceType, playerId) > tiles * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestResourcePointInDefaultPartitionFor(myCenter, resourceType, maxDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad);
		return target;
	}

	public static ShortPoint2D targetForFish(AiStatistics aiStatistics, AiPositions myBorder, ShortPoint2D myCenter, byte playerId, int maxDistance) {
		if (aiStatistics.resourceCountInDefaultPartition(EResourceType.FISH) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FISHER, playerId) + 1;

		if (aiStatistics.resourceCountOfPlayer(EResourceType.FISH, playerId) > FISH_NEEDED_BY_FISHER * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestFishPointForPlayer(myCenter, playerId, maxDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad);
		return target;
	}

	public static ShortPoint2D findBroadenTarget(AiStatistics aiStatistics, byte playerId) {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		return myBorder.getNearestPoint(getCentroidOf(aiStatistics, playerId));
	}

	private static ShortPoint2D getCentroidOf(AiStatistics aiStatistics, byte playerId) {
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
