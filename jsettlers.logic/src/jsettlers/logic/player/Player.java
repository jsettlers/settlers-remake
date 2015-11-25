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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.graphics.map.IMessenger;
import jsettlers.graphics.messages.Message;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;
import jsettlers.logic.player.CombatStrengthCalculator.CombatStrengthInformation;

/**
 * This class represents a player in the game. It can be used to access player specific statistics and methods.
 * 
 * @author Andreas Eberle
 * 
 */
public class Player implements Serializable, IMessenger, IInGamePlayer, IOffersCountListener {
	private static final long serialVersionUID = 1L;

	public final byte playerId;
	private final Team team;
	private final byte numberOfPlayers;

	private final ManaInformation manaInformation = new ManaInformation();
	private final int[] materialCounts = new int[EMaterialType.NUMBER_OF_MATERIALS];
	private final EndgameStatistic endgameStatistic = new EndgameStatistic(manaInformation);

	private transient CombatStrengthInformation combatStrengthInfo;
	private transient IMessenger messenger;

	public Player(byte playerId, Team team, byte numberOfPlayers) {
		this.playerId = playerId;
		this.team = team;
		this.numberOfPlayers = numberOfPlayers;
		team.registerPlayer(this);
		updateCombatStrengths();
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		updateCombatStrengths();
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

	@Override
	public ManaInformation getManaInformation() {
		return manaInformation;
	}

	@Override
	public ICombatStrengthInformation getCombatStrengthInformation() {
		return this.combatStrengthInfo;
	}

	@Override
	public EndgameStatistic getEndgameStatistic() {
		return endgameStatistic;
	}

	private int getAmountOf(EMaterialType materialType) {
		return materialCounts[materialType.ordinal];
	}

	@Override
	public void offersCountChanged(EMaterialType materialType, int delta) {
		materialCounts[materialType.ordinal] += delta;
		if (materialCounts[materialType.ordinal] < 0) {
			System.err.println("Sanity check: material count cannot be negative!");
		}

		if (materialType == EMaterialType.GOLD) {
			CombatStrengthInformation combatStrength = this.combatStrengthInfo;
			updateCombatStrengths();
			System.out.println("amount of gold of player: " + playerId + "   changed by: " + delta + "    to total: "
					+ getAmountOf(EMaterialType.GOLD) + "    combat strength changed from\n\t" + combatStrength + "   to \n\t"
					+ this.combatStrengthInfo);
		}
	}

	private void updateCombatStrengths() {
		int amountOfGold = getAmountOf(EMaterialType.GOLD);
		this.combatStrengthInfo = CombatStrengthCalculator.calculateCombatStrengthInformation(numberOfPlayers, amountOfGold);
	}
}
