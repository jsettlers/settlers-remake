package jsettlers.input;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;

/**
 * This interface defines the methods needed by the GUI to interact with the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGuiInputGrid {

	short getWidth();

	short getHeight();

	IBuilding getBuildingAt(short x, short y);

	boolean isInBounds(ISPosition2D position);

	IBuildingsGrid getBuildingsGrid();

	byte getPlayerAt(ISPosition2D position);

	void setBuildingType(EBuildingType buildingType);

	void setScreen(IMapArea screenArea);

	void resetDebugColors();

	boolean canConstructAt(ISPosition2D pos, EBuildingType type);

	IHexMovable getMovable(short x, short y);
}
