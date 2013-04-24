package networklib.server.lockstep;

import java.util.LinkedList;
import java.util.List;

import networklib.channel.GenericDeserializer;
import networklib.channel.NetworkConstants;
import networklib.channel.feedthrough.FeedthroughBufferPacket;
import networklib.channel.feedthrough.FeedthroughablePacket;
import networklib.channel.listeners.PacketChannelListener;

/**
 * This listener collects all {@link FeedthroughablePacket} packets for the {@link NetworkConstants}.Keys.SYNCHRONOUS_TASK key and adds them to a
 * list. The elements can then be removed to be send to the clients as batch.
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
	protected void receivePacket(FeedthroughBufferPacket deserialized) {
		currTasksList.add(deserialized);
	}
}
