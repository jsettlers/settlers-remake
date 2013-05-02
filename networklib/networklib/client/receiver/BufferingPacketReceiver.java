package networklib.client.receiver;

import java.util.LinkedList;
import java.util.List;

import networklib.channel.Packet;

/**
 * This class implements the {@link IPacketReceiver} interface and buffers the received {@link Packet}s. This implementation is generic and can be
 * used for any subtype of {@link Packet}.
 * 
 * @author Andreas Eberle
 * 
 * @param <T>
 */
public class BufferingPacketReceiver<T extends Packet> implements IPacketReceiver<T> {

	private List<T> buffer = new LinkedList<T>();

	@Override
	public void receivePacket(T packet) {
		buffer.add(packet);
	}

	public List<T> popBufferedPackets() {
		List<T> temp = buffer;
		buffer = new LinkedList<T>();
		return temp;
	}
}
