package networklib.server.actions.reject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.IDeserializingable;
import networklib.channel.NetworkConstants;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RejectPacket extends Packet {
	/**
	 * This is the default implementation of the {@link IDeserializingable} interface to deserialize objects of this class.
	 */
	public static final IDeserializingable<RejectPacket> DEFAULT_DESERIALIZER = new IDeserializingable<RejectPacket>() {
		@Override
		public RejectPacket deserialize(int key, DataInputStream dis) throws IOException {
			RejectPacket packet = new RejectPacket();
			packet.deserialize(dis);
			return packet;
		}
	};

	private int errorMessageId;
	private int rejectedKey;

	public RejectPacket() {
		super(NetworkConstants.Keys.REJECT_PACKET);
	}

	public RejectPacket(int errorMessageId, int rejectedKey) {
		this();
		this.errorMessageId = errorMessageId;
		this.rejectedKey = rejectedKey;
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
		dos.writeInt(errorMessageId);
		dos.writeInt(rejectedKey);
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
		errorMessageId = dis.readInt();
		rejectedKey = dis.readInt();
	}

	public int getErrorMessageId() {
		return errorMessageId;
	}

	public int getRejectedKey() {
		return rejectedKey;
	}
}
