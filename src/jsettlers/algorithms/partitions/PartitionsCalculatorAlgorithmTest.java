package jsettlers.algorithms.partitions;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import jsettlers.GraphicsGridAdapter;
import jsettlers.TestWindow;
import jsettlers.common.Color;
import jsettlers.logic.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.algorithms.partitions.PartitionCalculatorAlgorithm;

import org.junit.Test;

public class PartitionsCalculatorAlgorithmTest {

	private static final int HEIGHT = 30;
	private static final int WIDTH = 30;

	@Test
	public void test() {
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

		assertEquals(2, algo.getNumberOfPartitions());
	}

	@SuppressWarnings("unused")
	private void visualizeAlgoResult(final IBlockingProvider blockingProvider, final PartitionCalculatorAlgorithm algo) {
		TestWindow.openTestWindow(new GraphicsGridAdapter(WIDTH, HEIGHT) {
			@Override
			public int getDebugColorAt(int x, int y) {
				int value;

				value = algo.getPartitionAt(x, y) + 1;

				return blockingProvider.isBlocked(x, y) ? Color.BLACK.getABGR() : Color.getARGB((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f,
						((value / 9) % 3) * 0.33f, 1);
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
