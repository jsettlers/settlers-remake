package networklib.client.task;

import java.util.LinkedList;
import java.util.List;

import networklib.client.packets.TaskPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestTaskScheduler implements ITaskScheduler {
	private LinkedList<TaskPacket> buffer;
	private int lockstepNumber;

	public TestTaskScheduler() {
		this.buffer = new LinkedList<TaskPacket>();
	}

	public List<TaskPacket> popBufferedPackets() {
		List<TaskPacket> temp = buffer;
		buffer = new LinkedList<TaskPacket>();
		return temp;
	}

	@Override
	public void scheduleTasksAndUnlockStep(int lockstepNumber, List<TaskPacket> tasks) {
		this.lockstepNumber = lockstepNumber;
		buffer.addAll(tasks);
	}

	public int getUnlockedLockstepNumber() {
		return lockstepNumber;
	}
}