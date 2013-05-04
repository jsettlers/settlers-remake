package networklib.server.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.channel.IDeserializingable;
import networklib.channel.Packet;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class KeyOnlyPacket extends Packet {
	public static IDeserializingable<KeyOnlyPacket> DEFAULT_DESERIALIZER = new IDeserializingable<KeyOnlyPacket>() {
		@Override
		public KeyOnlyPacket deserialize(int key, DataInputStream dis) throws IOException {
			KeyOnlyPacket packet = new KeyOnlyPacket(key);
			packet.deserialize(dis);
			return packet;
		}
	};

	public KeyOnlyPacket(int key) {
		super(key);
	}

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
	}
}
