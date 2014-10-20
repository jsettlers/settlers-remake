package networklib.client;

import java.util.Arrays;

import networklib.NetworkConstants;
import networklib.client.interfaces.IGameClock;
import networklib.client.interfaces.INetworkConnector;
import networklib.client.interfaces.ITaskScheduler;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.synchronic.timer.NetworkTimer;

/**
 * This is a {@link ITaskScheduler} implementation that supports offline gameplay. It directly schedules the tasks in the {@link NetworkTimer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class OfflineNetworkConnector implements ITaskScheduler, INetworkConnector {

	private final NetworkTimer networkTimer = new NetworkTimer(true);
	private boolean startFinished;

	@Override
	public void scheduleTask(TaskPacket task) {
		scheduleTaskAt(networkTimer.getTime() / NetworkConstants.Client.LOCKSTEP_PERIOD + 2, task);
	}

	/**
	 * Schedules the given task for execution in the given targetLockstep.
	 * 
	 * @param targetLockstep
	 *            Time the task should be scheduled in milliseconds.
	 * @param task
	 *            Task to be scheduled.
	 */
	public void scheduleTaskAt(int targetLockstep, TaskPacket task) {
		networkTimer.scheduleSyncTasksPacket(new SyncTasksPacket(targetLockstep, Arrays.asList(task)));
	}

	@Override
	public IGameClock getGameClock() {
		return networkTimer;
	}

	@Override
	public void shutdown() {
		networkTimer.stopExecution();
	}

	@Override
	public ITaskScheduler getTaskScheduler() {
		return this;
	}

	@Override
	public void setStartFinished(boolean startFinished) {
		this.startFinished = startFinished;
	}

	@Override
	public boolean haveAllPlayersStartFinished() {
		return startFinished;
	}

}
