package networklib;

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
	}

	public final static class Client {
		private Client() {
		}

		public static final long TIME_SYNC_SEND_INTERVALL = 100;
		public static final int TIME_SYNC_TOLERATED_DIFFERENCE = 50;
		public static final float TIME_SYNC_APPROACH_FACTOR = 0.95f;

		/**
		 * The number of milliseconds between a lockstep event.
		 */
		public static final int LOCKSTEP_PERIOD = 100;
		/**
		 * The number of steps the server can run ahead of the clients.
		 */
		public static final int LOCKSTEP_DEFAULT_LEAD_STEPS = 5;
	}

	/**
	 * This class contains constants used to represent messages used in networklib. These constants can be used for internationalization.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public final static class Messages {
		private Messages() {
		}

		public static final int PLAYER_JOINED = 1;
		public static final int PLAYER_LEFT = 2;

		public static final int UNAUTHORIZED = -1;
		public static final int UNKNOWN_ERROR = -2;
		public static final int INVALID_STATE_ERROR = -3;
		public static final int NO_LISTENER_FOUND = -4;
		public static final int NOT_ALL_PLAYERS_READY = -5;

	}

	/**
	 * This class contains the Keys (i.e. message identifiers) used in the client server communication.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public final static class Keys {
		private Keys() {
		}

		public static final int PING = -1;

		public static final int SYNCHRONOUS_TASK = -2;

		public static final int IDENTIFY_USER = -3;

		public static final int REJECT_PACKET = -4;

		public static final int TEST_PACKET = -7;

		public static final int ARRAY_OF_MATCHES = -10;

		public static final int MATCH_STARTED = -19;

		public static final int REQUEST_MATCHES = -14;
		public static final int REQUEST_PLAYERS_RUNNING_MATCHES = -15;
		public static final int REQUEST_OPEN_NEW_MATCH = -16;
		public static final int REQUEST_LEAVE_MATCH = -17;
		public static final int REQUEST_JOIN_MATCH = -18;
		public static final int REQUEST_START_MATCH = -20;
		public static final int READY_STATE_CHANGE = -24;

		public static final int MATCH_INFO_UPDATE = -21;

		public static final int CHAT_MESSAGE = -22;

		public static final int TIME_SYNC = -23;

	}
}
