package jsettlers.ai.highlevel;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

public class WhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	
	public WhatToDoAi(byte playerId, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = new AiStatistics();
	}
	
	@Override
	public void applyRules() {
		int numberOfNotFinishedBuildings = aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId);
		int numberOfTotalStonecutters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.STONECUTTER, playerId);
		int numberOfTotalLumberJacks = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.LUMBERJACK, playerId);
		int numberOfTotalSawMillss = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.SAWMILL, playerId);
		int numberOfTotalForesters = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(EBuildingType.FORESTER, playerId);
		
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalStonecutters < 1) {
			construct(EBuildingType.STONECUTTER);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalLumberJacks < 1) {
			construct(EBuildingType.LUMBERJACK);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalSawMillss < 1) {
			construct(EBuildingType.SAWMILL);
		}
		if (numberOfNotFinishedBuildings < 5 && numberOfTotalForesters < 1) {
			construct(EBuildingType.FORESTER);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalStonecutters < 3 &&
				numberOfTotalLumberJacks >= 1 &&
				numberOfTotalSawMillss >= 1 &&
				numberOfTotalForesters >= 1
				) {
			construct(EBuildingType.STONECUTTER);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalLumberJacks < 4 &&
				numberOfTotalStonecutters >= 3 &&
				numberOfTotalSawMillss >= 1 &&
				numberOfTotalForesters >= 1
				) {
			construct(EBuildingType.LUMBERJACK);
		}
		if (numberOfNotFinishedBuildings < 5 &&
				numberOfTotalLumberJacks >= 4 &&
				numberOfTotalStonecutters >= 3 &&
				numberOfTotalSawMillss < 2 &&
				numberOfTotalForesters >= 1
				) {
			construct(EBuildingType.SAWMILL);
		}
	}
	
	private void construct(EBuildingType type) {
		BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		ShortPoint2D position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(type).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), mainGrid.getPartitionsGrid(), mainGrid.getObjectsGrid(),playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, type));
		}
	}

}
