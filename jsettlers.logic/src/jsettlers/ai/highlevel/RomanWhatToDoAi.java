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

import static jsettlers.common.buildings.EBuildingType.FORESTER;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.MEDIUM_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.SAWMILL;
import static jsettlers.common.buildings.EBuildingType.STONECUTTER;
import static jsettlers.common.buildings.EBuildingType.TOWER;

import java.util.ArrayList;
import java.util.List;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

public class RomanWhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;
	List<EBuildingType> buildingMaterialEconomy;
	int nextBuilding;

	public RomanWhatToDoAi(byte playerId, AiStatistics aiStatistics, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		buildingMaterialEconomy = new ArrayList<EBuildingType>();
		buildingMaterialEconomy.add(STONECUTTER);
		buildingMaterialEconomy.add(STONECUTTER);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(SAWMILL);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(FORESTER);
		buildingMaterialEconomy.add(MEDIUM_LIVINGHOUSE);
		buildingMaterialEconomy.add(TOWER);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(SAWMILL);
		buildingMaterialEconomy.add(FORESTER);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(LUMBERJACK);
		buildingMaterialEconomy.add(SAWMILL);
		buildingMaterialEconomy.add(FORESTER);
		buildingMaterialEconomy.add(FORESTER);
		buildingMaterialEconomy.add(STONECUTTER);
		buildingMaterialEconomy.add(STONECUTTER);
		buildingMaterialEconomy.add(STONECUTTER);
		buildingMaterialEconomy.add(MEDIUM_LIVINGHOUSE);
		buildingMaterialEconomy.add(MEDIUM_LIVINGHOUSE);
		nextBuilding = 0;
	}

	@Override
	public void applyRules() {
		long startTime = System.currentTimeMillis();
		int numberOfNotFinishedBuildings = aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId);
		int totalNumberOfStoneCutters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId);
		int totalNumberOfLumberJacks = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId);
		int totalNumberOfSawMills = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(SAWMILL, playerId);
		int totalNumberOfForesters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(FORESTER, playerId);
		int totalNumberOfMediumLivingHouse = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(MEDIUM_LIVINGHOUSE, playerId);
		int totalNumberOfTowers = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(TOWER, playerId);
		int numberOfTowers = aiStatistics.getNumberOfBuildingTypeForPlayer(TOWER, playerId);

		if (numberOfNotFinishedBuildings < 5 && nextBuilding < buildingMaterialEconomy.size()) {
			construct(buildingMaterialEconomy.get(nextBuilding));
			nextBuilding++;
		}

		if (nextBuilding == buildingMaterialEconomy.size() && numberOfTowers == totalNumberOfTowers) {
			construct(TOWER);
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
