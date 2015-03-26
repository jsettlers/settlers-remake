package jsettlers.network.infrastructure.channel.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.IDeserializingable;

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
