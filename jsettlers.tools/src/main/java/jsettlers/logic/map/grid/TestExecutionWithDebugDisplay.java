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
package jsettlers.logic.map.grid;

import java.io.IOException;

import jsettlers.GraphicsGridAdapter;
import jsettlers.TestToolUtils;
import jsettlers.common.Color;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.swing.SwingManagedJSettlers;
import jsettlers.main.swing.lookandfeel.JSettlersLookAndFeelExecption;
import jsettlers.main.swing.resources.SwingResourceLoader;
import jsettlers.network.synchronic.timer.NetworkTimer;

public class TestExecutionWithDebugDisplay {

	public static void main(String args[]) throws MapLoadException, InterruptedException, JSettlersLookAndFeelExecption, IOException, SwingResourceLoader.ResourceSetupException {
		SwingManagedJSettlers.setupResources(true, args);
		MatchConstants.init(new NetworkTimer(true), 0);

		MainGrid grid = MapList.getDefaultList().getMapByName("SoldierFightingTestMap").loadMainGrid(null).getMainGrid();
		MainGridDataAccessor gridAccessor = new MainGridDataAccessor(grid);

		short width = gridAccessor.getWidth();
		short height = gridAccessor.getHeight();

		final PartitionsGrid partitionsGrid = gridAccessor.getPartitionsGrid();

		TestToolUtils.openTestWindow(new GraphicsGridAdapter(width, height) {
			@Override
			public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
				int value = partitionsGrid.getRealPartitionIdAt(x, y);
				// int value = partitionsGrid.getPartitionIdAt(x, y);
				// int value = partitionsGrid.getTowerCountAt(x, y);
				// int value = partitionsGrid.getPlayerIdAt(x, y) + 1; // +1 to get -1 player displayed as black

				return Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);
			}
		});

		Thread.sleep(5000L);
		grid.startThreads();
	}
}
