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
package jsettlers.network.server;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import jsettlers.network.server.match.Match;

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
