package jsettlers.graphics.map;

/**
 * This class keeps track of the frames.
 * 
 * @author michael
 */
public class FramerateComputer {
	private static final long RECOMPUTE_INTERVALL = 500;
	private long[] lastFrames = new long[30];
	private long lastRecompute = 0;
	private int capturedFrames = 0;
	private double rate;

	public void nextFrame() {
		long time = System.currentTimeMillis();
		lastFrames[capturedFrames] = time;
		capturedFrames++;
		if ((time - lastRecompute > RECOMPUTE_INTERVALL || capturedFrames >= lastFrames.length)
		        && capturedFrames > 1) {
			recompute();
			lastRecompute = time;
		}
	}

	private void recompute() {
		long time = lastFrames[capturedFrames - 1] - lastFrames[0];
		rate = 1000.0 / time * capturedFrames;
		lastFrames[0] = lastFrames[capturedFrames - 1];
		capturedFrames = 1;
	}

	public double getRate() {
		return rate;
	}
}
