package jsettlers.ai.highlevel;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.SimpleGuiTask;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

public class WhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	
	public WhatToDoAi(byte playerId, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
	}
	
	private boolean buildingsAreConstructed = false;
	
	@Override
	public void applyRules() {
		if (!buildingsAreConstructed) {
			construct(EBuildingType.STONECUTTER);
			construct(EBuildingType.LUMBERJACK);
			construct(EBuildingType.SAWMILL);
			construct(EBuildingType.FORESTER);
			buildingsAreConstructed = true;
		}
	}
	
	private void construct(EBuildingType type) {
		BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		ShortPoint2D position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(type).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, type));
		}
	}

}
