package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.client.receiver.IPacketReceiver;
import networklib.infrastructure.channel.IDeserializingable;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.infrastructure.channel.packet.Packet;

/**
 * Default {@link PacketChannelListener} used by the {@link NetworkClient} to dispatch received {@link Packet}s to a {@link IPacketReceiver}.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 *            The type of Packets that can be received by this {@link DefaultClientPacketListener}.
 */
public class DefaultClientPacketListener<T extends Packet> extends PacketChannelListener<T> {

	private IPacketReceiver<T> receiver;

	public DefaultClientPacketListener(ENetworkKey key, IDeserializingable<T> deserializer, IPacketReceiver<T> receiver) {
		super(key, deserializer);
		this.receiver = receiver;
	}

	@Override
	protected void receivePacket(ENetworkKey key, T packet) throws IOException {
		if (receiver != null)
			receiver.receivePacket(packet);
	}

}
