package jsettlers.network.client;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.MatchInfoUpdatePacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;

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
