package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.client.receiver.IPacketReceiver;
import networklib.server.packets.MatchInfoUpdatePacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoUpdatedListener extends PacketChannelListener<MatchInfoUpdatePacket> {
	private final NetworkClient client;
	private final IPacketReceiver<MatchInfoUpdatePacket> listener;

	public MatchInfoUpdatedListener(NetworkClient client, IPacketReceiver<MatchInfoUpdatePacket> listener) {
		super(NetworkConstants.Keys.MATCH_INFO_UPDATE, new GenericDeserializer<MatchInfoUpdatePacket>(MatchInfoUpdatePacket.class));

		this.client = client;
		this.listener = listener;
	}

	@Override
	protected void receivePacket(int key, MatchInfoUpdatePacket packet) throws IOException {
		client.matchInfoUpdated(packet);
		listener.receivePacket(packet);
	}

}
