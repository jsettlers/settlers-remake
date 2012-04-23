package jsettlers.network.webserver;

public final class SimpleWebserverTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleWebServer webserver = new SimpleWebServer(10001);
		webserver.setDaemon(false);
		webserver.start();

		webserver.addHandler(new TestHandler());
	}

}
