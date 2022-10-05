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

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.IEndgameStatistic;

import java.io.Serializable;

/**
 * @author codingberlin
 */
public class EndgameStatistic implements IEndgameStatistic, Serializable {
	private static final long serialVersionUID = -1352905249487671843L;

	private Player player;
	private short amountOfProducedSoldiers = 0;
	private short amountOfProducedGold = 0;

	public EndgameStatistic(Player player) {
		this.player = player;
	}

	@Override
	public short getAmountOfProducedSoldiers() {
		return amountOfProducedSoldiers;
	}

	@Override
	public short getAmountOfProducedMana() {
		return player.getMannaInformation().getAmountOfManna();
	}

	@Override
	public short getAmountOfProducedGold() {
		return amountOfProducedGold;
	}

	@Override
	public String getName() {
		return player.getPlayerType().toString();
	}

	@Override
	public byte getTeam() {
		return player.getTeamId();
	}

	public void incrementAmountOfProducedSoldiers() {
		amountOfProducedSoldiers++;
	}

	public void incrementAmountOfProducedGold() {
		amountOfProducedGold++;
	}

	@Override
	public String toString() {

		return "amountOfProducedSoldiers: " + amountOfProducedSoldiers +
				", amountOfProducedGold: " + amountOfProducedGold +
				", amountOfProducedMana: " + getAmountOfProducedMana();
	}
}
