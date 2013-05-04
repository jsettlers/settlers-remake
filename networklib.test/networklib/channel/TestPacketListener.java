package networklib.channel;

import java.util.LinkedList;

import networklib.channel.listeners.PacketChannelListener;

public class TestPacketListener extends PacketChannelListener<TestPacket> {

	public TestPacketListener(int key) {
		super(key, TestPacket.DEFAULT_DESERIALIZER);
	}

	public final LinkedList<TestPacket> packets = new LinkedList<TestPacket>();

	@Override
	protected void receivePacket(int key, TestPacket deserialized) {
		this.packets.addLast(deserialized);
	}

}