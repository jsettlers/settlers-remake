package networklib.server.actions.reject;

import networklib.NetworkConstants;
import networklib.channel.GenericDeserializer;
import networklib.channel.listeners.PacketChannelListener;
import networklib.server.actions.packets.RejectPacket;

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
	protected void receivePacket(RejectPacket rejectPacket) {
		System.out.println("errorMessageId: " + rejectPacket.getErrorMessageId() + "  rejectedKey: " + rejectPacket.getRejectedKey());
	}

}
