package jsettlers.logic.map.newGrid;

import jsettlers.logic.map.newGrid.flags.FlagsGrid;

public class MainGridDataAccessor {
	private MainGrid grid;

	public MainGridDataAccessor(MainGrid grid) {
		this.grid = grid;
	}

	public short getWidth() {
		return grid.width;
	}

	public short getHeight() {
		return grid.height;
	}

	public FlagsGrid getFlagsGrid() {
		return grid.flagsGrid;
	}
}
