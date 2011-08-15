package jsettlers.logic.algorithms.path.area;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;
import jsettlers.logic.algorithms.path.wrapper.PathfinderWrapper;
import jsettlers.logic.algorithms.path.wrapper.requests.InAreaRequest;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public class InAreaFinderQueue implements INetworkTimerable {

	private final InAreaFinder inAreaFinder;

	private final Queue<InAreaRequest> pathRequests = new ConcurrentLinkedQueue<InAreaRequest>();

	public InAreaFinderQueue(IDijkstraPathMap map) {
		inAreaFinder = new InAreaFinder(map);
		NetworkTimer.schedule(this, (short) 2);
	}

	public void findPath(IPathRequester requester, ISPosition2D centerPos, short searchRadius, ESearchType type) {
		pathRequests.offer(new InAreaRequest(requester, centerPos.getX(), centerPos.getY(), searchRadius, type));
	}

	@Override
	public void timerEvent() {
		try {
			InAreaRequest request;

			if (pathRequests.isEmpty()) {
				return;
			}

			request = pathRequests.poll();

			ISPosition2D tilePos = inAreaFinder.find(request.getRequester(), request.getCx(), request.getCy(), request.getTileRadius(),
					request.getType());

			if (tilePos != null) { // give calculation of path to AStar
				request.setAStartTarget(tilePos);
				PathfinderWrapper.findWithAStarPath(request);
			} else {
				request.setFailed();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * NOTE: you can't restart this task
	 */
	public void cancel() {
		NetworkTimer.remove(this);
	}

}
