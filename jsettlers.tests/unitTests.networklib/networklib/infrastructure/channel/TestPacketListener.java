package networklib.infrastructure.channel;

import java.util.LinkedList;

import networklib.NetworkConstants.ENetworkKey;
import networklib.infrastructure.channel.listeners.PacketChannelListener;

public class TestPacketListener extends PacketChannelListener<TestPacket> {

	public TestPacketListener(ENetworkKey key) {
		super(key, TestPacket.DEFAULT_DESERIALIZER);
	}

	public final LinkedList<TestPacket> packets = new LinkedList<TestPacket>();

	@Override
	protected void receivePacket(ENetworkKey key, TestPacket deserialized) {
		this.packets.addLast(deserialized);
	}

}