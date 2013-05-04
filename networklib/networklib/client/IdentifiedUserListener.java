package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.packets.KeyOnlyPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class IdentifiedUserListener extends PacketChannelListener<KeyOnlyPacket> {

	private NetworkClient networkClient;

	public IdentifiedUserListener(NetworkClient networkClient) {
		super(NetworkConstants.Keys.IDENTIFY_USER, KeyOnlyPacket.DEFAULT_DESERIALIZER);

		this.networkClient = networkClient;
	}

	@Override
	protected void receivePacket(KeyOnlyPacket packet) throws IOException {
		networkClient.identifiedUser();
	}
}
