package jsettlers.algorithms.partitions;

import java.util.BitSet;

import jsettlers.TestUtils;
import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.newGrid.MainGridDataAccessor;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.save.MapList;
import jsettlers.network.synchronic.random.RandomSingleton;
import jsettlers.network.synchronic.timer.NetworkTimer;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionCalculatorAlgorithmSpeedTest {

	public static void main(String[] args) throws MapLoadException, InterruptedException {
		TestUtils.setupSwingResources();
		RandomSingleton.load(0);

		MatchConstants.clock = new NetworkTimer(true);

		MainGrid grid = MapList.getDefaultList().getMapByName("big map").loadMainGrid(null).getMainGrid();
		MainGridDataAccessor gridAccessor = new MainGridDataAccessor(grid);

		short width = gridAccessor.getWidth();
		short height = gridAccessor.getHeight();
		BitSet notBlockingSet = new BitSet(width * height);
		LandscapeGrid landscapeGrid = gridAccessor.getLandscapeGrid();

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				notBlockingSet.set(x + y * width, !landscapeGrid.getLandscapeTypeAt(x, y).isBlocking);
			}
		}

		Thread.sleep(500);

		MilliStopWatch watch = new MilliStopWatch();

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(0, 0, width, height, notBlockingSet,
				IBlockingProvider.DEFAULT_IMPLEMENTATION);
		partitioner.calculatePartitions();
		System.out.println("\n\n\n\nnumber of partitions: " + partitioner.getNumberOfPartitions());

		watch.stop("partitioning test needed:");

		System.exit(0);
	}
}
