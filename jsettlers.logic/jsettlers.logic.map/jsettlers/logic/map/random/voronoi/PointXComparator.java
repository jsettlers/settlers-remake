package jsettlers.logic.map.random.voronoi;

import java.util.Comparator;

public class PointXComparator implements Comparator<VoronoiEvent> {

	@Override
    public int compare(VoronoiEvent o1, VoronoiEvent o2) {
		//TODO: use epsilon, use y
	    return Double.compare(o1.getX(), o2.getX());
    }
	
}
