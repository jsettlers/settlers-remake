package networklib.infrastructure.log;

public class Log {
	public static synchronized void log(String s) {
		System.out.println(ServerTime.getTime() + ": " + s);
	}

	public static synchronized void error(String s) {
		System.err.println(ServerTime.getTime() + ": " + s);
	}
}
