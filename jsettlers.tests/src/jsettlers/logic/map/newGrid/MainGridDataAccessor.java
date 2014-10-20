package jsettlers.logic.map.newGrid;

import jsettlers.logic.map.newGrid.flags.FlagsGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;

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

	public LandscapeGrid getLandscapeGrid() {
		return grid.landscapeGrid;
	}

	public PartitionsGrid getPartitionsGrid() {
		return grid.partitionsGrid;
	}
}
