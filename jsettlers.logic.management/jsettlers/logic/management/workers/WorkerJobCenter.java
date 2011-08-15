package jsettlers.logic.management.workers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.logic.management.GameManager;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

/**
 * Manages requests, creation of workers and jobless workers.
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkerJobCenter<T extends AbstractWorkerRequest> implements INetworkTimerable {
	private static final int LENGTH = EMovableType.values().length;

	@SuppressWarnings("unchecked")
	private List<IWorkerJobable<T>>[] jobless = new List[LENGTH];
	private Queue<IWorkerJobable<T>> newJobless = new ConcurrentLinkedQueue<IWorkerJobable<T>>();

	private List<T> checkedRequests = Collections.synchronizedList(new ArrayList<T>());
	private Queue<T> newRequests = new ConcurrentLinkedQueue<T>();

	public WorkerJobCenter() {
		for (int i = 0; i < LENGTH; i++) {
			jobless[i] = Collections.synchronizedList(new ArrayList<IWorkerJobable<T>>());
		}
	}

	public void start() {
		NetworkTimer.schedule(this, (short) 10);
	}

	public void request(T request) {
		newRequests.offer(request);
	}

	/**
	 * Adds a new movable to the list of jobless workers.
	 * 
	 * @param movable
	 *            The movable to add.
	 */
	public void addJobless(IWorkerJobable<T> movable) {
		newJobless.offer(movable);
	}

	@Override
	public void timerEvent() {
		try {
			handleCheckedRequests();

			addNewRequests();

			addNewJobless();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void addNewRequests() {
		while (!newRequests.isEmpty()) {
			T curr = newRequests.poll();

			int workerid = curr.getWorkerType().ordinal();
			if (jobless[workerid].isEmpty()) {
				GameManager.requestMovable(curr.getWorkerType(), curr.getPlayer());
			}
			checkedRequests.add(curr);
		}
	}

	private void handleCheckedRequests() {
		// for every request we checked if we need a new Worker, check if the Worker is now available
		for (int i = 0; i < checkedRequests.size();) {
			T currRequest = checkedRequests.get(i);

			if (!jobless[currRequest.getWorkerType().ordinal()].isEmpty()) {
				int idx = ILocatable.Methods.getNearest(jobless[currRequest.getWorkerType().ordinal()], currRequest.getPos());

				IWorkerJobable<T> worker = jobless[currRequest.getWorkerType().ordinal()].remove(idx);
				checkedRequests.remove(i);
				worker.setWorkerRequest(currRequest);
			} else {
				i++;
			}
		}
	}

	/**
	 * adds the new jobless workers to the correct list for their EMovableType.
	 */
	private void addNewJobless() {
		for (IWorkerJobable<T> curr : newJobless) {
			jobless[curr.getMovableType().ordinal()].add(curr);
		}
		newJobless.clear();
	}

	public void cancel() {
		NetworkTimer.remove(this);
	}

}
