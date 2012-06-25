package jsettlers.logic.algorithms.path.astar.supergrid;

/**
 * Data object for costs of a position calculated by {@link DijkstraForSupergrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PositionCosts {

	private final float[] costs;

	public PositionCosts(float[] costs) {
		this.costs = costs;
	}

	public float[] getCosts() {
		return costs;
	}
}
