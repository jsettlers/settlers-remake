/*******************************************************************************
 * Copyright (c) 2016 - 2017
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

import static java8.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.network.client.interfaces.ITaskScheduler;

import java8.util.stream.Collectors;

/**
 * @author codingberlin
 */
public class PioneerGroup {

	private final int targetSize;
	private final List<Integer> pioneerIds;

	private PioneerGroup(List<Integer> pioneerIds, int targetSize) {
		this.targetSize = targetSize;
		this.pioneerIds = new ArrayList<>(pioneerIds);
	}

	public PioneerGroup(int targetSize) {
		this(new ArrayList<>(targetSize), targetSize);
	}

	public PioneerGroup(List<Integer> pioneerIds) {
		this(pioneerIds, pioneerIds.size());
	}

	public void clear() {
		pioneerIds.clear();
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

	public void fill(ITaskScheduler taskScheduler, AiStatistics aiStatistics, byte playerId, int maxNewPioneersCount) {
		if (isFull()) {
			return;
		}

		MovableGrid movableGrid = aiStatistics.getMainGrid().getMovableGrid();
		List<ShortPoint2D> joblessBearers = aiStatistics.getPositionsOfJoblessBearersForPlayer(playerId);

		int newPioneers = Math.min(getMissingPioneers(), maxNewPioneersCount);

		List<Integer> newPioneerIds = stream(joblessBearers)
				.limit(newPioneers)
				.map(position -> movableGrid.getMovableAt(position.x, position.y))
				.map(ILogicMovable::getID)
				.collect(Collectors.toList());

		if (newPioneerIds.size() > 0) {
			taskScheduler.scheduleTask(new ConvertGuiTask(playerId, newPioneerIds, EMovableType.PIONEER));
			pioneerIds.addAll(newPioneerIds);
		}
	}

	public PioneerGroup getPioneersWithNoAction() {
		List<Integer> pioneersWithNoAction = stream(pioneerIds).filter(pioneerId -> Movable.getMovableByID(pioneerId).getAction() == EMovableAction.NO_ACTION).collect(Collectors.toList());
		return new PioneerGroup(pioneersWithNoAction);
	}

	public void addAll(List<Integer> pioneerIds) {
		this.pioneerIds.addAll(pioneerIds);
	}

	public List<Integer> getPioneerIds() {
		return pioneerIds;
	}

	public int getMissingPioneers() {
		return Math.max(0, targetSize - pioneerIds.size());
	}

	public boolean isFull() {
		return pioneerIds.size() >= targetSize;
	}

	public boolean isNotEmpty() {
		return pioneerIds.size() > 0;
	}
}
