package jsettlers.logic.algorithms.path.astar;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;
import jsettlers.logic.algorithms.path.wrapper.requests.AStarRequest;
import jsettlers.logic.algorithms.path.wrapper.requests.AbstractAStarRequest;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public class AStarThread implements INetworkTimerable {

	private final HexAStar aStar;

	private final Queue<AbstractAStarRequest> pathRequests = new ConcurrentLinkedQueue<AbstractAStarRequest>();

	public AStarThread(IAStarPathMap map) {
		aStar = new HexAStar(map);
		NetworkTimer.schedule(this, (short) 2);
	}

	@Override
	public void timerEvent() {
		try {
			if (!pathRequests.isEmpty()) {
				AbstractAStarRequest request = pathRequests.poll();

				Path path = aStar.findPath(request);
				if (path != null) {
					request.setFoundPath(path);
				} else {
					request.setFailed();
				}
			}
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}
	}

	/**
	 * NOTE: you can't restart this thread
	 */
	public void cancel() {
		NetworkTimer.remove(this);
	}

	public void findPath(IPathRequester requester, short tx, short ty) {
		pathRequests.add(new AStarRequest(requester, tx, ty));
	}

	public void findPath(AbstractAStarRequest request) {
		pathRequests.offer(request);
	}

}
