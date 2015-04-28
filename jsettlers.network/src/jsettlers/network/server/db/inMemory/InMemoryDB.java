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
package jsettlers.network.server.db.inMemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import jsettlers.network.server.db.IDBFacade;
import jsettlers.network.server.match.EPlayerState;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.Player;

/**
 * This class implements an in memory database.
 * 
 * @author Andreas Eberle
 * 
 */
public class InMemoryDB implements IDBFacade {

	private HashMap<String, Player> players = new HashMap<String, Player>();
	private HashMap<String, Match> matches = new HashMap<String, Match>();

	@Override
	public boolean isAcceptedPlayer(String id) {
		return true;
	}

	@Override
	public void storePlayer(Player player) {
		synchronized (players) {
			players.put(player.getId(), player);
		}
	}

	@Override
	public void removePlayer(Player player) {
		synchronized (players) {
			players.remove(player.getId());
		}
	}

	@Override
	public Match getRunningMatchOf(Player player) {
		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.isRunning() && curr.hasPlayer(player)) {
					return curr;
				}
			}
		}

		return null;
	}

	@Override
	public List<Match> getJoinableMatches() {
		List<Match> result = new LinkedList<Match>();

		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.canJoin()) {
					result.add(curr);
				}
			}
		}

		return result;
	}

	@Override
	public List<Match> getJoinableRunningMatches(Player player) {
		List<Match> result = new LinkedList<Match>();

		synchronized (matches) {
			for (Match curr : matches.values()) {
				if (curr.isRunning() && curr.hasLeftPlayer(player.getId())) {
					result.add(curr);
				}
			}
		}

		return result;
	}

	@Override
	public void storeMatch(Match match) {
		matches.put(match.getId(), match);
	}

	public int getNumberOfPlayers() {
		return players.size();
	}

	public Player getPlayer(String id) {
		return players.get(id);
	}

	public int getNumberOfMatches() {
		return matches.size();
	}

	@Override
	public Match getMatchById(String id) {
		return matches.get(id);
	}

	@Override
	public List<Player> getPlayers(EPlayerState... allowedStates) {
		synchronized (players) {
			List<Player> result = new LinkedList<Player>();
			for (Player curr : players.values()) {
				if (EPlayerState.isOneOf(curr.getState(), allowedStates)) {
					result.add(curr);
				}
			}
			return result;
		}
	}

	@Override
	public List<Match> getMatches() {
		List<Match> matchesList = new ArrayList<Match>();
		synchronized (matches) {
			for (Entry<String, Match> matchEntry : matches.entrySet()) {
				matchesList.add(matchEntry.getValue());
			}
		}
		return matchesList;
	}
}
