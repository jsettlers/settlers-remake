package jsettlers.input;

import java.io.FileNotFoundException;
import java.io.IOException;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.construction.AbstractConstructionMarkableMap;

/**
 * This interface defines the methods needed by the GUI to interact with the grid.
 * 
 * @author Andreas Eberle
 */
public interface IGuiInputGrid {

	short getWidth();

	short getHeight();

	IBuilding getBuildingAt(short x, short y);

	boolean isInBounds(ShortPoint2D position);

	void resetDebugColors();

	IGuiMovable getMovable(short x, short y);

	/**
	 * Gets a position where the building can be constructed some points around pos.
	 * 
	 * @param position
	 *            THe position
	 * @param type
	 *            The type of the building
	 * @param useNeighborPositionsForConstruction
	 *            If this is true, not only the given position is checked, if it can be used to construct a building, but also the neighbors.<br>
	 *            If this is false, only the given position will be checked.
	 * 
	 * @return <code>null</code> if no position was found, the position otherwise.
	 */
	ShortPoint2D getConstructablePosition(ShortPoint2D position, EBuildingType type, boolean useNeighbors);

	void save() throws FileNotFoundException, IOException, InterruptedException;

	void toggleFogOfWar();

	AbstractConstructionMarkableMap getConstructionMarksGrid();

	/**
	 * Positions a new building of the given type at the given position.
	 * 
	 * @param position
	 *            Position the new building will be placed. <br>
	 *            NOTE: There will be no validation if this position is allowed! This must be done prior to this call.
	 * @param type
	 *            {@link EBuildingType} of the new building.
	 */
	void constructBuildingAt(ShortPoint2D position, EBuildingType type);

	/**
	 * This method can be used to print debug output when the given position is clicked by the user.
	 * 
	 * @param x
	 *            x coordinate of the position.
	 * @param y
	 *            y coordinate of the position.
	 */
	void postionClicked(short x, short y);
}
