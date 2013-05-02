package networklib.client.receiver;

import networklib.channel.Packet;
import networklib.client.NetworkClient;

/**
 * This interface defines a receiver for packets send by the server and received by the {@link NetworkClient}.
 * 
 * @author Andreas Eberle
 */
public interface IPacketReceiver<T extends Packet> {

	void receivePacket(T packet);
}
