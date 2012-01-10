package jsettlers.mapcreator.main;


import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.LandscapeFader;
import jsettlers.mapcreator.data.MapData;

public class DataTester implements Runnable {

	public static final int MAX_HEIGHT_DIFF = 3;
	private boolean retest = true;
	private final MapData data;

	// onyl used from test thread
	private boolean successful;
	private String result;
	private ShortPoint2D resultPosition;
	private final TestResultReceiver receiver;
	private final LandscapeFader fader = new LandscapeFader();

	public DataTester(MapData data, TestResultReceiver receiver) {
		this.data = data;
		this.receiver = receiver;
		new Thread(this, "data tester").start();
	}

	@Override
	public void run() {
		while (true) {
			synchronized (this) {
				while (!retest) {
					try {
						this.wait();
					} catch (InterruptedException e) {
					}
				}
				retest = false;
			}
			doTest();
		}
	}

	private void doTest() {
		successful = true;
		result = "";
		resultPosition = new ShortPoint2D(0, 0);
		for (int x = 0; x < data.getWidth() - 1; x++) {
			for (int y = 0; y < data.getHeight() - 1; y++) {
				test(x, y, x + 1, y);
				test(x, y, x + 1, y + 1);
				test(x, y, x, y + 1);
			}
		}
		receiver.testResult(result, successful, resultPosition);
	}

	private void test(int x, int y, int x2, int y2) {
		if (Math.abs(data.getLandscapeHeight(x2, y2)
		        - data.getLandscapeHeight(x, y)) > MAX_HEIGHT_DIFF) {
			successful = false;
			result = "Too high landscape diff";
			resultPosition = new ShortPoint2D(x, y);
		}
		ELandscapeType l2 = data.getLandscape(x2, y2);
		ELandscapeType l1 = data.getLandscape(x, y);
		if (!fader.canFadeTo(l2, l1)) {
			successful = false;
			result =
			        "Wrong landscape pair: " + l2 + ", "
			                + l1;
			resultPosition = new ShortPoint2D(x, y);
		}
	}

	public synchronized void retest() {
		retest = true;
		this.notifyAll();
	}

	public interface TestResultReceiver {
		public void testResult(String name, boolean allowed,
		        ShortPoint2D failPoint);
	}
}
