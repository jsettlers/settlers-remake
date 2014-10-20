package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.MatchInfoUpdatePacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchInfoUpdatedListener extends PacketChannelListener<MatchInfoUpdatePacket> {
	private final NetworkClient client;
	private final IPacketReceiver<MatchInfoUpdatePacket> listener;

	public MatchInfoUpdatedListener(NetworkClient client, IPacketReceiver<MatchInfoUpdatePacket> listener) {
		super(NetworkConstants.ENetworkKey.MATCH_INFO_UPDATE, new GenericDeserializer<MatchInfoUpdatePacket>(MatchInfoUpdatePacket.class));

		this.client = client;
		this.listener = listener;
	}

	@Override
	protected void receivePacket(ENetworkKey key, MatchInfoUpdatePacket packet) throws IOException {
		client.matchInfoUpdated(packet);

		if (listener != null)
			listener.receivePacket(packet);
	}

}
