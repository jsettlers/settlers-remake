package networklib.client.task;

import networklib.client.task.packets.SyncTasksPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskScheduler {

	void scheduleTasksAndUnlockStep(SyncTasksPacket tasksPacket);

}
