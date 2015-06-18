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

import static jsettlers.common.buildings.EBuildingType.BIG_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.FORESTER;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.MEDIUM_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.SAWMILL;
import static jsettlers.common.buildings.EBuildingType.SMALL_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.STONECUTTER;
import static jsettlers.common.buildings.EBuildingType.TOWER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.movable.EMovableType;
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
	}

	@Override
	public void applyRules() {
		int numberOfNotFinishedBuildings = aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId);

		Map<EBuildingType, Integer> numberOf = new HashMap<EBuildingType, Integer>();
		numberOf.put(SMALL_LIVINGHOUSE, aiStatistics.getNumberOfBuildingTypeForPlayer(SMALL_LIVINGHOUSE, playerId));
		numberOf.put(MEDIUM_LIVINGHOUSE, aiStatistics.getNumberOfBuildingTypeForPlayer(MEDIUM_LIVINGHOUSE, playerId));
		numberOf.put(BIG_LIVINGHOUSE, aiStatistics.getNumberOfBuildingTypeForPlayer(BIG_LIVINGHOUSE, playerId));
		Map<EBuildingType, Integer> totalNumberOf = new HashMap<EBuildingType, Integer>();
		totalNumberOf.put(STONECUTTER, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId));
		totalNumberOf.put(LUMBERJACK, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId));
		totalNumberOf.put(TOWER, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(TOWER, playerId));
		totalNumberOf.put(SAWMILL, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(SAWMILL, playerId));
		totalNumberOf.put(FORESTER, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(FORESTER, playerId));
		totalNumberOf.put(SMALL_LIVINGHOUSE, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(SMALL_LIVINGHOUSE, playerId));
		totalNumberOf.put(MEDIUM_LIVINGHOUSE, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(MEDIUM_LIVINGHOUSE, playerId));
		totalNumberOf.put(BIG_LIVINGHOUSE, aiStatistics.getTotalNumberOfBuildingTypeForPlayer(BIG_LIVINGHOUSE, playerId));
		int numberOfNotOccupiedTowers = aiStatistics.getNumberOfNotOccupiedTowers(playerId);
		int numberOfBearers = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size();
		int numberOfTotalBuildings = aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId);

		boolean iCanBuild = numberOfNotFinishedBuildings <= 4;

		int futureNumberOfBearers = numberOfBearers
				+ totalNumberOf.get(SMALL_LIVINGHOUSE)
				- numberOf.get(SMALL_LIVINGHOUSE)
				+ totalNumberOf.get(MEDIUM_LIVINGHOUSE)
				- numberOf.get(MEDIUM_LIVINGHOUSE)
				+ totalNumberOf.get(BIG_LIVINGHOUSE)
				- numberOf.get(BIG_LIVINGHOUSE);
		if (iCanBuild && (futureNumberOfBearers < 10 || numberOfTotalBuildings > 1.5 * futureNumberOfBearers)) {
			if (totalNumberOf.get(STONECUTTER) < 4 || totalNumberOf.get(SAWMILL) < 3) {
				construct(SMALL_LIVINGHOUSE);
			} else {
				construct(MEDIUM_LIVINGHOUSE);
			}
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 0) {
			construct(STONECUTTER);
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 1) {
			construct(STONECUTTER);
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 2 && totalNumberOf.get(LUMBERJACK) < 1) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 1 && totalNumberOf.get(LUMBERJACK) < 2) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) >= 2 && numberOfNotOccupiedTowers == 0) {
			construct(TOWER);
		}
		if (iCanBuild && totalNumberOf.get(TOWER) == 1 && totalNumberOf.get(SAWMILL) < 1) {
			construct(SAWMILL);
		}
		if (iCanBuild && totalNumberOf.get(SAWMILL) == 1 && totalNumberOf.get(LUMBERJACK) < 3) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 3 && totalNumberOf.get(FORESTER) < 1) {
			construct(FORESTER);
		}
		if (iCanBuild && totalNumberOf.get(FORESTER) == 1 && totalNumberOf.get(LUMBERJACK) < 4) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 4 && totalNumberOf.get(LUMBERJACK) < 5) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 5 && totalNumberOf.get(SAWMILL) < 2) {
			construct(SAWMILL);
		}
		if (iCanBuild && totalNumberOf.get(SAWMILL) == 2 && totalNumberOf.get(FORESTER) < 2) {
			construct(FORESTER);
		}
		if (iCanBuild && totalNumberOf.get(FORESTER) == 2 && totalNumberOf.get(LUMBERJACK) < 6) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 6 && totalNumberOf.get(LUMBERJACK) < 7) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 7 && totalNumberOf.get(LUMBERJACK) < 8) {
			construct(LUMBERJACK);
		}
		if (iCanBuild && totalNumberOf.get(LUMBERJACK) == 8 && totalNumberOf.get(SAWMILL) < 3) {
			construct(SAWMILL);
		}
		if (iCanBuild && totalNumberOf.get(SAWMILL) == 3 && totalNumberOf.get(FORESTER) < 3) {
			construct(FORESTER);
		}
		if (iCanBuild && totalNumberOf.get(FORESTER) == 3 && totalNumberOf.get(FORESTER) < 4) {
			construct(FORESTER);
		}
		if (iCanBuild && totalNumberOf.get(FORESTER) == 4 && totalNumberOf.get(STONECUTTER) < 3) {
			construct(STONECUTTER);
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 3 && totalNumberOf.get(STONECUTTER) < 4) {
			construct(STONECUTTER);
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 4 && totalNumberOf.get(STONECUTTER) < 5) {
			construct(STONECUTTER);
		}
		if (iCanBuild && totalNumberOf.get(STONECUTTER) == 5 && totalNumberOf.get(MEDIUM_LIVINGHOUSE) < 2) {
			construct(MEDIUM_LIVINGHOUSE);
		}
		if (iCanBuild && totalNumberOf.get(MEDIUM_LIVINGHOUSE) == 2 && totalNumberOf.get(MEDIUM_LIVINGHOUSE) < 3) {
			construct(MEDIUM_LIVINGHOUSE);
		}
	}

	private void construct(EBuildingType type) {
		ShortPoint2D position = bestConstructionPositionFinderFactory
				.getBestConstructionPositionFinderFor(type)
				.findBestConstructionPosition(aiStatistics, mainGrid.getConstructionMarksGrid(), playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, type));
		}
	}
}
