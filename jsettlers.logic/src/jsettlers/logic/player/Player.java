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
package jsettlers.logic.player;

import java.io.Serializable;

import jsettlers.graphics.map.IMessenger;
import jsettlers.graphics.messages.Message;

/**
 * This class represents a player in the game. It can be used to access player specific statistics and methods.
 * 
 * @author Andreas Eberle
 * 
 */
public class Player implements Serializable, IMessenger {
	private static final long serialVersionUID = 1L;

	public final byte playerId;
	private final Team team;

	private transient IMessenger messenger;

	public Player(byte playerId, Team team) {
		this.playerId = playerId;
		this.team = team;
		team.registerPlayer(this);
	}

	@Override
	public String toString() {
		return "Player " + playerId + " of team " + team.getTeamId();
	}

	public void setMessenger(IMessenger messenger) {
		this.messenger = messenger;
	}

	@Override
	public void showMessage(Message message) {
		if (messenger != null) {
			messenger.showMessage(message);
		}
	}
}
