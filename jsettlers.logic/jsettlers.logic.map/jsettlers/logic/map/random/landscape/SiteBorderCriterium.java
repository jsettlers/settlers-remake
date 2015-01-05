package jsettlers.logic.map.random.landscape;

public class SiteBorderCriterium implements SiteCriterium {

	private final SiteCriterium criterium;

	/**
	 * Creates a new site border criterium
	 * 
	 * @param criterium
	 *            The criterium all of the neighbors (also just edge) must match.
	 */
	public SiteBorderCriterium(SiteCriterium criterium) {
		this.criterium = criterium;
	}

	@Override
	public boolean matchesCriterium(MeshSite site) {
		for (MeshSite neighbour : site.getAllNeighbours()) {
			if (!criterium.matchesCriterium(neighbour)) {
				return false;
			}
		}
		return true;
	}
}
