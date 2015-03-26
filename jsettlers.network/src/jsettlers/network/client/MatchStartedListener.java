package jsettlers.network.client;

import java.io.IOException;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.client.receiver.IPacketReceiver;
import jsettlers.network.common.packets.MatchStartPacket;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class MatchStartedListener extends PacketChannelListener<MatchStartPacket> {

	private NetworkClient networkClient;
	private IPacketReceiver<MatchStartPacket> matchStartedListener;

	public MatchStartedListener(NetworkClient networkClient, IPacketReceiver<MatchStartPacket> matchStartedListener) {
		super(NetworkConstants.ENetworkKey.MATCH_STARTED, new GenericDeserializer<MatchStartPacket>(MatchStartPacket.class));
		this.networkClient = networkClient;
		this.matchStartedListener = matchStartedListener;
	}

	@Override
	protected void receivePacket(ENetworkKey key, MatchStartPacket packet) throws IOException {
		networkClient.matchStartedEvent();

		if (matchStartedListener != null)
			matchStartedListener.receivePacket(packet);
	}

}
