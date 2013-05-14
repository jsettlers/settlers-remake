package networklib.client.task;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.IDeserializingable;
import networklib.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class TaskPacket extends Packet {
	public static final IDeserializingable<TaskPacket> DEFAULT_DESERIALIZER = new IDeserializingable<TaskPacket>() {

		@Override
		public TaskPacket deserialize(int key, DataInputStream dis) throws IOException {
			try {
				String className = dis.readUTF();
				@SuppressWarnings("unchecked")
				Class<? extends TaskPacket> taskClass = (Class<? extends TaskPacket>) Class.forName(className);
				TaskPacket packet = taskClass.newInstance();
				packet.deserializeTask(dis);
				return packet;
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	};

	@Override
	public final void serialize(DataOutputStream dos) throws IOException {
		dos.writeUTF(this.getClass().getName());
		serializeTask(dos);
	}

	protected abstract void serializeTask(DataOutputStream dos) throws IOException;

	@Override
	public final void deserialize(DataInputStream dis) throws IOException {
	}

	protected abstract void deserializeTask(DataInputStream dis) throws IOException;

}
