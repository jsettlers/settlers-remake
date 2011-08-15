package jsettlers.logic.algorithms.path.dijkstra;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;
import jsettlers.logic.algorithms.path.wrapper.PathfinderWrapper;
import jsettlers.logic.algorithms.path.wrapper.requests.DijkstraPathRequest;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public class DijkstraQueue<T extends IDijkstraAlgorithm> implements INetworkTimerable {

	private final T dijkstra;
	private final Queue<DijkstraPathRequest> pathRequests = new ConcurrentLinkedQueue<DijkstraPathRequest>();

	public DijkstraQueue(T dijkstra) {
		this.dijkstra = dijkstra;
		NetworkTimer.schedule(this, (short) 2);
	}

	@Override
	public void timerEvent() {
		try {
			if (pathRequests.isEmpty()) {
				return;
			}

			DijkstraPathRequest request = pathRequests.poll();

			ISPosition2D tilePos = dijkstra.find(request.getRequester(), request.getCenterX(), request.getCenterY(), request.getSearchRadius(),
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

	public void findPath(IPathRequester requester, short x, short y, short searchRadius, ESearchType type) {
		pathRequests.offer(new DijkstraPathRequest(requester, x, y, searchRadius, type));
	}

}
