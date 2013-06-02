package networklib.client.interfaces;

import networklib.client.task.packets.TaskPacket;
import networklib.synchronic.timer.INetworkTimerable;

/**
 * This interface defines a method to schedule a {@link TaskPacket} for execution.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskScheduler {

	/**
	 * Schedules the given task for execution.
	 * 
	 * @param task
	 *            The task to be scheduled.
	 * @throws Exception
	 *             If anything goes wrong, an exception may be thrown.
	 */
	void scheduleTask(TaskPacket task);

	/**
	 * 
	 * @return Returns the {@link IGameClock} that can be used to attach {@link INetworkTimerable}s for synchronous execution.
	 */
	IGameClock getGameClock();
}
