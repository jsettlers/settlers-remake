package networklib;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import networklib.synchronic.timer.NetworkTimer;

/**
 * This class contains constants of networklib.
 * 
 * @author Andreas Eberle
 * 
 */
public final class NetworkConstants {
	private NetworkConstants() {
	}

	/**
	 * This class contains constants used by the Server part of networklib.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public final static class Server {
		private Server() {
		}

		public static final int SERVER_PORT = 10213;

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
	 * This class contains constants used to represent messages used in networklib. These constants can be used for internationalization.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public static enum ENetworkMessage {

		PLAYER_JOINED,
		PLAYER_LEFT,
		NO_LISTENER_FOUND,
		NOT_ALL_PLAYERS_READY,
		READY_STATE_CHANGED,

		UNAUTHORIZED,
		UNKNOWN_ERROR,
		INVALID_STATE_ERROR;

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
	public static enum ENetworkKey {

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
		TIME_SYNC;

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
