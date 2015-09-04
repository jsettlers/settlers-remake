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

import java.util.ArrayList;
import java.util.List;

import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.IClockListener;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * The AiExecutor holds all IWhatToDoAi high level KIs and executes them when the game clock notifies it.
 * 
 * @author codingberlin
 */
public class AiExecutor implements IClockListener {

	private final List<IWhatToDoAi> whatToDoAis;
	AiStatistics aiStatistics;
	private int nextExecutionTime;
	private final int TICK_TIME = 3000;

	public AiExecutor(List<Byte> aiPlayers, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		aiStatistics = new AiStatistics(mainGrid);
		this.whatToDoAis = new ArrayList<IWhatToDoAi>();
		nextExecutionTime = 0;
		for (byte playerId : aiPlayers) {
			whatToDoAis.add(new RomanWhatToDoAi(playerId, aiStatistics, mainGrid, taskScheduler));
		}
	}

	@Override
	public void notify(int time) {
		if (nextExecutionTime <= time) {
			aiStatistics.updateStatistics();
			for (IWhatToDoAi whatToDoAi : whatToDoAis) {
				whatToDoAi.applyRules();
			}
			nextExecutionTime = nextExecutionTime + TICK_TIME;
		}
	}
}
