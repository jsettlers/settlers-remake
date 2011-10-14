package jsettlers.input;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;

public interface IInputGrid {

	IHexMovable getMovable(ISPosition2D curr);

	short getWidth();

	short getHeight();

	IBuilding getBuildingAt(ISPosition2D curr);

	boolean isInBounds(ISPosition2D pos);

	IBuildingsGrid getBuildingsGrid();

	byte getPlayerAt(ISPosition2D position);

}
