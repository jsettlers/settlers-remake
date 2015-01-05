package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.position.ShortPoint2D;

public abstract class TileMatcher implements Iterable<ShortPoint2D> {
	protected final IMapData grid;
	protected final int startx;
	protected final int starty;
	protected final int distance;
	protected final Random random;
	private final LandFilter filter;

	public TileMatcher(IMapData grid, int startx, int starty, int distance,
			LandFilter filter, Random random) {
		this.grid = grid;
		this.startx = startx;
		this.starty = starty;
		this.distance = distance;
		this.filter = filter;
		this.random = random;

	}

	protected boolean isPlaceable(ShortPoint2D point) {
		return filter.isPlaceable(point);
	}

}
