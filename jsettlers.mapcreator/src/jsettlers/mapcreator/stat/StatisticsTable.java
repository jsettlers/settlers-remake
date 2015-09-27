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
package jsettlers.mapcreator.stat;

import javax.swing.table.AbstractTableModel;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.data.MapData;

public class StatisticsTable extends AbstractTableModel {
	private static final long serialVersionUID = -3492356821401542476L;

	private static final int MATERIAL_START = 0;
	private static final int MOVABLE_START = MATERIAL_START + EMaterialType.NUMBER_OF_MATERIALS;
	private static final int BUILDING_START = MOVABLE_START + EMovableType.NUMBER_OF_MOVABLETYPES;
	private static final int END_POS = BUILDING_START + EBuildingType.NUMBER_OF_BUILDINGS;
	private final int playercount;
	private final int[][] counts;
	private String[] rowNames;

	public StatisticsTable(MapData data) {
		playercount = data.getPlayerCount();
		int allRows = END_POS;
		int[][] allCounts = new int[allRows][playercount];
		boolean[] used = new boolean[allRows]; // is the row used?

		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				int row = getRow(data.getMapObject(x, y));
				byte player = data.getPlayer(x, y);
				if (row >= 0 && player >= 0) {
					allCounts[row][player]++;
					used[row] = true;
				}
			}
		}

		// filter used rows
		int rows = 0;
		for (int row = 0; row < allRows; row++) {
			if (used[row]) {
				rows++;
			}
		}
		counts = new int[rows][];
		rowNames = new String[rows];

		int realrow = 0;
		for (int row = 0; row < allRows; row++) {
			if (used[row]) {
				counts[realrow] = allCounts[row];
				rowNames[realrow] = getName(row);
				realrow++;
			}
		}
	}

	private static int getRow(MapObject mapObject) {
		if (mapObject instanceof BuildingObject) {
			return BUILDING_START + ((BuildingObject) mapObject).getType().ordinal();
		} else if (mapObject instanceof MovableObject) {
			return MOVABLE_START + ((MovableObject) mapObject).getType().ordinal();
		} else if (mapObject instanceof StackObject) {
			return BUILDING_START + ((StackObject) mapObject).getType().ordinal();
		}

		return -1;
	}

	private static String getName(int row) {
		if (row >= BUILDING_START) {
			return Labels.getName(EBuildingType.values()[row - BUILDING_START]);
		} else if (row >= MOVABLE_START) {
			return Labels.getName(EMovableType.values()[row - MOVABLE_START]);
		} else if (row >= MATERIAL_START) {
			return Labels.getName(EMaterialType.values()[row - MATERIAL_START], true);
		}
		return "";
	}

	@Override
	public int getColumnCount() {
		return playercount + 1;
	}

	@Override
	public int getRowCount() {
		return counts.length;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col > 0) {
			return Integer.toString(counts[row][col - 1]);
		} else {
			return rowNames[row];
		}
	}

	@Override
	public String getColumnName(int col) {
		return col > 0 ? "player " + (col - 1) : "";
	}

}
