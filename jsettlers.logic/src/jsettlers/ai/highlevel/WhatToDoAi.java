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
package jsettlers.ai.highlevel;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

public class WhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;

	public WhatToDoAi(byte playerId, AiStatistics aiStatistics, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
	}

	@Override
	public void applyRules() {
		long startTime = System.currentTimeMillis();
		int numberOfNotFinishedBuildings = aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId);
		int numberOfTotalStonecutters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.STONECUTTER, playerId);
		int numberOfTotalLumberJacks = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.LUMBERJACK, playerId);
		int numberOfTotalSawMills = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.SAWMILL, playerId);
		int numberOfTotalForesters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FORESTER, playerId);

		if (numberOfNotFinishedBuildings < 5 && numberOfTotalStonecutters < 1) {
			construct(EBuildingType.STONECUTTER);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalLumberJacks < 1) {
			construct(EBuildingType.LUMBERJACK);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalSawMills < 1) {
			construct(EBuildingType.SAWMILL);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalForesters < 1) {
			construct(EBuildingType.FORESTER);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalStonecutters < 3 &&
				numberOfTotalLumberJacks >= 1 &&
				numberOfTotalSawMills >= 1 &&
				numberOfTotalForesters >= 1) {
			construct(EBuildingType.STONECUTTER);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalLumberJacks < 4 &&
				numberOfTotalStonecutters >= 3 &&
				numberOfTotalSawMills >= 1 &&
				numberOfTotalForesters >= 1) {
			construct(EBuildingType.LUMBERJACK);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalLumberJacks >= 4 &&
				numberOfTotalStonecutters >= 3 &&
				numberOfTotalSawMills < 2 &&
				numberOfTotalForesters >= 1) {
			construct(EBuildingType.SAWMILL);
		}
		System.out.println("WhatToDoAi took " + (System.currentTimeMillis() - startTime) + " ms");
	}

	private void construct(EBuildingType type) {
		ShortPoint2D position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(type).findBestConstructionPosition(
				aiStatistics, mainGrid.getConstructionMarksGrid(), playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, type));
		}
	}

}
