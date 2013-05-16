package networklib.client.task;

import java.util.List;

import networklib.client.packets.TaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITaskScheduler {

	void scheduleTasksAndUnlockStep(int lockstepNumber, List<TaskPacket> tasks);

}
