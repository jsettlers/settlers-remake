package networklib.server.actions.ack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.IDeserializingable;
import networklib.channel.NetworkConstants;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 */
public class AcknowledgePacket extends Packet {
	/**
	 * This is the default implementation of the {@link IDeserializingable} interface to deserialize objects of this class.
	 */
	public static final IDeserializingable<AcknowledgePacket> DEFAULT_DESERIALIZER = new IDeserializingable<AcknowledgePacket>() {
		@Override
		public AcknowledgePacket deserialize(int key, DataInputStream dis) throws IOException {
			AcknowledgePacket packet = new AcknowledgePacket();
			packet.deserialize(dis);
			return packet;
		}
	};

	private int acknowledgedKey;

	public AcknowledgePacket() {
		super(NetworkConstants.Keys.ACKNOWLEDGE_PACKET);
	}

	public AcknowledgePacket(int acknowledgedKey) {
		this();
		this.acknowledgedKey = acknowledgedKey;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(acknowledgedKey);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		acknowledgedKey = dis.readInt();
	}

	public int getAcknowledgedKey() {
		return acknowledgedKey;
	}
}
