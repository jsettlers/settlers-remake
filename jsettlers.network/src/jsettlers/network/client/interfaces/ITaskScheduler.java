package jsettlers.network.client.interfaces;

import jsettlers.network.client.task.packets.TaskPacket;

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

}
