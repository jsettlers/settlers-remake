package networklib.channel;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;

import networklib.channel.IDeserializingable;
import networklib.channel.listeners.PacketChannelListener;

public class TestPacketListener extends PacketChannelListener<TestPacket> {

	public TestPacketListener(int key) {
		super(key, new IDeserializingable<TestPacket>() {
			@Override
			public TestPacket deserialize(int key, DataInputStream dis) throws IOException {
				TestPacket packet = new TestPacket(key);
				packet.deserialize(dis);
				return packet;
			}
		});
	}

	public final LinkedList<TestPacket> packets = new LinkedList<TestPacket>();

	@Override
	protected void receivePacket(TestPacket deserialized) {
		this.packets.addLast(deserialized);
	}

}