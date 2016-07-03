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

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.network.client.interfaces.ITaskScheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author codingberlin
 */
public class PioneerGroup {

	private int targetSize;
	private List<Integer> pioneerIds;

	private PioneerGroup(List<Integer> pioneerIds, int targetSize) {
		this.targetSize = targetSize;
		this.pioneerIds = pioneerIds;
	}

	public PioneerGroup(int targetSize) {
		this(new ArrayList<Integer>(targetSize), targetSize);
	}

	public PioneerGroup(List<Integer> pioneerIds) {
		this(pioneerIds, pioneerIds.size());
	}

	public void removeDeadPioneers() {
		Collection<Integer> idsToRemove = new ArrayList<>(pioneerIds.size());
		for (Integer pioneerId : pioneerIds) {
			if (Movable.getMovableByID(pioneerId) == null) {
				idsToRemove.add(pioneerId);
			}
		}
		pioneerIds.removeAll(idsToRemove);
	}

	public boolean isFull() {
		return pioneerIds.size() - targetSize == 0;
	}

	public void fill(ITaskScheduler taskScheduler, AiStatistics aiStatistics, byte playerId, int maxNewPioneersCount) {
		if (isFull()) {
			return;
		}

		List<Integer> newPioneerIds = new ArrayList<>(targetSize - pioneerIds.size());
		MovableGrid movableGrid = aiStatistics.getMainGrid().getMovableGrid();
		List<ShortPoint2D> bearers = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId);
		for (ShortPoint2D bearerPosition : bearers) {
			if (newPioneerIds.size() == maxNewPioneersCount || isFull()) {
				break;
			}
			Movable bearer = movableGrid.getMovableAt(bearerPosition.x, bearerPosition.y);
			if (bearer.getAction() == EMovableAction.NO_ACTION) {
				newPioneerIds.add(bearer.getID());
				pioneerIds.add(bearer.getID());
			}
		}

		if (newPioneerIds.size() > 0) {
			taskScheduler.scheduleTask(new ConvertGuiTask(playerId, newPioneerIds, EMovableType.PIONEER));
		}
	}

	public PioneerGroup getPioneersWithNoAction() {
		List<Integer> pioneerIdsWithNoAction = new ArrayList<>(pioneerIds.size());
		for (Integer pioneerId : pioneerIds) {
			if (Movable.getMovableByID(pioneerId).getAction() == EMovableAction.NO_ACTION) {
				pioneerIdsWithNoAction.add(pioneerId);
			}
		}

		return new PioneerGroup(pioneerIdsWithNoAction);
	}

	public List<Integer> getPioneerIds() {
		return pioneerIds;
	}

	public boolean isNotEmpty() {
		return pioneerIds.size() > 0;
	}
}
