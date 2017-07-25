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
package jsettlers.logic.player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.menu.messages.IMessage;
import jsettlers.common.menu.messages.IMessenger;
import jsettlers.common.player.ECivilisation;
import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.player.IPlayer;
import jsettlers.common.player.ISettlerInformation;
import jsettlers.logic.map.grid.partition.data.MaterialCounts;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;

/**
 * This class represents a player in the game. It can be used to access player specific statistics and methods.
 *
 * @author Andreas Eberle
 */
public class Player implements Serializable, IMessenger, IInGamePlayer, IOffersCountListener {
	private static final long serialVersionUID = 1L;

	public final byte playerId;

	private final Team team;
	private final byte numberOfPlayers;
	private final MannaInformation mannaInformation = new MannaInformation();
	private final MaterialCounts materialCounts = new MaterialCounts();
	private final EndgameStatistic endgameStatistic = new EndgameStatistic(mannaInformation);

	private transient EPlayerType playerType;
	private transient ECivilisation civilisation;
	private transient CombatStrengthInformation combatStrengthInfo = new CombatStrengthInformation();
	private transient IMessenger messenger;

	public Player(byte playerId, Team team, byte numberOfPlayers, EPlayerType playerType, ECivilisation civilisation) {
		this.playerId = playerId;
		this.team = team;
		this.numberOfPlayers = numberOfPlayers;
		this.playerType = playerType;
		this.civilisation = civilisation;
		team.registerPlayer(this);
		updateCombatStrengths();
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		combatStrengthInfo = new CombatStrengthInformation();
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
	public void showMessage(IMessage message) {
		if (messenger != null) {
			messenger.showMessage(message);
		}
	}

	@Override
	public byte getPlayerId() {
		return playerId;
	}

	@Override
	public MannaInformation getMannaInformation() {
		return mannaInformation;
	}

	@Override
	public ICombatStrengthInformation getCombatStrengthInformation() {
		return combatStrengthInfo;
	}

	@Override
	public EndgameStatistic getEndgameStatistic() {
		return endgameStatistic;
	}

	@Override
	public ISettlerInformation getSettlerInformation() {
		return new SettlerInformation(playerId);
	}

	private int getAmountOf(EMaterialType materialType) {
		return materialCounts.getAmountOf(materialType);
	}

	@Override
	public void offersCountChanged(EMaterialType materialType, int delta) {
		materialCounts.offersCountChanged(materialType, delta);

		if (materialType == EMaterialType.GOLD) {
			CombatStrengthInformation combatStrength = this.combatStrengthInfo;
			updateCombatStrengths();
			System.out.println("amount of gold of player: " + playerId + "   changed by: " + delta + "    to total: " + getAmountOf(EMaterialType.GOLD) + "    combat strength changed from\n\t" +
					combatStrength + "   to \n\t" + this.combatStrengthInfo);
		}
	}

	private void updateCombatStrengths() {
		int amountOfGold = getAmountOf(EMaterialType.GOLD);
		this.combatStrengthInfo.updateGoldCombatStrength(numberOfPlayers, amountOfGold);
	}

	public byte getTeamId() {
		return team.getTeamId();
	}

	public EPlayerType getPlayerType() {
		return playerType;
	}

	public ECivilisation getCivilisation() {
		return civilisation;
	}

	public void setPlayerType(EPlayerType playerType) {
		this.playerType = playerType;
	}

	public void setCivilisation(ECivilisation civilisation) {
		this.civilisation = civilisation;
	}

	public boolean hasSameTeam(Player player) {
		return player != null && this.team == player.team;
	}
}
