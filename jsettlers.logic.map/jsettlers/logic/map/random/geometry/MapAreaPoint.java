package jsettlers.logic.map.random.geometry;

import jsettlers.logic.map.random.voronoi.VoronioSite;

/**
 * This is a point of the map that is the representative of a landscape part.
 * 
 * @author michael
 * 
 */
public class MapAreaPoint implements VoronioSite {

	private final int x;
	private final int y;

	public MapAreaPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

}
