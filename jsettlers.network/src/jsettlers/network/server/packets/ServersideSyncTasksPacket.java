package jsettlers.network.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import jsettlers.network.client.task.packets.SyncTasksPacket;
import jsettlers.network.client.task.packets.TaskPacket;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This subclass of {@link Packet} is the server side representation of the client side {@link SyncTasksPacket}. The server side representation uses
 * the {@link ServersideTaskPacket} instead of the clients {@link TaskPacket}. The {@link ServersideTaskPacket} does not deserialize the data and is
 * therefore independent from the data in the {@link TaskPacket}.
 * 
 * @author Andreas Eberle
 * 
 */
public class ServersideSyncTasksPacket extends Packet {

	private int lockstepNumber;
	private List<ServersideTaskPacket> tasks;

	public ServersideSyncTasksPacket() {
	}

	public ServersideSyncTasksPacket(int packetNumber, List<ServersideTaskPacket> tasks) {
		this.lockstepNumber = packetNumber;
		this.tasks = tasks;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(lockstepNumber);
		dos.writeInt(tasks.size());

		for (ServersideTaskPacket curr : tasks) {
			curr.serialize(dos);
		}
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		lockstepNumber = dis.readInt();
		int numberOfTasks = dis.readInt();
		tasks = new LinkedList<ServersideTaskPacket>();

		for (int i = 0; i < numberOfTasks; i++) {
			ServersideTaskPacket curr = new ServersideTaskPacket();
			curr.deserialize(dis);
			tasks.add(curr);
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
		ServersideSyncTasksPacket other = (ServersideSyncTasksPacket) obj;
		if (lockstepNumber != other.lockstepNumber)
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		return true;
	}
}
