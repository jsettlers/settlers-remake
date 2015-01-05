package jsettlers.logic.map.random.landscape;

public class AndSiteCriterium implements SiteCriterium {

	private final SiteCriterium a;
	private final SiteCriterium b;

	public AndSiteCriterium(SiteCriterium a, SiteCriterium b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean matchesCriterium(MeshSite site) {
		return a.matchesCriterium(site) && b.matchesCriterium(site);
	}

}
