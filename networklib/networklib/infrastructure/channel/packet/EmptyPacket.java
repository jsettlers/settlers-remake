package networklib.infrastructure.channel.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.IDeserializingable;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class EmptyPacket extends Packet {

	public static final IDeserializingable<EmptyPacket> DEFAULT_DESERIALIZER = new GenericDeserializer<EmptyPacket>(EmptyPacket.class);

	@Override
	public void serialize(DataOutputStream dos) throws IOException {
	}

	@Override
	public void deserialize(DataInputStream dis) throws IOException {
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o.getClass() == this.getClass();
	}

	@Override
	public int hashCode() {
		return this.getClass().hashCode();
	}
}
