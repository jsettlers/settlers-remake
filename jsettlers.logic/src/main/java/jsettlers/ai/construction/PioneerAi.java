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

import java.util.List;

/**
 * @author codingberlin
 */
public class PioneerAi {

	public static final int FISH_NEEDED_BY_FISHER = 10;

	private final AiStatistics aiStatistics;
	private final byte playerId;
	private final int searchRadius;

	public PioneerAi(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.searchRadius = aiStatistics.getMainGrid().getWidth() / 2;
	}

	public ShortPoint2D findResourceTarget() {
		// TODO: ressourcen pios gucken von ihrer aktuellen position aus, nicht mehr vom center. Bekämpft schneeberg auf mountain lake
		// TODO: verbreitungspios merken sich letztes target und erst wenn dadrum herum gut eingenommen wurde, gibt es ein neues

		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		ShortPoint2D myCenter = aiStatistics.getPositionOfPartition(playerId);
		int maxDistance = halfDistanceToNearestEnemy(myCenter);

		ShortPoint2D target = targetForCuttingBuilding(myBorder, myCenter, EMapObjectType.TREE_ADULT,
				EBuildingType.LUMBERJACK, aiStatistics.getTreesForPlayer(playerId), 6, maxDistance);
		if (target != null)
			return target;

		target = targetForCuttingBuilding(myBorder, myCenter, EMapObjectType.STONE, EBuildingType.STONECUTTER,
				aiStatistics.getStonesForPlayer(playerId), 4, maxDistance);
		if (target != null)
			return target;

		target = targetForNearStoneFields(myBorder);
		if (target != null)
			return target;

		target = targetForMine(myBorder, myCenter, EResourceType.COAL, EBuildingType.COALMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(myBorder, myCenter, EResourceType.IRONORE, EBuildingType.IRONMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForRivers(myBorder, myCenter, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(myBorder, myCenter, EResourceType.GOLDORE, EBuildingType.GOLDMINE, maxDistance);
		if (target != null)
			return target;

		return targetForFish(myBorder, myCenter, maxDistance);
	}

	private ShortPoint2D targetForNearStoneFields(AiPositions myBorder) {
		for (ShortPoint2D stonePosition : aiStatistics.getStonesInDefaultPosition()) {
			ShortPoint2D target = myBorder.getNearestPoint(stonePosition, 5);
			if (target != null) {
				return target;
			}
		}
		return null;
	}

	private int halfDistanceToNearestEnemy(ShortPoint2D myCenter) {
		int distance = Integer.MAX_VALUE;
		for (byte enemyId : aiStatistics.getAliveEnemiesOf(playerId)) {
			int enemyDistance = myCenter.getOnGridDistTo(aiStatistics.getPositionOfPartition(enemyId));
			if (enemyDistance < distance) {
				distance = enemyDistance;
			}
		}
		return (int) Math.ceil(distance / 1.9F);
	}

	private ShortPoint2D targetForCuttingBuilding(AiPositions myBorder, ShortPoint2D myCenter,
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

		return myBorder.getNearestPoint(nearestCuttableObject, CommonConstants.TOWER_RADIUS);
	}

	private ShortPoint2D targetForRivers(AiPositions myBorder, ShortPoint2D myCenter,
			int maxDistance) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.WATERWORKS, playerId) + 1;
		if (aiStatistics.getRiversForPlayer(playerId).size() > buildingCount * 5)
			return null;

		ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(myCenter, maxDistance);
		if (nearestRiver == null)
			return null;

		return myBorder.getNearestPoint(nearestRiver, searchRadius);
	}

	private ShortPoint2D targetForMine(AiPositions myBorder, ShortPoint2D myCenter,
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

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	private ShortPoint2D targetForFish(AiPositions myBorder, ShortPoint2D myCenter, int maxDistance) {
		if (aiStatistics.resourceCountInDefaultPartition(EResourceType.FISH) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FISHER, playerId) + 1;

		if (aiStatistics.resourceCountOfPlayer(EResourceType.FISH, playerId) > FISH_NEEDED_BY_FISHER * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestFishPointForPlayer(myCenter, playerId, maxDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	public ShortPoint2D findBroadenTarget() {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		return myBorder.getNearestPoint(getCentroidOf(), searchRadius);
	}

	private ShortPoint2D getCentroidOf() {
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
