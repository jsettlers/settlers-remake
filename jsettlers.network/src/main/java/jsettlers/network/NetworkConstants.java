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
package jsettlers.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import jsettlers.network.synchronic.timer.NetworkTimer;

/**
 * This class contains constants of network library.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NetworkConstants {
	private NetworkConstants() {
	}

	public static final boolean USE_DELAYED_SOCKETS = false;
	public static final int RTT_LOGGING_THRESHOLD = 800;
	public static final int JITTER_LOGGING_THRESHOLD = 200;

	/**
	 * This class contains constants used by the Server part of network library.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public final static class Server {
		private Server() {
		}

		// public static final int SERVER_PORT = 10213; // VERSION 1
		public static final int SERVER_PORT = 10214; // VERSION 2

		public static final int BROADCAST_PORT = 10233;
		public static final String BROADCAST_MESSAGE = "JSETTLERS-LAN-SERVER-BROADCAST-V1";
		public static final int BROADCAST_BUFFER_LENGTH = BROADCAST_MESSAGE.length();

		public static final long OPEN_MATCHES_SEND_INTERVAL_MS = 5 * 1000;
	}

	public final static class Client {
		private Client() {
		}

		public static long TIME_SYNC_SEND_INTERVALL = 100;
		/**
		 * The tolerated time difference between two clients.<br>
		 * NOTE: This must be higher than {@link NetworkTimer}.TIME_SLICE!
		 */
		public static int TIME_SYNC_TOLERATED_DIFFERENCE = 100;
		public static float TIME_SYNC_APPROACH_FACTOR = 0.7f;

		/**
		 * The number of milliseconds between a lockstep event.
		 */
		public static int LOCKSTEP_PERIOD = 100;
		/**
		 * The number of steps the server can run ahead of the clients.
		 */
		public static int LOCKSTEP_DEFAULT_LEAD_STEPS = 3;
	}

	/**
	 * This class contains constants used to represent messages used in network library. These constants can be used for internationalization.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public enum ENetworkMessage {

		PLAYER_JOINED,
		PLAYER_LEFT,
		NO_LISTENER_FOUND,
		NOT_ALL_PLAYERS_READY,
		READY_STATE_CHANGED,
		START_FINISHED,

		UNAUTHORIZED,
		UNKNOWN_ERROR,
		INVALID_STATE_ERROR,

		;

		private static final ENetworkMessage[] values = ENetworkMessage.values();
		private final byte ordinal;

		ENetworkMessage() {
			this.ordinal = (byte) ordinal();
		}

		public void writeTo(DataOutputStream dos) throws IOException {
			dos.writeByte(ordinal);
		}

		public static ENetworkMessage readFrom(DataInputStream dis) throws IOException {
			try {
				return values[dis.readByte()];
			} catch (Exception ex) {
				throw new IOException(ex);
			}
		}
	}

	/**
	 * This class contains the Keys (i.e. message identifiers) used in the client server communication.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public enum ENetworkKey {

		PING,

		SYNCHRONOUS_TASK,
		IDENTIFY_USER,
		REJECT_PACKET,
		TEST_PACKET,
		ARRAY_OF_MATCHES,
		MATCH_STARTED,

		REQUEST_OPEN_NEW_MATCH,
		REQUEST_LEAVE_MATCH,
		REQUEST_JOIN_MATCH,
		REQUEST_START_MATCH,

		CHANGE_READY_STATE,
		MATCH_INFO_UPDATE,
		CHAT_MESSAGE,
		TIME_SYNC,

		CHANGE_START_FINISHED;

		private static final ENetworkKey[] values = ENetworkKey.values();
		private final byte ordinal;

		ENetworkKey() {
			this.ordinal = (byte) ordinal();
		}

		public void writeTo(DataOutputStream dos) throws IOException {
			dos.writeByte(ordinal);
		}

		public static ENetworkKey readFrom(DataInputStream dis) throws IOException {
			try {
				return values[dis.readByte()];
			} catch (Exception ex) {
				throw new IOException(ex);
			}
		}
	}
}
