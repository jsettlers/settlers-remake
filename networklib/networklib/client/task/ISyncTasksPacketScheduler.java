package networklib.client.task;

import networklib.client.task.packets.SyncTasksPacket;

/**
 * Implementors of this interface may receive {@link SyncTasksPacket}s from the {@link TaskPacketListener}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ISyncTasksPacketScheduler {

	/**
	 * This method will be called by the {@link TaskPacketListener} when it received a {@link SyncTasksPacket}.
	 * 
	 * @param packet
	 *            The received {@link SyncTasksPacket}.
	 */
	void scheduleSyncTasksPacket(SyncTasksPacket packet);

}
