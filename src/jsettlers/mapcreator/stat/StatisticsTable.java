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
	private static final int MOVABLE_START = MATERIAL_START + EMaterialType.values().length;
	private static final int BUILDING_START = MOVABLE_START + EMovableType.values().length;
	private static final int END_POS = BUILDING_START + EBuildingType.values().length;
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
