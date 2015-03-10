package jsettlers.logic.map.random.voronoi;

/**
 * This is a part of the beach line of the algorithm.
 * <p>
 * It is a leaf of the beach tree.
 * 
 * @author michael
 */
public class BeachLinePart implements BeachTreeItem {
	private BeachSeparator parent;
	private final VoronioSite site;

	public BeachLinePart(VoronioSite site) {
		this.site = site;
	}

	@Override
	public BeachTreeItem getTopChild() {
		return null;
	}

	@Override
	public BeachSeparator getParent() {
		return parent;
	}

	@Override
	public BeachTreeItem getBottomChild() {
		return null;
	}

	public double getX() {
		return site.getX();
	}

	public double getY() {
		return site.getY();
	}

	public BeachLinePart copy() {
		return new BeachLinePart(site);
	}

}
