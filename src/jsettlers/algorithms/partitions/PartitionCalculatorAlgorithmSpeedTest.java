package jsettlers.algorithms.partitions;

import java.io.File;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.newGrid.MainGridDataAccessor;
import jsettlers.logic.map.newGrid.flags.FlagsGridDataAccessor;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import random.RandomSingleton;

public class PartitionCalculatorAlgorithmSpeedTest {

	static { // sets the native library path for the system dependent jogl libs
		SwingResourceLoader.setupSwingPaths();
		ResourceManager.setProvider(new SwingResourceProvider());
		RandomSingleton.load(0);
	}

	public static void main(String[] args) throws MapLoadException, InterruptedException {
		MainGrid grid = new MapLoader(new File(MapList.getDefaultFolder(), "bigmap-2012-04-17_09-26-33.map")).getMainGrid();
		MainGridDataAccessor gridAccessor = new MainGridDataAccessor(grid);
		FlagsGridDataAccessor flagsAccessor = new FlagsGridDataAccessor(gridAccessor.getFlagsGrid());

		Thread.sleep(500);

		MilliStopWatch watch = new MilliStopWatch();

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(0, 0, gridAccessor.getWidth(), gridAccessor.getHeight(),
				flagsAccessor.getBlockedGrid(), true);
		partitioner.calculatePartitions();
		System.out.println("number of partitions: " + partitioner.getNumberOfPartitions());

		watch.stop("partitioning test needed:");

		System.exit(0);
	}
}
