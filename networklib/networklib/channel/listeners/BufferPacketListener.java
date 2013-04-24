package networklib.channel.listeners;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import networklib.channel.IDeserializingable;
import networklib.channel.Packet;

/**
 * This implementation of a {@link PacketChannelListener} collects the received packets to buffer them.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class BufferPacketListener<T extends Packet> extends PacketChannelListener<T> {

	private final Object lock = new Object();
	private List<T> packets = new LinkedList<T>();

	public BufferPacketListener(int key, IDeserializingable<T> deserializer) {
		super(key, deserializer);
	}

	@Override
	protected void receivePacket(T deserialized) throws IOException {
		synchronized (lock) {
			packets.add(deserialized);
		}
	}

	public List<T> popBufferedPackets() {
		synchronized (lock) {
			List<T> temp = packets;
			packets = new LinkedList<T>();
			return temp;
		}
	}
}
