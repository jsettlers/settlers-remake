package jsettlers.algorithms.partitions;

import java.io.File;
import java.util.BitSet;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.swing.SwingResourceLoader;
import jsettlers.graphics.swing.SwingResourceProvider;
import jsettlers.logic.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.newGrid.MainGridDataAccessor;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
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
		MainGrid grid = new MapLoader(new File(MapList.getDefaultFolder(), "bigmap.map")).getMainGrid();
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

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(0, 0, width, height, notBlockingSet, IBlockingProvider.DEFAULT_IMPLEMENTATION);
		partitioner.calculatePartitions();
		System.out.println("\n\n\n\nnumber of partitions: " + partitioner.getNumberOfPartitions());

		watch.stop("partitioning test needed:");

		System.exit(0);
	}
}
