package networklib.infrastructure.channel;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import networklib.infrastructure.channel.packet.Packet;

/**
 * This is a {@link Channel} implementation with asynchronous sending. The packets that shall be send, will be buffered and send by an extra thread.
 * 
 * @author Andreas Eberle
 * 
 */
public class AsyncChannel extends Channel {

	private final LinkedBlockingQueue<PacketWithKey> sendBuffer = new LinkedBlockingQueue<PacketWithKey>();
	private final Thread senderThread;

	public AsyncChannel(String host, int port) throws UnknownHostException, IOException {
		super(host, port);
		senderThread = createSenderThread(host + ":" + port);
	}

	public AsyncChannel(Socket socket) throws IOException {
		super(socket);
		senderThread = createSenderThread(socket.toString());
	}

	private Thread createSenderThread(String identifier) {
		return new Thread("AsyncChannelSenderThread(" + identifier + ")") {
			@Override
			public void run() {
				while (!isClosed()) {
					PacketWithKey packetWithKey = null;
					try {
						packetWithKey = sendBuffer.take();
					} catch (InterruptedException e) {
					}

					if (packetWithKey != null) {
						sendPacket(packetWithKey.key, packetWithKey.packet);
					}
				}
			}
		};
	}

	/**
	 * Starts the message receiving and sending of this {@link AsnycChannel}.
	 * <p />
	 * NOTE: This method may only be called once!
	 * 
	 * @throws IllegalThreadStateException
	 *             If the thread was already started.
	 * 
	 * @see <code>Thread.start()</code>
	 */
	@Override
	public void start() {
		super.start();
		senderThread.start();
	}

	/**
	 * Sends the given packet asynchronously.
	 * 
	 * @param packet
	 *            Packet to be sent.
	 */
	public synchronized void sendPacketAsync(int key, Packet packet) {
		sendBuffer.offer(new PacketWithKey(key, packet));
	}

	@Override
	public void close() {
		super.close();
		senderThread.interrupt();
	}

	private class PacketWithKey {
		final int key;
		final Packet packet;

		PacketWithKey(int key, Packet packet) {
			this.key = key;
			this.packet = packet;
		}
	}
}
