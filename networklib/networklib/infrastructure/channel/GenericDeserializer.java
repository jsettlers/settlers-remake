package networklib.infrastructure.channel;

import java.io.DataInputStream;
import java.io.IOException;

import networklib.infrastructure.channel.packet.Packet;

/**
 * This is a generic implementation of {@link IDeserializingable} that simply calls the {@link Packet}.deserialize() method.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class GenericDeserializer<T extends Packet> implements IDeserializingable<T> {

	private final Class<T> classType;

	public GenericDeserializer(Class<T> classType) {
		this.classType = classType;
	}

	@Override
	public T deserialize(int key, DataInputStream dis) throws IOException {
		try {
			T packet = classType.newInstance();
			packet.deserialize(dis);
			return packet;
		} catch (InstantiationException e) {
			throw new IOException("Error creating packet of type " + classType, e);
		} catch (IllegalAccessException e) {
			throw new IOException("Error creating packet of type " + classType, e);
		}
	}

}
