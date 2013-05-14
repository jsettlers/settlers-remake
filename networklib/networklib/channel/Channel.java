package networklib.channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import networklib.NetworkConstants;
import networklib.channel.packet.Packet;
import networklib.channel.ping.IRoundTripTimeSupplier;
import networklib.channel.ping.PingPacket;
import networklib.channel.ping.PingPacketListener;
import networklib.channel.ping.RoundTripTime;
import networklib.channel.reject.RejectPacket;

/**
 * This class builds up a logical channel between to network partners. The class allows to send data of type {@link Packet} to the partner and to
 * register {@link IChannelListener}s to receive incoming data as a callback.
 * 
 * @author Andreas Eberle
 * 
 */
public class Channel implements Runnable, IRoundTripTimeSupplier {
	private final Thread thread;

	private final Socket socket;
	private final DataOutputStream outStream;
	private final DataInputStream inStream;

	private final ByteArrayOutputStream byteBufferOutStream = new ByteArrayOutputStream();
	private final DataOutputStream bufferDataOutStream = new DataOutputStream(byteBufferOutStream);

	private final HashMap<Integer, IChannelListener> listenerRegistry = new HashMap<Integer, IChannelListener>();

	private final PingPacketListener pingListener;

	private IChannelClosedListener channelClosedListener;

	/**
	 * Creates a new Channel with the given socket as the underlying communication method.
	 * 
	 * @param socket
	 *            The socket to be used for communication.
	 * @throws IOException
	 *             If an I/O error occurs when creating the channel or if the socket is not connected.
	 */
	public Channel(Socket socket) throws IOException {
		this.socket = socket;
		outStream = new DataOutputStream(socket.getOutputStream());
		inStream = new DataInputStream(socket.getInputStream());

		pingListener = new PingPacketListener(this);
		registerListener(pingListener);

		thread = new Thread(this, "ChannelForSocket_" + socket);
	}

	public Channel(String host, int port) throws UnknownHostException, IOException {
		this(new Socket(host, port));
	}

	/**
	 * Starts the message receiving of this {@link Channel}.
	 * <p />
	 * NOTE: This method may only be called once!
	 * 
	 * @throws IllegalThreadStateException
	 *             If the thread was already started.
	 * 
	 * @see <code>Thread.start()</code>
	 */
	public void start() {
		thread.start();
	}

	public synchronized void sendPacket(int key, Packet packet) {
		if (socket.isClosed())
			return;

		try {
			sendPacketData(key, packet);
		} catch (IOException e) {
		}
	}

	private void sendPacketData(int key, Packet packet) throws IOException {
		bufferDataOutStream.flush();
		byteBufferOutStream.reset();

		packet.serialize(bufferDataOutStream); // write packet to buffer to calculate length
		bufferDataOutStream.flush();
		final int length = byteBufferOutStream.size();

		outStream.writeInt(key); // write key, length and the data
		outStream.writeInt(length);
		byteBufferOutStream.writeTo(outStream);

		outStream.flush();
	}

	/**
	 * Registers the given listener to receive data of the type it specifys with it's getKeys() method.
	 * 
	 * @param listener
	 *            The listener that shall be registered.
	 */
	public void registerListener(IChannelListener listener) {
		int[] keys = listener.getKeys();
		for (int i = 0; i < keys.length; i++) {
			listenerRegistry.put(keys[i], listener);
		}
	}

	public void removeListener(int key) {
		listenerRegistry.remove(key);
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				int key = inStream.readInt();
				int length = inStream.readInt();

				DataInputStream bufferIn = readBytesToBuffer(inStream, length);

				IChannelListener listener = listenerRegistry.get(key);

				if (listener != null) {
					try {
						listener.receive(key, length, bufferIn);
						if (bufferIn.available() > 0) {
							System.err.println("WARNING: Deserialization did not read all bytes of input: " + key + " " + length + " "
									+ bufferIn.available());
						}
					} catch (Exception e) { // ignore exceptions thrown in receive
						e.printStackTrace();
					}
				} else {
					System.err.println("WARNING: NO LISTENER FOUND for key: " + key + "   (" + socket + ")");

					if (key != NetworkConstants.Keys.REJECT_PACKET) { // prevent endless loop
						sendPacket(NetworkConstants.Keys.REJECT_PACKET, new RejectPacket(NetworkConstants.Messages.NO_LISTENER_FOUND, key));
					}
				}

			} catch (Exception e) {
				try {
					socket.close();
				} catch (IOException ex) {
				}
			}
		}

		close(); // release the resources

		if (channelClosedListener != null) {
			channelClosedListener.channelClosed();
		}
		System.out.println("Channel listener shut down: " + socket);
	}

	private DataInputStream readBytesToBuffer(DataInputStream inStream, int length) throws IOException {
		byte[] data = new byte[length];

		int alreadyRead = 0;
		while (length - alreadyRead > 0) {
			int numberOfBytesRead = inStream.read(data, alreadyRead, length - alreadyRead);
			if (numberOfBytesRead < 0) {
				throw new IOException("Stream ended to early!");
			}

			alreadyRead += numberOfBytesRead;
		}

		return new DataInputStream(new ByteArrayInputStream(data));
	}

	/**
	 * Closes this {@link Channel} and releases the contained {@link Socket} and the stream resources.
	 */
	public void close() {
		try {
			inStream.close();
		} catch (IOException e1) {
		}

		try {
			outStream.close();
		} catch (IOException e1) {
		}

		try {
			socket.close();
		} catch (IOException e) {
		}

		thread.interrupt();
	}

	/**
	 * Gets the round trip time of this {@link Channel}.
	 * 
	 * @return Returns the current round trip time of this {@link Channel}.
	 */
	@Override
	public RoundTripTime getRoundTripTime() {
		return pingListener.getRoundTripTime();
	}

	/**
	 * Initialize the pinging by sending a first {@link PingPacket}.
	 */
	public void initPinging() {
		pingListener.initPinging();
	}

	/**
	 * Sets an {@link IChannelClosedListener} to this {@link Channel}. The given listener will be informed when the {@link Channel} has been shut
	 * down.
	 * <p />
	 * NOTE: To remove a listener, just call this method with <code>null</code> as argument.<br>
	 * NOTE2: Only one listener may be registered at a time. By setting a new listener, the old one will be replaced.
	 * 
	 * @param channelClosedListener
	 *            The new {@link IChannelClosedListener} that shall be registered on this {@link Channel}.
	 */
	public void setChannelClosedListener(IChannelClosedListener channelClosedListener) {
		this.channelClosedListener = channelClosedListener;
	}

	public boolean isClosed() {
		return socket.isClosed();
	}
}
