package networklib.server.lockstep;

import java.util.List;
import java.util.TimerTask;

import networklib.NetworkConstants;
import networklib.infrastructure.channel.ping.IPingUpdateListener;
import networklib.infrastructure.channel.ping.RoundTripTime;
import networklib.server.game.Match;
import networklib.server.packets.ServersideSyncTasksPacket;
import networklib.server.packets.ServersideTaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskSendingTimerTask extends TimerTask implements IPingUpdateListener {
	private static final int LEAD_TIME_DECREASE_STEPS = 10;

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
		int leadSteps = (currentLeadTimeMs / NetworkConstants.Client.LOCKSTEP_PERIOD);
		currentLockstepMax = Math.max(currentLockstepMax, acknowledgedLockstep + leadSteps);
		// System.out.println("lead steps: " + leadSteps);
	}

	@Override
	public void pingUpdated(RoundTripTime rtt) {
		if (rtt.getRtt() > 5000) {
			return; // this is exceptional, we can not adapt to this
		}

		currentLeadTimeMs = (int) Math.max(currentLeadTimeMs - LEAD_TIME_DECREASE_STEPS, rtt.getRtt() * 1.1f
				+ NetworkConstants.Client.LOCKSTEP_PERIOD);
		System.out.println("lead time: " + currentLeadTimeMs);
	}
}
