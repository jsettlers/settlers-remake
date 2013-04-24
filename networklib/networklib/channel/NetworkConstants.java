package networklib.channel;

public class NetworkConstants {
	private NetworkConstants() {
	}

	public static class Server {
		public static final int SERVER_PORT = 10213;

		public static final int BROADCAST_PORT = 10233;
		public static final String BROADCAST_MESSAGE = "JSETTLERS-LAN-SERVER-BROADCAST-V1";
		public static final int BROADCAST_BUFFER_LENGTH = BROADCAST_MESSAGE.length();
	}

	public static class Strings {
		public static final int UNAUTHORIZED = 1;

	}

	public static class Keys {
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

		public static final int GET_MATCHES = -10;

		public static final int LIST_OF_MATCHES = -11;

		public static final int GET_PLAYERS_RUNNING_MATCHES = -12;
	}
}
