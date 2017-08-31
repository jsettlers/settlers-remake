package jsettlers.network.client.task.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class SyncTasksPacket extends Packet {

	private int lockstepNumber;
	private List<TaskPacket> tasks;

	public SyncTasksPacket() {
	}

	public SyncTasksPacket(int packetNumber, List<TaskPacket> tasks) {
		this.lockstepNumber = packetNumber;
		this.tasks = tasks;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(lockstepNumber);
		dos.writeInt(tasks.size());

		for (TaskPacket curr : tasks) {
			curr.serialize(dos);
		}
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		lockstepNumber = dis.readInt();
		int numberOfTasks = dis.readInt();
		tasks = new LinkedList<>();

		for (int i = 0; i < numberOfTasks; i++) {
			TaskPacket task = TaskPacket.DEFAULT_DESERIALIZER.deserialize(null, dis);
			tasks.add(task);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lockstepNumber;
		result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SyncTasksPacket other = (SyncTasksPacket) obj;
		if (lockstepNumber != other.lockstepNumber)
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		return true;
	}

	/**
	 * @return the packetNumber
	 */
	public int getLockstepNumber() {
		return lockstepNumber;
	}

	/**
	 * @return the tasks
	 */
	public List<TaskPacket> getTasks() {
		return tasks;
	}

	@Override
	public String toString() {
		return "lockstep: " + lockstepNumber + " tasks: " + tasks;
	}
}
