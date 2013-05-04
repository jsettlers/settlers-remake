package networklib.server.listeners.reject;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.packets.RejectPacket;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RejectListener extends PacketChannelListener<RejectPacket> {

	public RejectListener() {
		super(NetworkConstants.Keys.REJECT_PACKET, new GenericDeserializer<RejectPacket>(RejectPacket.class));
	}

	@Override
	protected void receivePacket(int key, RejectPacket rejectPacket) {
		System.out.println("errorMessageId: " + rejectPacket.getErrorMessageId() + "  rejectedKey: " + rejectPacket.getRejectedKey());
	}

}
