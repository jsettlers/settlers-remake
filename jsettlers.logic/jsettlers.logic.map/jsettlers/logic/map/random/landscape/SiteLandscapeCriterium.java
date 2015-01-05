package jsettlers.logic.map.random.landscape;

public class SiteLandscapeCriterium implements SiteCriterium {
	private final MeshLandscapeType type;

	public SiteLandscapeCriterium(MeshLandscapeType type) {
		this.type = type;
	}

	@Override
	public boolean matchesCriterium(MeshSite site) {
		return type.equals(site.getLandscape());
	}
}
