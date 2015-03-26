package jsettlers.logic.map.newGrid;

import jsettlers.GraphicsGridAdapter;
import jsettlers.TestUtils;
import jsettlers.common.Color;
import jsettlers.common.map.EDebugColorModes;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.synchronic.random.RandomSingleton;
import jsettlers.network.synchronic.timer.NetworkTimer;

public class TestExecutionWithDebugDisplay {

	public static void main(String args[]) throws MapLoadException, InterruptedException {
		TestUtils.setupResourcesManager();
		RandomSingleton.load(0);

		MatchConstants.clock = new NetworkTimer(true);

		MainGrid grid = MapList.getDefaultList().getMapByName("SoldierFightingTestMap").loadMainGrid(null).getMainGrid();
		MainGridDataAccessor gridAccessor = new MainGridDataAccessor(grid);

		short width = gridAccessor.getWidth();
		short height = gridAccessor.getHeight();

		final PartitionsGrid partitionsGrid = gridAccessor.getPartitionsGrid();

		TestUtils.openTestWindow(new GraphicsGridAdapter(width, height) {
			@Override
			public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
				int value = partitionsGrid.getRealPartitionIdAt(x, y);
				// int value = partitionsGrid.getPartitionIdAt(x, y);
				// int value = partitionsGrid.getTowerCountAt(x, y);
				// int value = partitionsGrid.getPlayerIdAt(x, y) + 1; // +1 to get -1 player displayed as black

				return Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);
			}
		});

		Thread.sleep(5000);
		grid.startThreads();
	}
}
