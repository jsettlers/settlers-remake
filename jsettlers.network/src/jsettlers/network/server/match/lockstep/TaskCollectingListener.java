package jsettlers.network.server.match.lockstep;

import java.util.LinkedList;
import java.util.List;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.server.packets.ServersideTaskPacket;

/**
 * This listener collects {@link Packet}s for the {@link NetworkConstants}.Keys.SYNCHRONOUS_TASK key and adds them to a list. The elements can then be
 * removed from the list to be send to the clients as batch.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskCollectingListener extends PacketChannelListener<ServersideTaskPacket> {
	private List<ServersideTaskPacket> currTasksList = new LinkedList<ServersideTaskPacket>();

	public TaskCollectingListener() {
		super(ENetworkKey.SYNCHRONOUS_TASK, new GenericDeserializer<ServersideTaskPacket>(ServersideTaskPacket.class));
	}

	/**
	 * 
	 * @return
	 */
	public List<ServersideTaskPacket> getAndResetTasks() {
		List<ServersideTaskPacket> temp = currTasksList;
		currTasksList = new LinkedList<ServersideTaskPacket>();
		return temp;
	}

	@Override
	protected void receivePacket(ENetworkKey key, ServersideTaskPacket deserialized) {
		currTasksList.add(deserialized);
	}
}
