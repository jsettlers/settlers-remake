package jsettlers.ai.highlevel;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.player.Player;

public class WhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	
	public WhatToDoAi(byte playerId, MainGrid mainGrid) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
	}
	
	private boolean buildingsAreConstructed = false;
	
	@Override
	public void applyRules() {
		if (!buildingsAreConstructed) {
			BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
			ShortPoint2D position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(EBuildingType.STONECUTTER).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
			if (position != null) {
			 mainGrid.getGuiInputGrid().constructBuildingAt(position, EBuildingType.STONECUTTER, playerId);
			}
			 position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(EBuildingType.LUMBERJACK).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
			if (position != null) {
				 mainGrid.getGuiInputGrid().constructBuildingAt(position, EBuildingType.LUMBERJACK, playerId);
				}
			 position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(EBuildingType.SAWMILL).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
			if (position != null) {
				 mainGrid.getGuiInputGrid().constructBuildingAt(position, EBuildingType.SAWMILL, playerId);
				}
			 position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(EBuildingType.FORESTER).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
			if (position != null) {
				 mainGrid.getGuiInputGrid().constructBuildingAt(position, EBuildingType.FORESTER, playerId);
				}
			 position = bestConstructionPositionFinderFactory.getBestConstructionPositionFinderFor(EBuildingType.MEDIUM_LIVINGHOUSE).findBestConstructionPosition(mainGrid.getConstructionMarksGrid(), playerId);
			if (position != null) {
				 mainGrid.getGuiInputGrid().constructBuildingAt(position, EBuildingType.MEDIUM_LIVINGHOUSE, playerId);
				}
		}
		buildingsAreConstructed = true;
	}

}
