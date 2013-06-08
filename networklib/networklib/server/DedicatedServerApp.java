package networklib.server;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class starts a dedicated server.
 * 
 * @author Andreas Eberle
 * 
 */
public class DedicatedServerApp {

	public static void main(String args[]) throws IOException {
		GameServerThread gameServer = new GameServerThread(false);
		gameServer.start();

		Scanner s = new Scanner(System.in);
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if ("exit".equalsIgnoreCase(line)) {
				break;
			} else if ("listMatches".equalsIgnoreCase(line)) {

			}
		}
	}
}
