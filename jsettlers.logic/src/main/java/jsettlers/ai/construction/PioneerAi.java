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
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;

/**
 * @author codingberlin
 */
public class PioneerAi {

	public static ShortPoint2D findTarget(AiStatistics aiStatistics, byte playerId) {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		ShortPoint2D myCenter = aiStatistics.getPositionOfPartition(playerId);
		if (aiStatistics.getTreesForPlayer(playerId).size() < 30) {
			ShortPoint2D nearestTree = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(myCenter, EMapObjectType.TREE_ADULT,
					Integer.MAX_VALUE);
			return myBorder.getNearestPoint(nearestTree);
		} else if (aiStatistics.getStonesForPlayer(playerId).size() < 15) {
			ShortPoint2D nearestStone = aiStatistics.getNearestCuttableObjectPointInDefaultPartitionFor(myCenter, EMapObjectType.STONE,
					Integer.MAX_VALUE);
			return myBorder.getNearestPoint(nearestStone);
		} else if (aiStatistics.getRiversForPlayer(playerId).size() < 15) {
			ShortPoint2D nearestRiver = aiStatistics.getNearestRiverPointInDefaultPartitionFor(myCenter, Integer.MAX_VALUE);
			return myBorder.getNearestPoint(nearestRiver);
		} else {
			EResourceType[] resources = { EResourceType.COAL, EResourceType.IRONORE, EResourceType.FISH, EResourceType.GOLDORE };
			for (EResourceType resourceType : resources) {
				ShortPoint2D nearestResourceAbroad = aiStatistics.getNearestResourcePointInDefaultPartitionFor(myCenter, resourceType,
						Integer.MAX_VALUE);
				if (nearestResourceAbroad == null) {
					continue;
				}
				ShortPoint2D nearestResourceInland = aiStatistics.getNearestResourcePointForPlayer(myCenter, resourceType, playerId,
						Integer.MAX_VALUE);
				if (nearestResourceInland == null || nearestResourceInland.getOnGridDistTo(nearestResourceAbroad) < 3) {
					ShortPoint2D target = myBorder.getNearestPoint(nearestResourceAbroad);
					if (resourceType != EResourceType.FISH || nearestResourceInland.getOnGridDistTo(nearestResourceAbroad) > 1) {
						return target;
					}
				}
			}
			return null;
		}
	}
}
