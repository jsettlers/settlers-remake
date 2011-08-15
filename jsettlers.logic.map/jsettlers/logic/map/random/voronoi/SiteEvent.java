package jsettlers.logic.map.random.voronoi;


/**
 * This is a site event. It occurs for every voroni site.
 * @author michael
 *
 */
public class SiteEvent implements VoronoiEvent {
	private final VoronioSite site;

	public SiteEvent(VoronioSite site) {
		this.site = site;
	}

	@Override
    public double getX() {
	    return site.getX();
    }

	@Override
    public boolean isVoronoiSite() {
	    return true;
    }

	public double getY() {
	    return site.getY();
    }

	public VoronioSite getSite() {
		return site;
    }

	@Override
	public boolean equals(Object obj) {
	    // TODO Auto-generated method stub
	    return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
	    // TODO Auto-generated method stub
	    return super.hashCode();
	}

}
