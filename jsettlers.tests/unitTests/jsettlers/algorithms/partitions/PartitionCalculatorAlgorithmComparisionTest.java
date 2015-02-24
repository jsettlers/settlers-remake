package jsettlers.algorithms.partitions;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import jsettlers.TestUtils;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.newGrid.MainGridDataAccessor;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.save.MapList;
import networklib.synchronic.random.RandomSingleton;
import networklib.synchronic.timer.NetworkTimer;

import org.junit.Test;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionCalculatorAlgorithmComparisionTest {

	@Test
	public void testCompareOldAndNew() throws MapLoadException {
		TestUtils.setupResourcesManager();
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

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(0, 0, width, height, notBlockingSet,
				IBlockingProvider.DEFAULT_IMPLEMENTATION);
		partitioner.calculatePartitions();
		System.out.println("\n\n\n\nnumber of partitions: " + partitioner.getNumberOfPartitions());

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				assertEquals(gridAccessor.getLandscapeGrid().getBlockedPartitionAt(x, y), partitioner.getPartitionAt(x, y));
			}
		}
	}
}
