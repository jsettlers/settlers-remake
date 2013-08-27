package networklib.server.match.lockstep;

import java.util.List;
import java.util.TimerTask;

import networklib.NetworkConstants;
import networklib.infrastructure.channel.ping.IPingUpdateListener;
import networklib.infrastructure.channel.ping.RoundTripTime;
import networklib.infrastructure.utils.MaximumSlotBuffer;
import networklib.server.match.Match;
import networklib.server.packets.ServersideSyncTasksPacket;
import networklib.server.packets.ServersideTaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskSendingTimerTask extends TimerTask {
	private TaskCollectingListener taskCollectingListener;
	private Match match;

	private int lockstepCounter = 0;
	private int currentLockstepMax = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS;

	private int minimumLeadTimeMs = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS * NetworkConstants.Client.LOCKSTEP_PERIOD;
	private int leadSteps = minimumLeadTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD;

	public TaskSendingTimerTask(TaskCollectingListener taskCollectingListener, Match match) {
		this.taskCollectingListener = taskCollectingListener;
		this.match = match;
	}

	@Override
	public void run() {
		if (lockstepCounter > currentLockstepMax) {
			return;
		}

		List<ServersideTaskPacket> tasksList = taskCollectingListener.getAndResetTasks();
		ServersideSyncTasksPacket syncTasksPacket = new ServersideSyncTasksPacket(lockstepCounter++, tasksList);
		match.broadcastMessage(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, syncTasksPacket);
	}

	public void receivedLockstepAcknowledge(int acknowledgedLockstep) {
		currentLockstepMax = Math.max(currentLockstepMax, acknowledgedLockstep + leadSteps);
		// System.out.println("lead steps: " + leadSteps);
	}

	final void pingUpdated(int rtt, int jitter) {
		if (rtt > 10000 || jitter > 5000) {
			return; // this is an exceptional high rtt, we can not adapt to this
		}

		int newLeadTime = (int) (rtt / 2 * 1.1f + jitter * 2f + NetworkConstants.Client.LOCKSTEP_PERIOD * 1.5f);
		if (newLeadTime > minimumLeadTimeMs) {
			minimumLeadTimeMs = newLeadTime;
		} else {
			minimumLeadTimeMs -= (minimumLeadTimeMs - newLeadTime) / 4;
		}

		leadSteps = (int) Math.ceil(((float) minimumLeadTimeMs) / NetworkConstants.Client.LOCKSTEP_PERIOD);

		System.out.println(String.format("rtt/2: %5d   min lead time: %4d   lead steps: %2d", rtt / 2,
				minimumLeadTimeMs, leadSteps));
	}

	private MaximumSlotBuffer rttMaximum = new MaximumSlotBuffer(0);
	private MaximumSlotBuffer jitterMaximum = new MaximumSlotBuffer(0);

	public IPingUpdateListener getPingListener(final int index) {
		if (rttMaximum.getLength() <= index) {
			rttMaximum = new MaximumSlotBuffer(index + 1);
			jitterMaximum = new MaximumSlotBuffer(index + 1);
		}

		return new IPingUpdateListener() {
			@Override
			public void pingUpdated(RoundTripTime rtt) {
				rttMaximum.insert(index, rtt.getRtt());
				jitterMaximum.insert(index, rtt.getAveragedJitter());

				TaskSendingTimerTask.this.pingUpdated(rttMaximum.getMax(), jitterMaximum.getMax());
			}
		};
	}
}
