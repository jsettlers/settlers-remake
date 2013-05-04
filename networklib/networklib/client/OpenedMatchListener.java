package networklib.client;

import java.io.IOException;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.client.receiver.IPacketReceiver;
import networklib.server.packets.MatchInfoPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class OpenedMatchListener extends PacketChannelListener<MatchInfoPacket> {
	private final NetworkClient client;
	private final IPacketReceiver<MatchInfoPacket> listener;

	public OpenedMatchListener(NetworkClient client, IPacketReceiver<MatchInfoPacket> listener) {
		super(NetworkConstants.Keys.MATCH_INFO, new GenericDeserializer<MatchInfoPacket>(MatchInfoPacket.class));

		this.client = client;
		this.listener = listener;
	}

	@Override
	protected void receivePacket(MatchInfoPacket deserialized) throws IOException {
		client.openedMatch(deserialized);
		listener.receivePacket(deserialized);
	}

}
