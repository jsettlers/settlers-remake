package jsettlers.algorithms.partitions;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import jsettlers.GraphicsGridAdapter;
import jsettlers.TestUtils;
import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.common.Color;
import jsettlers.common.map.EDebugColorModes;

import org.junit.Test;

public class PartitionsCalculatorAlgorithmTest {

	private static final int HEIGHT = 100;
	private static final int WIDTH = 100;

	@Test
	public void testSqaureWithBlockingLine() {
		BitSet containing = new BitSet(HEIGHT * WIDTH);
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				if (x > 0 && y > 0 && x < WIDTH - 1 && y < HEIGHT - 1)
					containing.set(x + y * WIDTH);
			}
		}

		final IBlockingProvider blockingProvider = new IBlockingProvider() {
			@Override
			public boolean isBlocked(int x, int y) {
				return x == y;
			}
		};

		final PartitionCalculatorAlgorithm algo = new PartitionCalculatorAlgorithm(0, 0, WIDTH, HEIGHT, containing, blockingProvider);
		algo.calculatePartitions();

		// visualizeAlgoResult(blockingProvider, algo);

		assertEquals(PartitionCalculatorAlgorithm.NUMBER_OF_RESERVED_PARTITIONS + 2, algo.getNumberOfPartitions());
	}

	@SuppressWarnings("unused")
	private void visualizeAlgoResult(final IBlockingProvider blockingProvider, final PartitionCalculatorAlgorithm algo) {
		TestUtils.openTestWindow(new GraphicsGridAdapter(WIDTH, HEIGHT) {
			@Override
			public int getDebugColorAt(int x, int y, EDebugColorModes debugColorMode) {
				int value;

				value = algo.getPartitionAt(x, y) + 1;

				// boolean isBlocked = blockingProvider.isBlocked(x, y);
				boolean isBlocked = false;

				return isBlocked ? Color.BLACK.getABGR() : Color
						.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);
			}
		});

		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
