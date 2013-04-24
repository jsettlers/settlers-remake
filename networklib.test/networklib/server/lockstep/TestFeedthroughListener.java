package networklib.server.lockstep;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.LinkedList;

import networklib.channel.IDeserializingable;
import networklib.channel.listeners.PacketChannelListener;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class TestFeedthroughListener extends PacketChannelListener<TestFeedthroughPacket> {

	public TestFeedthroughListener(int key) {
		super(key, new IDeserializingable<TestFeedthroughPacket>() {

			@Override
			public TestFeedthroughPacket deserialize(int key, DataInputStream dis) throws IOException {
				TestFeedthroughPacket packet = new TestFeedthroughPacket(key);
				packet.deserialize(dis);
				return packet;
			}
		});
	}

	public final LinkedList<TestFeedthroughPacket> packets = new LinkedList<TestFeedthroughPacket>();

	@Override
	protected void receivePacket(TestFeedthroughPacket deserialized) {
		this.packets.addLast(deserialized);
	}

}