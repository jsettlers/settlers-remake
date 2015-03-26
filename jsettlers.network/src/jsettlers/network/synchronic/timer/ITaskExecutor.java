package jsettlers.network.synchronic.timer;

import jsettlers.network.client.task.packets.TaskPacket;

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
