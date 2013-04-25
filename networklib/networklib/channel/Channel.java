package networklib.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import networklib.channel.ping.PingPacket;
import networklib.channel.ping.PingPacketListener;
import networklib.channel.ping.RoundTripTime;

/**
 * This class builds up a logical channel between to network partners. The class allows to send data of type {@link Packet} to the partner and to
 * register {@link IChannelListener}s to receive incoming data as a callback.
 * 
 * @author Andreas Eberle
 * 
 */
public class Channel implements IPacketSendable, Runnable {
	private final Thread thread;

	private final Socket socket;
	private final DataOutputStream outStream;
	private final DataInputStream inStream;

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

	@Override
	public synchronized void sendPacket(Packet packet) {
		if (socket.isClosed())
			return;

		try {
			final int key = packet.getKey();
			outStream.writeInt(key);
			packet.serialize(outStream);
			outStream.flush();

		} catch (IOException e) {
		}
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

				IChannelListener listener = listenerRegistry.get(key);

				if (listener != null) {
					listener.receive(key, inStream);
				} else {
					System.err.println("WARNING: NO LISTENER FOUND. key: " + key);
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
	 * 
	 * @see Thread.join()
	 * 
	 * @throws InterruptedException
	 */
	public void join() throws InterruptedException {
		thread.join();
	}

	/**
	 * Gets the round trip time of this {@link Channel}.
	 * 
	 * @return Returns the current round trip time of this {@link Channel}.
	 */
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

	@Override
	public boolean isClosed() {
		return socket.isClosed();
	}
}
