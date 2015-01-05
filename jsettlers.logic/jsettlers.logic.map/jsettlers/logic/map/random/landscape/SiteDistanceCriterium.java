package jsettlers.logic.map.random.landscape;

import jsettlers.logic.map.random.geometry.Point2D;

public class SiteDistanceCriterium implements SiteCriterium {
	private final int minDistanceSquared;
	private final int maxDistanceSquared;
	private final Point2D center;

	public SiteDistanceCriterium(Point2D point, String distanceRange) {
		this.center = point;
		String[] rangeParts = distanceRange.split("-");
		int mindistance = Integer.parseInt(rangeParts[0]);
		int maxdistance;
		if (rangeParts.length < 2) {
			maxdistance = mindistance;
		} else {
			maxdistance = Integer.parseInt(rangeParts[1]);
		}
		minDistanceSquared = mindistance * mindistance;
		maxDistanceSquared = maxdistance * maxdistance;
	}

	@Override
	public boolean matchesCriterium(MeshSite site) {
		if (site.isFixed()) {
			return false;
		}
		double distanceSquared = site.getCenter().distanceSquared(center);
		return minDistanceSquared < distanceSquared && distanceSquared < maxDistanceSquared;
	}

}
