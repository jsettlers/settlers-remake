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
package jsettlers.logic.map.grid.partition;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;

import java.io.Serializable;

/**
 * This class holds the data of a tower occupying an area on the {@link PartitionsGrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionOccupyingTower implements Serializable {
	private static final long serialVersionUID = 8080783290542281254L;

	public final byte playerId;
	public final ShortPoint2D position;
	public final FreeMapArea groundArea;
	public final IMapArea area;
	public final SRectangle areaBorders;
	public final int radius;

	public PartitionOccupyingTower(byte playerId, ShortPoint2D position, FreeMapArea groundArea, IMapArea area, SRectangle areaBorders, int radius) {
		this.playerId = playerId;
		this.position = position;
		this.groundArea = groundArea;
		this.area = area;
		this.areaBorders = areaBorders;
		this.radius = radius;
	}

	/**
	 * Creates a new {@link PartitionOccupyingTower} object with the same data as the given tower but the newPlayerId as playerId.
	 * 
	 * @param newPlayerId
	 * @param tower
	 */
	public PartitionOccupyingTower(byte newPlayerId, PartitionOccupyingTower tower) {
		this(newPlayerId, tower.position, tower.groundArea, tower.area, tower.areaBorders, tower.radius);
	}

	public CoordinateStream getAreaWithoutGround() {
		return area.stream().filter((x, y) -> !groundArea.contains(x, y));
	}
}
