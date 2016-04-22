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
package jsettlers.graphics.map.minimap;

/**
 * This is the current mode the minimap should have.
 * 
 * @author Michael Zangl
 */
public class MinimapMode {
	public enum OccupiedAreaMode {
		NONE,
		BORDERS,
		AREA
	}

	public enum SettlersMode {
		NONE,
		SOILDERS,
		ALL
	}

	private boolean displayBuildings = true;
	private OccupiedAreaMode displayOccupied = OccupiedAreaMode.BORDERS;
	private SettlersMode displaySettlers = SettlersMode.SOILDERS;

	public boolean getDisplayBuildings() {
		return displayBuildings;
	}

	public void setDisplayBuildings(boolean displayBuildings) {
		this.displayBuildings = displayBuildings;
	}

	public OccupiedAreaMode getDisplayOccupied() {
		return displayOccupied;
	}

	public void setDisplayOccupied(OccupiedAreaMode displayOccupied) {
		this.displayOccupied = displayOccupied;
	}

	public SettlersMode getDisplaySettlers() {
		return displaySettlers;
	}

	public void setDisplaySettlers(SettlersMode displaySettlers) {
		this.displaySettlers = displaySettlers;
	}
}
