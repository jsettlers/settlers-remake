package jsettlers.network.client;

import java.util.Arrays;

import jsettlers.network.NetworkConstants;
import jsettlers.network.client.interfaces.IGameClock;
import jsettlers.network.client.interfaces.INetworkConnector;
import jsettlers.network.client.interfaces.ITaskScheduler;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.synchronic.timer.NetworkTimer;

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
