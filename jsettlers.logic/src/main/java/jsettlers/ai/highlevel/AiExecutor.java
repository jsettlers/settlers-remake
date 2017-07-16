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
package jsettlers.ai.highlevel;

import static java8.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.logging.StatisticsStopWatch;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.network.client.interfaces.ITaskScheduler;
import jsettlers.network.synchronic.timer.INetworkTimerable;

/**
 * The AiExecutor holds all IWhatToDoAi high level KIs and executes them when NetworkTimer notifies it.
 * 
 * @author codingberlin
 */
public class AiExecutor implements INetworkTimerable {

	private final List<IWhatToDoAi> whatToDoAis;
	private final AiStatistics aiStatistics;
	private final StatisticsStopWatch updateStatisticsStopWatch = new StatisticsStopWatch();
	private final StatisticsStopWatch applyRulesStopWatch = new StatisticsStopWatch();

	public AiExecutor(PlayerSetting[] playerSettings, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		aiStatistics = new AiStatistics(mainGrid);
		aiStatistics.updateStatistics();
		this.whatToDoAis = new ArrayList<>();
		WhatToDoAiFactory aiFactory = new WhatToDoAiFactory();
		for (byte playerId = 0; playerId < playerSettings.length; playerId++) {
			PlayerSetting playerSetting = playerSettings[playerId];
			if (playerSetting.isAvailable() && playerSetting.getPlayerType().isAi()) {
				whatToDoAis.add(aiFactory.buildWhatToDoAi(
						playerSettings[playerId].getPlayerType(),
						playerSettings[playerId].getCivilisation(),
						aiStatistics,
						mainGrid.getPartitionsGrid().getPlayer(playerId),
						mainGrid,
						mainGrid.getMovableGrid(),
						taskScheduler));
			}
		}
	}

	@Override
	public void timerEvent() {
		updateStatisticsStopWatch.restart();
		aiStatistics.updateStatistics();
		updateStatisticsStopWatch.stop("computerplayer:updateStatistics()");
		applyRulesStopWatch.restart();
		stream(whatToDoAis).forEach(IWhatToDoAi::applyRules);
		applyRulesStopWatch.stop("computerplayer:applyRules()");
	}

	public StatisticsStopWatch getUpdateStatisticsStopWatch() {
		return updateStatisticsStopWatch;
	}

	public StatisticsStopWatch getApplyRulesStopWatch() {
		return applyRulesStopWatch;
	}
}
