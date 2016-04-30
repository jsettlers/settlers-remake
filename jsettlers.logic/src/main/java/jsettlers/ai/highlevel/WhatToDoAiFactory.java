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
package jsettlers.ai.highlevel;

import jsettlers.ai.army.ArmyGeneral;
import jsettlers.ai.army.LooserGeneral;
import jsettlers.ai.army.WinnerGeneral;
import jsettlers.ai.economy.AdaptableEconomyMinister;
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.ai.economy.MiddleEconomyMinister;
import jsettlers.ai.economy.WinnerEconomyMinister;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.player.Player;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * @author codingberlin
 */
public class WhatToDoAiFactory {

	public IWhatToDoAi buildWhatToDoAi(EPlayerType type, ECivilisation civilisation, AiStatistics aiStatistics, Player player, MainGrid mainGrid,
			MovableGrid movableGrid,
			ITaskScheduler
					taskScheduler, AiMapInformation aiMapInformation) {
		ArmyGeneral general = determineArmyGeneral(type, civilisation, aiStatistics, player, movableGrid, taskScheduler);
		EconomyMinister minister = determineMinister(type, civilisation, aiStatistics, player, aiMapInformation);
		return new WhatToDoAi(player.playerId, aiStatistics, minister, general, mainGrid, taskScheduler);
	}

	private EconomyMinister determineMinister(
			EPlayerType type, ECivilisation civilisation, AiStatistics aiStatistics, Player player, AiMapInformation aiMapInformation) {
		if (type == EPlayerType.AI_VERY_EASY) {
			return new AdaptableEconomyMinister(aiStatistics, player);
		} else if (type == EPlayerType.AI_VERY_HARD) {
			return new WinnerEconomyMinister(aiMapInformation);
		}
		return new MiddleEconomyMinister(aiMapInformation);
	}

	private ArmyGeneral determineArmyGeneral(EPlayerType type, ECivilisation civilisation, AiStatistics aiStatistics, Player player,
			MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		//TODO: use civilisation to determine different general when there is more than ROMAN
		if (type == EPlayerType.AI_HARD || type == EPlayerType.AI_VERY_HARD) {
			return new WinnerGeneral(aiStatistics, player, movableGrid, taskScheduler);
		}
		return new LooserGeneral(aiStatistics, player, movableGrid, taskScheduler);
	}
}
