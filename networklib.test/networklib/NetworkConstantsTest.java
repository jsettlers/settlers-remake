package networklib;

import static org.junit.Assert.assertEquals;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import networklib.NetworkConstants.ENetworkKey;
import networklib.NetworkConstants.ENetworkMessage;

import org.junit.Test;

/**
 * This class tests the {@link NetworkConstants} class.
 * 
 * @author Andreas Eberle
 * 
 */
public class NetworkConstantsTest {

	private final DataInputStream in;
	private final DataOutputStream out;

	public NetworkConstantsTest() throws IOException {
		PipedInputStream in = new PipedInputStream();
		PipedOutputStream out = new PipedOutputStream(in);

		this.in = new DataInputStream(in);
		this.out = new DataOutputStream(out);
	}

	@Test
	public void testKeysSerialization() throws IOException {
		for (ENetworkKey currKey : ENetworkKey.values()) {
			currKey.writeTo(out);
			ENetworkKey readKey = ENetworkKey.readFrom(in);

			assertEquals(currKey, readKey);
		}

		assertEquals(0, in.available());
	}

	@Test
	public void testMessagesSerialization() throws IOException {
		for (ENetworkMessage currKey : ENetworkMessage.values()) {
			currKey.writeTo(out);
			ENetworkMessage readKey = ENetworkMessage.readFrom(in);

			assertEquals(currKey, readKey);
		}

		assertEquals(0, in.available());
	}
}
