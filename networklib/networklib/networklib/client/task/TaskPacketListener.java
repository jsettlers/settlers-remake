package networklib.client.task;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.client.task.packets.SyncTasksPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskPacketListener extends PacketChannelListener<SyncTasksPacket> {

	private final ISyncTasksPacketScheduler receiver;

	public TaskPacketListener(ISyncTasksPacketScheduler receiver) {
		super(NetworkConstants.ENetworkKey.SYNCHRONOUS_TASK, new GenericDeserializer<SyncTasksPacket>(SyncTasksPacket.class));
		this.receiver = receiver;
	}

	@Override
	protected void receivePacket(ENetworkKey key, SyncTasksPacket packet) throws IOException {
		receiver.scheduleSyncTasksPacket(packet);
	}
}
