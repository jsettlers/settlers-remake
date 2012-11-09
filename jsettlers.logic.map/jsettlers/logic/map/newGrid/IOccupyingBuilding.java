package jsettlers.logic.map.newGrid;

import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ILocatable;

public interface IOccupyingBuilding extends ILocatable {

	IMapArea getOccupyablePositions();

	byte getPlayerId();

}
