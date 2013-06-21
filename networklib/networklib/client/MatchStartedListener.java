package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.NetworkConstants.ENetworkKey;
import networklib.client.receiver.IPacketReceiver;
import networklib.common.packets.MatchStartPacket;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;

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
