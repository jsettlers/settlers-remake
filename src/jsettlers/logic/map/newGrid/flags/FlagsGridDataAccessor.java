package jsettlers.logic.map.newGrid.flags;

import java.util.BitSet;

/**
 * FOR TESTS ONLY! <br>
 * This class can be used to access fields of the flags grid for tests.
 * 
 * @author Andreas Eberle
 * 
 */
public class FlagsGridDataAccessor {
	private FlagsGrid grid;

	public FlagsGridDataAccessor(FlagsGrid grid) {
		this.grid = grid;
	}

	public BitSet getBlockedGrid() {
		return grid.getBlockedGrid();
	}
}
