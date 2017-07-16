/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.network.infrastructure.channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import jsettlers.network.NetworkConstants;
import jsettlers.network.NetworkConstants.ENetworkKey;
import jsettlers.network.infrastructure.channel.packet.Packet;
import jsettlers.network.infrastructure.channel.ping.IPingUpdateListener;
import jsettlers.network.infrastructure.channel.ping.IRoundTripTimeSupplier;
import jsettlers.network.infrastructure.channel.ping.PingPacket;
import jsettlers.network.infrastructure.channel.ping.PingPacketListener;
import jsettlers.network.infrastructure.channel.ping.RoundTripTime;
import jsettlers.network.infrastructure.channel.reject.RejectPacket;
import jsettlers.network.infrastructure.channel.socket.ISocket;
import jsettlers.network.infrastructure.channel.socket.ISocketFactory;
import jsettlers.network.infrastructure.log.ConsoleLogger;
import jsettlers.network.infrastructure.log.Logger;
import jsettlers.network.infrastructure.log.SwitchableLogger;

/**
 * This class builds up a logical channel between to network partners. The class allows to send data of type {@link Packet} to the partner and to
 * register {@link IChannelListener}s to receive incoming data as a callback.
 * 
 * @author Andreas Eberle
 * 
 */
public class Channel implements Runnable, IRoundTripTimeSupplier {
	private final Thread thread;

	private final SwitchableLogger logger;
	private final ISocket socket;
	private final DataOutputStream outStream;
	private final DataInputStream inStream;

	private final ByteArrayOutputStream byteBufferOutStream = new ByteArrayOutputStream();
	private final DataOutputStream bufferDataOutStream = new DataOutputStream(byteBufferOutStream);

	private final HashMap<ENetworkKey, IChannelListener> listenerRegistry = new HashMap<>();

	private final PingPacketListener pingPacketListener;

	private IChannelClosedListener channelClosedListener;

	private boolean started;

	/**
	 * Creates a new Channel with the given socket as the underlying communication method.
	 * 
	 * @param socket
	 *            The socket to be used for communication.
	 * @throws IOException
	 *             If an I/O error occurs when creating the channel or if the socket is not connected.
	 */
	public Channel(ISocket socket) throws IOException {
		this(new ConsoleLogger(socket.toString()), socket);
	}

	public Channel(String host, int port) throws IOException {
		this(ISocketFactory.DEFAULT_FACTORY.generateSocket(host, port));
	}

	public Channel(Logger logger, ISocket socket) throws IOException {
		this.logger = new SwitchableLogger(logger);
		this.socket = socket;
		outStream = new DataOutputStream(socket.getOutputStream());
		inStream = new DataInputStream(socket.getInputStream());

		pingPacketListener = new PingPacketListener(this.logger, this);
		registerListener(pingPacketListener);

		thread = new Thread(this, "ChannelForSocket_" + socket);
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
		started = true;
		thread.start();
	}

	public synchronized void sendPacket(ENetworkKey key, Packet packet) {
		if (socket.isClosed())
			return;

		try {
			sendPacketData(key, packet);
		} catch (IOException e) {
		}
	}

	private void sendPacketData(ENetworkKey key, Packet packet) throws IOException {
		bufferDataOutStream.flush();
		byteBufferOutStream.reset();

		packet.serialize(bufferDataOutStream); // write packet to buffer to calculate length
		bufferDataOutStream.flush();
		final int length = byteBufferOutStream.size();

		key.writeTo(outStream); // write key, length and the data
		outStream.writeInt(length);
		byteBufferOutStream.writeTo(outStream);

		outStream.flush();
	}

	/**
	 * Registers the given listener to receive data of the type it specifies with it's getKeys() method.
	 * 
	 * @param listener
	 *            The listener that shall be registered.
	 */
	public void registerListener(IChannelListener listener) {
		ENetworkKey[] keys = listener.getKeys();
		for (int i = 0; i < keys.length; i++) {
			listenerRegistry.put(keys[i], listener);
		}
	}

	public void removeListener(ENetworkKey key) {
		listenerRegistry.remove(key);
	}

	@Override
	public void run() {
		while (!socket.isClosed()) {
			try {
				ENetworkKey key = ENetworkKey.readFrom(inStream);
				int length = inStream.readInt();

				DataInputStream bufferIn = readBytesToBuffer(inStream, length);

				IChannelListener listener = listenerRegistry.get(key);

				if (listener != null) {
					try {
						listener.receive(key, length, bufferIn);
						if (bufferIn.available() > 0) {
							logger.warn("Deserialization did not read all bytes of input: " + key + " " + length + " " + bufferIn.available());
						}
					} catch (Exception e) { // ignore exceptions thrown in receive
						e.printStackTrace();
					}
				} else {
					logger.warn("NO LISTENER FOUND for key: " + key + "   (" + socket + ")");

					if (key != NetworkConstants.ENetworkKey.REJECT_PACKET) { // prevent endless loop
						sendPacket(NetworkConstants.ENetworkKey.REJECT_PACKET,
								new RejectPacket(NetworkConstants.ENetworkMessage.NO_LISTENER_FOUND, key));
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
		logger.info("Channel listener shut down: " + socket);
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
		return pingPacketListener.getRoundTripTime();
	}

	/**
	 * Initialize the pinging by sending a first {@link PingPacket}.
	 */
	public void initPinging() {
		pingPacketListener.initPinging();
	}

	public void setPingUpdateListener(IPingUpdateListener pingUpdateListener) {
		pingPacketListener.setPingUpdateListener(pingUpdateListener);
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

	public boolean isStarted() {
		return started;
	}

	public void setLogger(Logger newLogger) {
		this.logger.setLogger(newLogger);
	}
}
