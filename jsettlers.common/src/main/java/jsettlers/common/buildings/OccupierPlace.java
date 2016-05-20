/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.common.buildings;

import java.io.Serializable;

import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.RelativePoint;

/**
 * A place an occuping person can be in a building.
 * 
 * @author michael
 */
public class OccupierPlace implements Serializable {
	private static final long serialVersionUID = 1355922428788608890L;

	private final ESoldierClass soldierClass;
	private final int offsetY;
	private final int offsetX;
	private final RelativePoint position;

	private final boolean looksRight;

	public OccupierPlace(int offsetX, int offsetY, ESoldierClass soldierClass, RelativePoint position, boolean looksRight) {
		if (position == null || soldierClass == null) {
			throw new NullPointerException();
		}
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.soldierClass = soldierClass;
		this.position = position;
		this.looksRight = looksRight;
	}

	/**
	 * gets the type of the occupyer.
	 * 
	 * @return {@link ESoldierClass#INFANTRY} if it is a person that is inside, {@link ESoldierClass#BOWMAN} if it is a bowman on the roof.
	 */
	public final ESoldierClass getSoldierClass() {
		return soldierClass;
	}

	/**
	 * Gets the x coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public final int getOffsetX() {
		return offsetX;
	}

	/**
	 * Gets the y coordinate (in pixels) of the settler.
	 * 
	 * @return
	 */
	public final int getOffsetY() {
		return offsetY;
	}

	/**
	 * Whether the solier should look to the right.
	 * 
	 * @return A boolean value.
	 */
	public final boolean looksRight() {
		return looksRight;
	}

	/**
	 * Gets the point over which the soldier is standing.
	 * 
	 * @return The position relative to the building.
	 */
	public final RelativePoint getPosition() {
		return position;
	}
}
