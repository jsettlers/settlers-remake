/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import static java8.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jsettlers.network.server.db.IDBFacade;
import jsettlers.network.server.match.EPlayerState;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.Player;

import java8.util.stream.Collectors;

/**
 * This class implements an in memory database.
 * 
 * @author Andreas Eberle
 * 
 */
public class InMemoryDB implements IDBFacade {

	private final HashMap<String, Player> players = new HashMap<>();
	private final HashMap<String, Match> matches = new HashMap<>();

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
		synchronized (matches) {
			return stream(matches.values()).collect(Collectors.toList());
		}
	}

	@Override
	public List<Match> getJoinableRunningMatches(Player player) {
		synchronized (matches) {
			String playerId = player.getId();
			return stream(matches.values()).filter(Match::isRunning).filter(match -> match.hasLeftPlayer(playerId)).collect(Collectors.toList());
		}
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
			return stream(players.values()).filter(player -> EPlayerState.isOneOf(player.getState(), allowedStates)).collect(Collectors.toList());
		}
	}

	@Override
	public List<Match> getMatches() {
		synchronized (matches) {
			return new ArrayList<>(matches.values());
		}
	}
}
