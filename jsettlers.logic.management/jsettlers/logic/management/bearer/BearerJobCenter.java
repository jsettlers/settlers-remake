package jsettlers.logic.management.bearer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.IJob;
import jsettlers.logic.management.bearer.job.BearerCarryJob;
import jsettlers.logic.management.bearer.job.BearerToWorkerJob;
import synchronic.timer.INetworkTimerable;
import synchronic.timer.NetworkTimer;

public class BearerJobCenter implements INetworkTimerable {
	private List<IBearerJobable> jobless = Collections.synchronizedList(new LinkedList<IBearerJobable>());
	private List<BearerCarryJob> carryJobs = Collections.synchronizedList(new LinkedList<BearerCarryJob>());
	private List<BearerToWorkerJob> toWorkerJobs = Collections.synchronizedList(new LinkedList<BearerToWorkerJob>());

	public void start() {
		NetworkTimer.schedule(this, (short) 10);
	}

	public void addJobless(IBearerJobable movable) {
		jobless.add(movable); // check if movable is of type Bearer
	}

	@Override
	public void timerEvent() {
		try {
			if (!jobless.isEmpty() && !carryJobs.isEmpty()) {
				BearerCarryJob job = carryJobs.remove(0);
				IBearerJobable curr = getNearestJobable(job);
				curr.setCarryJob(job);
			}
			if (!jobless.isEmpty() && !toWorkerJobs.isEmpty()) {
				BearerToWorkerJob job = toWorkerJobs.remove(0);
				IBearerJobable curr = getNearestJobable(job);
				curr.setToWorkerJob(job);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private IBearerJobable getNearestJobable(IJob job) {
		ISPosition2D target = job.getFirstPos();

		int bIdx = 0;
		if (target != null) {
			bIdx = ILocatable.Methods.getNearest(jobless, target);
		}

		return jobless.remove(bIdx);
	}

	public void cancel() {
		NetworkTimer.remove(this);
	}

	public void addCarryJob(BearerCarryJob job) {
		carryJobs.add(job);
	}

	public void addToWorkerJob(BearerToWorkerJob job) {
		toWorkerJobs.add(job);
	}

}
