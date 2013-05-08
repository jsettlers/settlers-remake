package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.channel.packet.EmptyPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifiedUserListener extends PacketChannelListener<EmptyPacket> {

	private NetworkClient networkClient;

	public IdentifiedUserListener(NetworkClient networkClient) {
		super(NetworkConstants.Keys.IDENTIFY_USER, new GenericDeserializer<EmptyPacket>(EmptyPacket.class));

		this.networkClient = networkClient;
	}

	@Override
	protected void receivePacket(int key, EmptyPacket packet) throws IOException {
		networkClient.identifiedUserEvent();
	}
}
