/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.algorithms.construction;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.RelativePoint;

import java.util.Set;

/**
 * Interface offering the methods needed by {@link ConstructionMarksThread}.
 * 
 * @author Andreas Eberle
 */
public abstract class AbstractConstructionMarkableMap {

	/**
	 * Sets or removes a construction mark
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param set
	 *            If true, the construction mark shall be set, otherwise, it shall be removed.
	 * @param binaryConstructionMarkValues
	 *            If true, the construction marks can only have to values: Either building is possible (set == <code>true</code>) or building is not
	 *            possible (set == <code>false</code>).
	 * @param flattenPositions
	 *            The positions that need to be flattened to position this building. This value might be null whenever set is false.
	 */
	public abstract void setConstructMarking(int x, int y, boolean set, boolean binaryConstructionMarkValues, RelativePoint[] flattenPositions);

	/**
	 * @return width of map.
	 */
	public abstract short getWidth();

	/**
	 * @return height of map
	 */
	public abstract short getHeight();

	/**
	 * Checks if the given position is valid to build a building of given player that can stand on the given {@link ELandscapeType}s. Bounds checks
	 * will be done by this method.
	 * 
	 * @param x
	 *            x coordinate of the target position
	 * @param y
	 *            y coordinate of the target position
	 * @param landscapeTypes
	 *            allowed landscape types
	 * @param partitionId
	 *            player
	 * @return true if a building can be positioned at the given position<br>
	 *         false otherwise.
	 */
	public abstract boolean canUsePositionForConstruction(int x, int y, Set<ELandscapeType> landscapeTypes, short partitionId);

	public abstract short getPartitionIdAt(int x, int y);

	public abstract boolean canPlayerConstructOnPartition(byte playerId, short partitionId);

	public abstract boolean isInBounds(int x, int y);

	public abstract boolean canConstructAt(int x, int y, EBuildingType type, byte playerId);

	public abstract byte calculateConstructionMarkValue(int mapX, int mapY, final RelativePoint[] flattenPositions);
}
