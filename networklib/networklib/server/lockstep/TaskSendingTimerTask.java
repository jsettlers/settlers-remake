package networklib.server.lockstep;

import java.util.List;
import java.util.TimerTask;

import networklib.NetworkConstants;
import networklib.infrastructure.channel.ping.IPingUpdateListener;
import networklib.infrastructure.channel.ping.RoundTripTime;
import networklib.infrastructure.utils.MaximumSlotBuffer;
import networklib.server.game.Match;
import networklib.server.packets.ServersideSyncTasksPacket;
import networklib.server.packets.ServersideTaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskSendingTimerTask extends TimerTask {
	private static final int LEAD_TIME_DECREASE_STEPS = 20;

	private TaskCollectingListener taskCollectingListener;
	private Match match;

	private int lockstepCounter = 0;
	private int currentLockstepMax = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS;

	private int currentLeadTimeMs = NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS * NetworkConstants.Client.LOCKSTEP_PERIOD;

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
		int leadSteps = (int) Math.ceil(((float) currentLeadTimeMs) / NetworkConstants.Client.LOCKSTEP_PERIOD);
		currentLockstepMax = Math.max(currentLockstepMax, acknowledgedLockstep + leadSteps);
		// System.out.println("lead steps: " + leadSteps);
	}

	final void pingUpdated(int rtt, int jitter) {
		if (rtt > 3000 || jitter > 2000) {
			return; // this is exceptional, we can not adapt to this
		}

		currentLeadTimeMs = Math.max(currentLeadTimeMs - LEAD_TIME_DECREASE_STEPS, (int) (rtt / 2f * 1.1f
				+ NetworkConstants.Client.LOCKSTEP_PERIOD * 1.5f + jitter * 1.5f));
		System.out.println("ping/2 " + rtt / 2 + "    lead time: " + currentLeadTimeMs + "   jitter:   " + jitter);
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
