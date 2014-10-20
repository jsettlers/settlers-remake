package networklib.client.task.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.infrastructure.channel.IDeserializingable;
import networklib.infrastructure.channel.packet.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class TaskPacket extends Packet {
	public static final IDeserializingable<TaskPacket> DEFAULT_DESERIALIZER = new IDeserializingable<TaskPacket>() {

		@Override
		public TaskPacket deserialize(ENetworkKey key, DataInputStream dis) throws IOException {
			try {
				dis.readInt(); // read the length in bytes from the stream. We don't need it here, only the server needs it.
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
		ByteArrayOutputStream bufferOutStream = new ByteArrayOutputStream();
		DataOutputStream bufferDataOutStream = new DataOutputStream(bufferOutStream);

		bufferDataOutStream.writeUTF(this.getClass().getName());
		serializeTask(bufferDataOutStream);
		bufferDataOutStream.flush();

		dos.writeInt(bufferOutStream.size());
		bufferOutStream.writeTo(dos);
	}

	protected abstract void serializeTask(DataOutputStream dos) throws IOException;

	@Override
	public final void deserialize(DataInputStream dis) throws IOException {
	}

	protected abstract void deserializeTask(DataInputStream dis) throws IOException;

}
