package networklib.client.task;

import java.util.LinkedList;
import java.util.List;

import networklib.client.task.packets.SyncTasksPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SyncTasksPacketSchedulerMock implements ISyncTasksPacketScheduler {
	private LinkedList<SyncTasksPacket> buffer;
	private int unlockedLockstep = 0;

	public SyncTasksPacketSchedulerMock() {
		this.buffer = new LinkedList<SyncTasksPacket>();
	}

	/**
	 * 
	 * @return Returns all {@link SyncTasksPacket}s that were received and had at least one task packaged in them.
	 */
	public List<SyncTasksPacket> popBufferedPackets() {
		List<SyncTasksPacket> temp = buffer;
		buffer = new LinkedList<SyncTasksPacket>();
		return temp;
	}

	@Override
	public void scheduleSyncTasksPacket(SyncTasksPacket tasksPacket) {
		unlockedLockstep = tasksPacket.getLockstepNumber();

		if (!tasksPacket.getTasks().isEmpty()) {
			buffer.add(tasksPacket);
		}
	}

	public int getUnlockedLockstepNumber() {
		return unlockedLockstep;
	}
}