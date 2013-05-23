package networklib.client;

import java.util.Arrays;

import networklib.NetworkConstants;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.client.task.packets.TaskPacket;
import networklib.client.time.IGameClock;
import networklib.synchronic.timer.NetworkTimer;

/**
 * This is a {@link ITaskScheduler} implementation that supports offline gameplay. It directly schedules the tasks in the {@link NetworkTimer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class OfflineTaskScheduler implements ITaskScheduler {

	private final NetworkTimer networkTimer = new NetworkTimer();

	@Override
	public void scheduleTask(TaskPacket task) throws Exception {
		networkTimer.scheduleSyncTasksPacket(new SyncTasksPacket(networkTimer.getTime() / NetworkConstants.Client.LOCKSTEP_PERIOD + 2,
				Arrays.asList(task)));
	}

	@Override
	public IGameClock getGameClock() {
		return networkTimer;
	}
}
