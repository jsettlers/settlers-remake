package jsettlers.logic.map.newGrid.partition;

import java.util.BitSet;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This class implements a Thread that periodically checks if there are to much merged partitions on the grid. Merged partitions aren't needed an so,
 * this thread normalizes the grid by replacing the real partitions with their representatives.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsGridNormalizerThread extends Thread {
	private static final int MERGED_PARTITIONS_THRESHOLD = 700;
	private static final int CHECK_DELAY_MS = 1000;

	private final PartitionsGrid grid;
	private final Object lock;
	private boolean running = true;

	/**
	 * This constructor creates a new {@link PartitionsGridNormalizerThread} that's working on the given PartitionsGrid and uses the given object as
	 * synchronization lock when writing the partitions or the partition objects.
	 * 
	 * @param grid
	 * @param lock
	 */
	PartitionsGridNormalizerThread(PartitionsGrid grid, Object lock) {
		super("PartitionsGridNormalizer");
		this.grid = grid;
		this.lock = lock;

		super.setDaemon(true);
	}

	public void cancel() {
		running = false;
		this.interrupt();
	}

	@Override
	public void run() {
		int width = grid.width;
		int height = grid.height;

		MilliStopWatch milliWatch = new MilliStopWatch();

		while (running) {
			try {
				Thread.sleep(CHECK_DELAY_MS);
			} catch (InterruptedException e) {
			}

			milliWatch.restart();

			int maxPartitions = grid.partitionRepresentatives.length;
			BitSet stoppedManagers = new BitSet(maxPartitions);

			int counter = 0;

			for (int i = 1; i < maxPartitions; i++) {
				PartitionManager partitionObject = grid.partitionObjects[i];

				if (partitionObject != null && grid.partitionRepresentatives[i] != i) {
					stoppedManagers.set(i);
					counter++;
				}
			}

			if (counter <= MERGED_PARTITIONS_THRESHOLD) {
				continue;// skip the rest if nothing is to do.
			}

			// normalize the partitions
			for (int y = 0; y < height; y++) {
				synchronized (lock) { // the lock is acquired here to prevent holding it for a long time without requesting it every time
					for (int x = 0; x < width; x++) {
						int idx = x + y * width;
						grid.partitions[idx] = grid.partitionRepresentatives[grid.partitions[idx]];
					}
				}
			}

			// clear the partition objects
			synchronized (lock) {
				for (int i = 1; i < maxPartitions; i++) {
					if (stoppedManagers.get(i)) {
						grid.partitionObjects[i] = null;
					}
				}
			}

			System.out.println("PartitionsGridNormalizerThread: NORMALIZED " + counter + " partitions in " + milliWatch.getDiff()
					+ "ms!-----------------------------------");
		}
	}

}
