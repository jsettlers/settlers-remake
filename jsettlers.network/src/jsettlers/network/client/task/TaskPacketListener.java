package jsettlers.network.client.task;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;

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
