package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.random.grid.MapGrid;

public abstract class TileMatcher implements Iterable<ISPosition2D> {
	protected final MapGrid grid;
	protected final int startx;
	protected final int starty;
	protected final int distance;
	protected final Random random;
	private final LandFilter filter;

	public TileMatcher(MapGrid grid, int startx, int starty, int distance,
	        LandFilter filter, Random random) {
		this.grid = grid;
		this.startx = startx;
		this.starty = starty;
		this.distance = distance;
		this.filter = filter;
		this.random = random;

	}

	protected boolean isPlaceable(ISPosition2D point) {
		return filter.isPlaceable(point);
	}

}
