package jsettlers.network.client.receiver;

import jsettlers.network.client.NetworkClient;
import jsettlers.network.infrastructure.channel.packet.Packet;

/**
 * This interface defines a receiver for packets send by the server and received by the {@link NetworkClient}.
 * 
 * @author Andreas Eberle
 */
public interface IPacketReceiver<T extends Packet> {

	void receivePacket(T packet);
}
