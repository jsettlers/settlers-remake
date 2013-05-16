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

	private final ITaskScheduler taskReceiver;

	public TaskPacketListener(ITaskScheduler taskReceiver) {
		super(NetworkConstants.Keys.SYNCHRONOUS_TASK, new GenericDeserializer<SyncTasksPacket>(SyncTasksPacket.class));
		this.taskReceiver = taskReceiver;
	}

	@Override
	protected void receivePacket(int key, SyncTasksPacket packet) throws IOException {
		taskReceiver.scheduleTasksAndUnlockStep(packet.getLockstepNumber(), packet.getTasks());
	}

}
