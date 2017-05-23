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
package jsettlers.network.server.db;

import java.util.Collection;
import java.util.List;

import jsettlers.network.server.match.EPlayerState;
import jsettlers.network.server.match.Match;
import jsettlers.network.server.match.Player;

/**
 * This interface defines the methods required by the server to use a database.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDBFacade {

	/**
	 * Checks if the player is allowed to connect to the server.
	 * 
	 * @param id
	 *            The id of the player.
	 * @return Returns true if the player may connect to the server.
	 */
	boolean isAcceptedPlayer(String id);

	void storePlayer(Player player);

	void removePlayer(Player player);

	/**
	 * 
	 * @param player
	 * @return Returns the running {@link Match} of the given {@link Player} <br>
	 *         or null if the {@link Player} has no running {@link Match}.
	 */
	Match getRunningMatchOf(Player player);

	void storeMatch(Match match);

	List<Match> getJoinableMatches();

	List<Match> getJoinableRunningMatches(Player player);

	Match getMatchById(String id);

	List<Player> getPlayers(EPlayerState... allowedStates);

	List<Match> getMatches();
}
