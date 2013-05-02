package networklib.client;

import java.io.IOException;

import networklib.channel.IDeserializingable;
import networklib.channel.Packet;
import networklib.channel.listeners.PacketChannelListener;
import networklib.client.receiver.IPacketReceiver;

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

	public DefaultClientPacketListener(int key, IDeserializingable<T> deserializer, IPacketReceiver<T> receiver) {
		super(key, deserializer);
		this.receiver = receiver;
	}

	@Override
	protected void receivePacket(T packet) throws IOException {
		receiver.receivePacket(packet);
	}

}
