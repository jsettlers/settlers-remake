package networklib.synchronic.timer;

import networklib.client.task.packets.TaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskExecutor {

	/**
	 * Executes the given task packet.
	 * 
	 * @param task
	 */
	void executeTask(TaskPacket task);

}
