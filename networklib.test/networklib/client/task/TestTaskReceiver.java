package networklib.client.task;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestTaskReceiver implements ITaskReceiver {
	private LinkedList<TaskPacket> buffer;

	TestTaskReceiver() {
		this.buffer = new LinkedList<TaskPacket>();
	}

	public void receiveTask(TestTaskPacket packet) {
		buffer.add(packet);
	}

	public List<TaskPacket> popBufferedPackets() {
		List<TaskPacket> temp = buffer;
		buffer = new LinkedList<TaskPacket>();
		return temp;
	}
}