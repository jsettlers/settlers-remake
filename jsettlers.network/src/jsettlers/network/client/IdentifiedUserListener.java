package jsettlers.network.client;

import java.io.IOException;

import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.GenericDeserializer;
import jsettlers.network.infrastructure.channel.listeners.PacketChannelListener;
import jsettlers.network.infrastructure.channel.packet.EmptyPacket;

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
