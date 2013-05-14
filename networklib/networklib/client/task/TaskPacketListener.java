package networklib.client.task;

import java.io.IOException;
import java.lang.reflect.Method;

import networklib.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TaskPacketListener extends PacketChannelListener<TaskPacket> {

	private final ITaskReceiver taskReceiver;

	public TaskPacketListener(ITaskReceiver taskReceiver) {
		super(NetworkConstants.Keys.SYNCHRONOUS_TASK, TaskPacket.DEFAULT_DESERIALIZER);
		this.taskReceiver = taskReceiver;
	}

	@Override
	protected void receivePacket(int key, TaskPacket packet) throws IOException {
		try {
			Method method = taskReceiver.getClass().getMethod("receiveTask", packet.getClass());
			method.invoke(taskReceiver, packet);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
