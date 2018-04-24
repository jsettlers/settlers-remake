/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
import jsettlers.ai.army.ConfigurableGeneral;
import jsettlers.ai.economy.BuildingListEconomyMinister;
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.player.Player;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * @author codingberlin
 */
class WhatToDoAiFactory {

	IWhatToDoAi buildWhatToDoAi(EPlayerType type, ECivilisation civilisation, AiStatistics aiStatistics, Player player, MainGrid mainGrid, MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		ArmyGeneral general = determineArmyGeneral(type, civilisation, aiStatistics, player, movableGrid, taskScheduler);
		EconomyMinister minister = determineMinister(type, civilisation, aiStatistics, player);
		return new WhatToDoAi(player.playerId, aiStatistics, minister, general, mainGrid, taskScheduler);
	}

	private EconomyMinister determineMinister(EPlayerType type, ECivilisation civilisation, AiStatistics aiStatistics, Player player) {
		switch (type) {
		case AI_VERY_EASY:
			return new BuildingListEconomyMinister(aiStatistics, player, 1F / 10F, 1F / 5F, true);
		case AI_EASY:
			return new BuildingListEconomyMinister(aiStatistics, player, 1F / 4F, 1F / 2F, false);
		case AI_HARD:
			return new BuildingListEconomyMinister(aiStatistics, player, 1F / 2F, 3F / 4F, false);
		default:
			return new BuildingListEconomyMinister(aiStatistics, player, 1F, 1F, false);
		}
	}

	private ArmyGeneral determineArmyGeneral(EPlayerType playerType, ECivilisation civilisation, AiStatistics aiStatistics, Player player, MovableGrid movableGrid, ITaskScheduler taskScheduler) {
		// TODO: use civilisation to determine different general when there is more than ROMAN
		return new ConfigurableGeneral(aiStatistics, player, movableGrid, taskScheduler, playerType);
	}
}
