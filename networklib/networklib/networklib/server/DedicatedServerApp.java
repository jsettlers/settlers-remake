package networklib.server;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import networklib.server.match.Match;

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
				System.out.println("shutting down...");
				break;
			} else if ("listMatches".equalsIgnoreCase(line)) {
				List<Match> matches = gameServer.getDatabase().getMatches();
				System.out.println("listing matches (" + matches.size() + "):");
				for (Match match : matches) {
					System.out.println("\t" + match);
				}
			}
		}
		s.close();
	}
}
