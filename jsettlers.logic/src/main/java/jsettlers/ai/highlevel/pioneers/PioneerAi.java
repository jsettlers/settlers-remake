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
package jsettlers.ai.highlevel.pioneers;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.ai.highlevel.pioneers.target.ConnectPartitionsTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.FishTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.ITargetFinder;
import jsettlers.ai.highlevel.pioneers.target.LumberJackTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.MineTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.NearStonesTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.RiverTargetFinder;
import jsettlers.ai.highlevel.pioneers.target.StoneCutterTargetFinder;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;

/**
 * @author codingberlin
 */
public class PioneerAi {

	private final AiStatistics aiStatistics;
	private final byte playerId;
	private final int searchDistance;
	private final ITargetFinder[] targetFinders = new ITargetFinder[9];
	private ShortPoint2D lastResourceTarget;

	public PioneerAi(AiStatistics aiStatistics, byte playerId) {
		this.aiStatistics = aiStatistics;
		this.playerId = playerId;
		this.searchDistance = aiStatistics.getMainGrid().getWidth() / 2;
		this.lastResourceTarget = aiStatistics.getPositionOfPartition(playerId);
		targetFinders[0] = new LumberJackTargetFinder(aiStatistics, playerId, searchDistance, 10);
		targetFinders[1] = new NearStonesTargetFinder(aiStatistics, playerId, searchDistance);
		targetFinders[2] = new StoneCutterTargetFinder(aiStatistics, playerId, searchDistance, 6);
		targetFinders[3] = new ConnectPartitionsTargetFinder(aiStatistics, playerId, searchDistance);
		targetFinders[4] = new MineTargetFinder(aiStatistics, playerId, searchDistance, EResourceType.COAL, EBuildingType.COALMINE);
		targetFinders[5] = new MineTargetFinder(aiStatistics, playerId, searchDistance, EResourceType.IRONORE, EBuildingType.IRONMINE);
		targetFinders[6] = new RiverTargetFinder(aiStatistics, playerId, searchDistance);
		targetFinders[7] = new MineTargetFinder(aiStatistics, playerId, searchDistance, EResourceType.GOLDORE, EBuildingType.GOLDMINE);
		targetFinders[8] = new FishTargetFinder(aiStatistics, playerId, searchDistance);
	}

	public ShortPoint2D findResourceTarget() {
		ShortPoint2D newTarget = findResourceTargetNearLastTarget();
		if (newTarget == null) {
			AiPositions border = aiStatistics.getBorderOf(playerId);
			if (border.size() > 1) {
				lastResourceTarget = border.get(MatchConstants.aiRandom().nextInt(border.size()));
			}
		} else {
			lastResourceTarget = newTarget;
		}
		return newTarget;
	}

	private ShortPoint2D findResourceTargetNearLastTarget() {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);

		for (ITargetFinder targetFinder : targetFinders) {
			ShortPoint2D target = targetFinder.findTarget(myBorder, lastResourceTarget);
			if (target != null) {
				return target;
			}
		}

		return null;
	}


	public ShortPoint2D findBroadenTarget() {
		AiPositions myBorder = aiStatistics.getBorderOf(playerId);
		ShortPoint2D target = myBorder.getNearestPoint(centroid(), searchDistance);
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
