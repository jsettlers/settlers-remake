package networklib.infrastructure.channel.listeners;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Hashtable;

import networklib.NetworkConstants.ENetworkKey;
import networklib.infrastructure.channel.Channel;
import networklib.infrastructure.channel.IChannelListener;
import networklib.infrastructure.channel.IDeserializingable;
import networklib.infrastructure.channel.packet.Packet;

/**
 * This abstract class deserializes the packet received from a {@link Channel} with the {@link IDeserializingable}s supplied for each key and defines
 * a specialized method to receive the deserialized Packet in subtypes of this class.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class PacketChannelListener<T extends Packet> implements IChannelListener {

	private final ENetworkKey[] keys;
	private final Hashtable<ENetworkKey, IDeserializingable<T>> deserializers = new Hashtable<ENetworkKey, IDeserializingable<T>>();

	public PacketChannelListener(ENetworkKey key, IDeserializingable<T> deserializer) {
		this.keys = new ENetworkKey[] { key };
		this.deserializers.put(key, deserializer);
	}

	public PacketChannelListener(ENetworkKey[] keys, IDeserializingable<T>[] deserializers) {
		assert keys.length == deserializers.length;

		this.keys = keys;

		for (int i = 0; i < keys.length; i++) {
			this.deserializers.put(keys[i], deserializers[i]);
		}
	}

	@Override
	public ENetworkKey[] getKeys() {
		return keys;
	}

	@Override
	public final void receive(ENetworkKey key, int length, DataInputStream stream) throws IOException, ClassNotFoundException {
		IDeserializingable<T> deserializer = deserializers.get(key);
		assert deserializer != null;

		T deserializedPacket = deserializer.deserialize(key, stream);
		receivePacket(key, deserializedPacket);
	}

	protected abstract void receivePacket(ENetworkKey key, T packet) throws IOException;

}
