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

	/**
	 * This class contains constants used to represent strings used in networklib. These constants can be used for internationalization.
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	public final static class Strings {
		private Strings() {
		}

		public static final int UNAUTHORIZED = 1;
		public static final int UNKNOWN_ERROR = -1;
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

		public static final int ACKNOWLEDGE_PACKET = -5;

		public static final int MAP_INFO = -6;

		public static final int TEST = -7;

		public static final int MATCH_INFO = -8;

		public static final int PLAYER_INFO = -9;

		public static final int REQUEST_MATCHES = -10;

		public static final int LIST_OF_MATCHES = -11;

		public static final int REQUEST_PLAYERS_RUNNING_MATCHES = -12;

		public static final int REQUEST_OPEN_NEW_MATCH = -13;

		public static final int PLAYER_DISCONNECTED = -14;

		public static final int PLAYER_JOINED = -15;
	}
}
