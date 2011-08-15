package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.map.random.grid.MapGrid;

public abstract class TileMatcher implements Iterable<ISPosition2D> {
	protected final MapGrid grid;
	protected final int startx;
	protected final int starty;
	protected final ELandscapeType onLandscape;
	protected final int distance;
	protected final Random random;

	public TileMatcher(MapGrid grid, int startx, int starty, int distance,
	        ELandscapeType onLandscape, Random random) {
		this.grid = grid;
		this.startx = startx;
		this.starty = starty;
		this.distance = distance;
		this.onLandscape = onLandscape;
		this.random = random;

	}

	protected boolean isPlaceable(ISPosition2D point) {
		return grid.isObjectPlaceable(point.getX(), point.getY())
		        && (onLandscape == null || onLandscape.equals(grid
		                .getLandscape(point.getX(), point.getY())));
	}

}
