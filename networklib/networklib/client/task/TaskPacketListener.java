package networklib.client.task;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.client.packets.SyncTasksPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskPacketListener extends PacketChannelListener<SyncTasksPacket> {

	private final ITaskScheduler taskScheduler;

	public TaskPacketListener(ITaskScheduler taskScheduler) {
		super(NetworkConstants.Keys.SYNCHRONOUS_TASK, new GenericDeserializer<SyncTasksPacket>(SyncTasksPacket.class));
		this.taskScheduler = taskScheduler;
	}

	@Override
	protected void receivePacket(int key, SyncTasksPacket packet) throws IOException {
		taskScheduler.scheduleTasksAndUnlockStep(packet.getLockstepNumber(), packet.getTasks());
	}

}
