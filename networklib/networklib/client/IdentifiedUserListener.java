package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants.ENetworkKey;
import networklib.infrastructure.channel.GenericDeserializer;
import networklib.infrastructure.channel.listeners.PacketChannelListener;
import networklib.infrastructure.channel.packet.EmptyPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifiedUserListener extends PacketChannelListener<EmptyPacket> {

	private NetworkClient networkClient;

	public IdentifiedUserListener(NetworkClient networkClient) {
		super(ENetworkKey.IDENTIFY_USER, new GenericDeserializer<EmptyPacket>(EmptyPacket.class));

		this.networkClient = networkClient;
	}

	@Override
	protected void receivePacket(ENetworkKey key, EmptyPacket packet) throws IOException {
		networkClient.identifiedUserEvent();
	}
}
