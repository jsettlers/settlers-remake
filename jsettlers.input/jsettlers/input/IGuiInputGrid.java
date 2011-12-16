package jsettlers.input;

import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.newGrid.movable.IHexMovable;

/**
 * This interface defines the methods needed by the GUI to interact with the grid.
 * 
 * @author Andreas Eberle
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

	IHexMovable getMovable(short x, short y);

	/**
	 * Gets a position where the building can be constructed some points around pos.
	 * 
	 * @param pos
	 *            THe position
	 * @param type
	 *            The type of the building
	 * @return <code>null</code> if no position was found, the position otherwise.
	 */
	ISPosition2D getConstructablePositionAround(ISPosition2D pos, EBuildingType type);

	void save() throws FileNotFoundException, IOException, InterruptedException;
}
