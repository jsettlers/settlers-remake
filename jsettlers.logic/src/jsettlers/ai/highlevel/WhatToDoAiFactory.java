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
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.ai.economy.LooserEconomyMinister;
import jsettlers.ai.economy.WinnerEconomyMinister;
import jsettlers.common.ai.EWhatToDoAiType;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.player.Player;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * @author codingberlin
 */
public class WhatToDoAiFactory {

	public IWhatToDoAi buildWhatToDoAi(EWhatToDoAiType type, AiStatistics aiStatistics, Player player, MainGrid mainGrid, MovableGrid movableGrid,
			ITaskScheduler
			taskScheduler) {
		ArmyGeneral general = determineArmyGeneral(type, aiStatistics, player, movableGrid, taskScheduler);
		EconomyMinister minister = determineMinister(type);
		return new WhatToDoAi(player.playerId, aiStatistics, minister, general, mainGrid, taskScheduler);
	}

	private EconomyMinister determineMinister(EWhatToDoAiType type) {
		if (type == EWhatToDoAiType.ROMAN_EASY || type == EWhatToDoAiType.ROMAN_VERY_HARD) {
			return new WinnerEconomyMinister();
		}
		return new LooserEconomyMinister();
	}

	private ArmyGeneral determineArmyGeneral(EWhatToDoAiType type, AiStatistics aiStatistics, Player player, MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		if (type == EWhatToDoAiType.ROMAN_HARD || type == EWhatToDoAiType.ROMAN_VERY_HARD) {
			return new WinnerGeneral(aiStatistics, player, movableGrid, taskScheduler);
		}
		return new LooserGeneral(aiStatistics, player, movableGrid, taskScheduler);
	}
}
