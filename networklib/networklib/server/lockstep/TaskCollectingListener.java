package networklib.server.lockstep;

import java.util.LinkedList;
import java.util.List;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.feedthrough.FeedthroughBufferPacket;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.packet.Packet;

/**
 * This listener collects {@link Packet}s for the {@link NetworkConstants}.Keys.SYNCHRONOUS_TASK key and adds them to a list. The elements can then be
 * removed from the list to be send to the clients as batch.
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskCollectingListener extends PacketChannelListener<FeedthroughBufferPacket> {
	private List<FeedthroughBufferPacket> currTasksList = new LinkedList<FeedthroughBufferPacket>();

	public TaskCollectingListener() {
		super(NetworkConstants.Keys.SYNCHRONOUS_TASK, new GenericDeserializer<FeedthroughBufferPacket>(FeedthroughBufferPacket.class));
	}

	/**
	 * 
	 * @return
	 */
	public List<FeedthroughBufferPacket> getAndResetTasks() {
		List<FeedthroughBufferPacket> temp = currTasksList;
		currTasksList = new LinkedList<FeedthroughBufferPacket>();
		return temp;
	}

	@Override
	protected void receivePacket(int key, FeedthroughBufferPacket deserialized) {
		currTasksList.add(deserialized);
	}
}
