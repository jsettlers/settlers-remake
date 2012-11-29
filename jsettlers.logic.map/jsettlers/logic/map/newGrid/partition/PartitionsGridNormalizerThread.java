package jsettlers.logic.map.newGrid.partition;

import java.util.BitSet;

final class PartitionsGridNormalizerThread extends Thread {

	private final PartitionsGrid grid;
	private final Object lock;
	private boolean running = true;

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

		while (running) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}

			int maxPartitions = grid.partitionRepresentative.length;
			BitSet stoppedManagers = new BitSet(maxPartitions);

			int counter = 0;

			for (int i = 1; i < maxPartitions; i++) {
				Partition partitionObject = grid.partitionObjects[i];

				if (partitionObject != null && grid.partitionRepresentative[i] != i) {
					stoppedManagers.set(i);
					counter++;
				}
			}

			if (counter <= 400) {
				continue;// skip the rest if nothing is to do.
			}

			// normalize the partitions
			for (int y = 0; y < height; y++) {
				synchronized (lock) { // the lock is acquired here to prevent holding it for a long time without requesting it every time
					for (int x = 0; x < width; x++) {
						int idx = x + y * width;
						grid.partitions[idx] = grid.partitionRepresentative[grid.partitions[idx]];
					}
				}
			}

			synchronized (lock) { // the lock is acquired here to prevent holding it for a long time without requesting it every time
				for (int i = 1; i < maxPartitions; i++) {
					if (stoppedManagers.get(i)) {
						grid.partitionObjects[i] = null;
					}
				}
			}

			System.out.println("NORMALIZED " + counter + " partitions!-----------------------------------");
		}
	}

}
