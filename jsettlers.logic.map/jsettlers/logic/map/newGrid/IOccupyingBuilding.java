package jsettlers.logic.map.newGrid;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ILocatable;
import jsettlers.logic.player.Player;

public interface IOccupyingBuilding extends ILocatable {

	IMapArea getOccupyablePositions();

	Player getPlayer();

}
