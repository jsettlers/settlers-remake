package jsettlers.logic.map.random.voronoi;

public interface VoronoiEvent {
	/**
	 * Returns wheter this is just a regular point or a circle event.
	 * 
	 * @return true for point (site), false for circle
	 */
	boolean isVoronoiSite();

	public double getX();
}
