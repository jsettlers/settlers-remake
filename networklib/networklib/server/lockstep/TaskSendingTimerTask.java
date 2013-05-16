package networklib.server.lockstep;

import java.util.List;
import java.util.TimerTask;

import networklib.NetworkConstants;
import networklib.server.game.Match;
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
		match.sendMessage(NetworkConstants.Keys.SYNCHRONOUS_TASK, syncTasksPacket);
	}

	public void receivedLockstepAcknowledge(int acknowledgedLockstep) {
		currentLockstepMax = Math.max(currentLockstepMax, acknowledgedLockstep + NetworkConstants.Client.LOCKSTEP_DEFAULT_LEAD_STEPS);
	}
}
