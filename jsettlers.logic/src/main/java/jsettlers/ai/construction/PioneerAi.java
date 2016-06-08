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

	private ShortPoint2D lastResourceTarget;
	private ShortPoint2D lastBroadeningTarget;
	private ShortPoint2D centroid;
	private int ticksUntilBroadenFromCentroid = 0;

	public PioneerAi(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.searchRadius = aiStatistics.getMainGrid().getWidth() / 2;
		this.lastResourceTarget = aiStatistics.getPositionOfPartition(playerId);
		this.lastBroadeningTarget = lastResourceTarget;
	}

	public ShortPoint2D findResourceTarget() {
		ShortPoint2D newTarget = findResourceTargetNearLastTarget();
		if (newTarget != null) {
			lastResourceTarget = newTarget;
		}
		return newTarget;
	}

	private ShortPoint2D findResourceTargetNearLastTarget() {
		// TODO sicherstellen, dass kein Feindesland eingenommen wird
		// TODO wenn turmbau dann richtung feinde (in der naehe) ansonsten richtung wald\Stein in der naehe
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		int maxDistance = halfDistanceToNearestEnemy(centroid);

		ShortPoint2D target = targetForCuttingBuilding(myBorder, EMapObjectType.TREE_ADULT,
				EBuildingType.LUMBERJACK, aiStatistics.getTreesForPlayer(playerId), 6, maxDistance);
		if (target != null)
			return target;

		target = targetForCuttingBuilding(myBorder, EMapObjectType.STONE, EBuildingType.STONECUTTER,
				aiStatistics.getStonesForPlayer(playerId), 4, maxDistance);
		if (target != null)
			return target;

		target = targetForOtherPartition(myBorder);
		if (target != null)
			return target;

		target = targetForNearStoneFields(myBorder);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.COAL, EBuildingType.COALMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.IRONORE, EBuildingType.IRONMINE, maxDistance);
		if (target != null)
			return target;

		target = targetForRivers(myBorder, maxDistance);
		if (target != null)
			return target;

		target = targetForMine(myBorder, EResourceType.GOLDORE, EBuildingType.GOLDMINE, maxDistance);
		if (target != null)
			return target;

		return targetForFish(myBorder, maxDistance);
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

	private ShortPoint2D targetForCuttingBuilding(AiPositions myBorder, EMapObjectType cuttableObjectType, EBuildingType cuttingBuildingType,
			AiPositions cuttableObjectsOfPlayer, int factor, int maxDistance) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(cuttingBuildingType, playerId) + 1;
		if (cuttableObjectsOfPlayer.size() > buildingCount * factor)
			return null;

		List<ShortPoint2D> cuttingBuildings = aiStatistics.getBuildingPositionsOfTypeForPlayer(cuttingBuildingType, playerId);
		ShortPoint2D referencePoint = cuttingBuildings.size() > 0 ? cuttingBuildings.get(0) : lastResourceTarget;
		ShortPoint2D nearestCuttableObject = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(referencePoint, cuttableObjectType,
				maxDistance);
		if (nearestCuttableObject == null)
			return null;

		return myBorder.getNearestPoint(nearestCuttableObject, CommonConstants.TOWER_RADIUS);
	}

	private ShortPoint2D targetForRivers(AiPositions myBorder, int maxDistance) {
		int buildingCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.WATERWORKS, playerId) + 1;
		if (aiStatistics.getRiversForPlayer(playerId).size() > buildingCount * 5)
			return null;

		ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(lastResourceTarget, maxDistance);
		if (nearestRiver == null)
			return null;

		return myBorder.getNearestPoint(nearestRiver, searchRadius);
	}

	private ShortPoint2D targetForMine(AiPositions myBorder, EResourceType resourceType, EBuildingType buildingType, int maxDistance) {
		if (aiStatistics.resourceCountInDefaultPartition(resourceType) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) + 1;
		int tiles = buildingType.getProtectedTiles().length * 2;

		if (aiStatistics.resourceCountOfPlayer(resourceType, playerId) > tiles * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestResourcePointInDefaultPartitionFor(lastResourceTarget, resourceType, maxDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	private ShortPoint2D targetForFish(AiPositions myBorder, int maxDistance) {
		if (aiStatistics.resourceCountInDefaultPartition(EResourceType.FISH) == 0)
			return null;

		int factor = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FISHER, playerId) + 1;

		if (aiStatistics.resourceCountOfPlayer(EResourceType.FISH, playerId) > FISH_NEEDED_BY_FISHER * factor)
			return null;

		ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestFishPointForPlayer(lastResourceTarget, playerId, maxDistance);
		if (nearestResourceAbroad == null)
			return null;

		ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad, searchRadius);
		return target;
	}

	public ShortPoint2D findBroadenTarget() {
		ShortPoint2D target = findBroadenTargetNextToLastTarget();
		if (target != null) {
			lastBroadeningTarget = target;
		}
		return target;
	}

	private ShortPoint2D findBroadenTargetNextToLastTarget() {
		ShortPoint2D reference = lastBroadeningTarget;
		if (ticksUntilBroadenFromCentroid == 0) {
			ticksUntilBroadenFromCentroid = 20;
			reference = centroid;
		} else {
			ticksUntilBroadenFromCentroid--;
		}
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		return myBorder.getNearestPoint(reference, searchRadius);
	}

	public void update() {
		AiPositions landForPlayer = aiStatistics.getLandForPlayer(playerId);
		long x = 0;
		long y = 0;
		for (int i = 0; i < landForPlayer.size(); i += 50) {
			ShortPoint2D position = landForPlayer.get(i);
			x += position.x;
			y += position.y;
		}
		int divisor = landForPlayer.size() / 50;
		centroid = new ShortPoint2D((int) (x / divisor), (int) (y / divisor));
	}
}
