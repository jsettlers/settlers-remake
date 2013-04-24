package networklib.server.actions.reject;

import networklib.channel.NetworkConstants;
import networklib.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class RejectListener extends PacketChannelListener<RejectPacket> {

	public RejectListener() {
		super(NetworkConstants.Keys.REJECT_PACKET, RejectPacket.DEFAULT_DESERIALIZER);
	}

	@Override
	protected void receivePacket(RejectPacket rejectPacket) {
		System.out.println("errorMessageId: " + rejectPacket.getErrorMessageId() + "  rejectedKey: " + rejectPacket.getRejectedKey());
	}

}
