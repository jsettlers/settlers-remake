package jsettlers.logic.algorithms.path.wrapper;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.area.InAreaFinderQueue;
import jsettlers.logic.algorithms.path.astar.AStarThread;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraQueue;
import jsettlers.logic.algorithms.path.wrapper.requests.AbstractAStarRequest;

/**
 * wrapper class for all pathfinder functions.
 * 
 * @author Andreas Eberle
 * 
 */
public class PathfinderWrapper {
	private static PathfinderWrapper uniIns;

	private final AStarThread aStar;
	private final DijkstraQueue<DijkstraAlgorithm> dijkstra;
	private final InAreaFinderQueue inAreaFinder;

	public static void startPathfinder(IPathfinderWrapperMap map) {
		if (uniIns == null) {
			uniIns = new PathfinderWrapper(map);
		}
	}

	private PathfinderWrapper(IPathfinderWrapperMap map) {
		aStar = new AStarThread(map);
		dijkstra = new DijkstraQueue<DijkstraAlgorithm>(new DijkstraAlgorithm(map));
		inAreaFinder = new InAreaFinderQueue(map);
	}

	public static void findPath(IPathRequester requester, short tx, short ty) {
		uniIns.aStar.findPath(requester, tx, ty);
	}

	public static void findPath(IPathRequester requester, ISPosition2D t) {
		findPath(requester, t.getX(), t.getY());
	}

	/**
	 * finds a path with the usual Dijkstra algorithm
	 * 
	 * @param requester
	 * @param center
	 * @param searchRadius
	 * @param type
	 * @return the request object to gain information about the state of the request
	 */
	public static void findDijkstraPath(IPathRequester requester, ISPosition2D center, short searchRadius, ESearchType type) {
		uniIns.dijkstra.findPath(requester, center.getX(), center.getY(), searchRadius, type);
	}

	/**
	 * NOTE: you can't restart this thread
	 */
	public static void cancel() {
		uniIns.aStar.cancel();
		uniIns.dijkstra.cancel();
		uniIns.inAreaFinder.cancel();
	}

	/**
	 * finds the path of the given request with AStar
	 * 
	 * @param request
	 *            request to be fulfilled
	 */
	public static void findWithAStarPath(AbstractAStarRequest request) {
		uniIns.aStar.findPath(request);
	}

	public static void findInAreaPath(IPathRequester pathableStrategy, ISPosition2D centerPos, short searchRadius, ESearchType type) {
		uniIns.inAreaFinder.findPath(pathableStrategy, centerPos, searchRadius, type);
	}
}
