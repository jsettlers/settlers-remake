package jsettlers.logic.map.random.voronoi;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import jsettlers.logic.map.random.geometry.MapAreaPoint;
import jsettlers.logic.map.random.geometry.MapMeshEdge;
import jsettlers.logic.map.random.geometry.Mesh;
import jsettlers.logic.map.random.geometry.Point2D;

/**
 * This creates a voronoi graph out of some points.
 * 
 * @author michael
 */
public class Voronoi {

	public static Mesh createGraph(MapAreaPoint[] points) {
		final PriorityQueue<VoronoiEvent> events = new PriorityQueue<VoronoiEvent>(points.length, new PointXComparator());
		@SuppressWarnings("unused")
		final List<MapMeshEdge> edges = new LinkedList<MapMeshEdge>();
		final Beach beach = new BeachTree();
		final Mesh mesh = new Mesh();

		CircleEventManager cem = new CircleEventManager() {
			@Override
			public void remove(CircleEvent falseCircle) {
				events.remove(falseCircle);
			}

			@Override
			public void add(CircleEvent circle) {
				events.add(circle);
			}
		};

		for (MapAreaPoint p : points) {
			events.offer(new SiteEvent(p));
		}

		// until here, only points are in the queue
		// float minx = events.peek().getX();
		// while (events.peek().getX() == minx) {
		// events.poll();
		// }
		// VoronoiEvent first = events.poll();
		// beach.add(((SiteEvent) first).getSite());

		// TODO: ADD first points to tree.

		while (!events.isEmpty()) {
			VoronoiEvent e = events.poll();

			// Parabola[] arcsToCheck = new Parabola[0];
			if (e.isVoronoiSite()) {
				// point event
				SiteEvent se = (SiteEvent) e;
				beach.add(se.getSite(), cem);

				// crete edge: newArc, brokenArc
			} else {
				// circle closed event
				CircleEvent ce = ((CircleEvent) e);

				@SuppressWarnings("unused")
				VoronoiVertex vertex = mesh.addVoronoiVertex(ce.getCenter());

				BeachLinePart middle = ce.getMiddle(); // disappearing
				BeachLinePart top = ce.getTop();
				BeachLinePart bottom = ce.getTop();

				if (bottom == null || top == null || !bottom.equals(beach.getBottom(middle)) || !top.equals(beach.getTop(middle))) {
					// the graph changed, we are aborting
					// nothing to do
				} else {

					@SuppressWarnings("unused")
					BeachLinePart[] circlesToCheck = new BeachLinePart[] { top, bottom };
					// create the new point
					@SuppressWarnings("unused")
					Point2D newVertex = ce.getCenter();
					@SuppressWarnings("unused")
					BeachSeparator otherSeparator;
					// remove the found circle from the beach line
					BeachSeparator parent = middle.getParent();
					if (parent.getTopChild() == middle) {
						// bottom child has to be bottom, so we found the bottom separator.
						// newo find the top separator.
						// otherSeparator = beach.findSeparatorAbove(middle);

						// remove the simple separator
						// parent.getParent().replaceChild(parent, parent.getBottomChild());
					}
					// connect the point

				}

			}

			// for (Parabola arc : arcsToCheck) {
			//
			// }
		}

		return null;
	}
}
