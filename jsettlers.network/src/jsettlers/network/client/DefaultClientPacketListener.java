package jsettlers.network.client;

import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.infrastructure.channel.IDeserializingable;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.Packet;

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
