package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.data.LandscapeFader;

/**
 * Test landscape height and constelation
 * 
 * @author Andreas Butti
 */
public class ValidateLandscape extends AbstractValidationTask {

	/**
	 * Max height diff
	 */
	private static final int MAX_HEIGHT_DIFF = 3;

	/**
	 * To check landacape pairs
	 */
	private final LandscapeFader fader = new LandscapeFader();

	/**
	 * Constructor
	 */
	public ValidateLandscape() {
	}

	@Override
	public void doTest() {
		for (int x = 0; x < data.getWidth() - 1; x++) {
			for (int y = 0; y < data.getHeight() - 1; y++) {
				testLandscape(x, y, x + 1, y);
				testLandscape(x, y, x + 1, y + 1);
				testLandscape(x, y, x, y + 1);
			}
		}
	}

	private void testLandscape(int x, int y, int x2, int y2) {
		ELandscapeType l2 = data.getLandscape(x2, y2);
		ELandscapeType l1 = data.getLandscape(x, y);
		int maxHeightDiff = getMaxHeightDiff(l1, l2);
		if (Math.abs(data.getLandscapeHeight(x2, y2) - data.getLandscapeHeight(x, y)) > maxHeightDiff) {
			testFailed("Too high landscape diff", new ShortPoint2D(x, y));
		}
		if (!fader.canFadeTo(l2, l1)) {
			testFailed("Wrong landscape pair: " + l2 + ", " + l1, new ShortPoint2D(x, y));
		}

		if (players[x][y] != players[x2][y2]) {
			if (players[x][y] != -1) {
				borders[x][y] = true;
			}
			if (players[x2][y2] != -1) {
				borders[x2][y2] = true;
			}
		}
	}

	/**
	 * Get diff between two landacapes
	 * 
	 * @param landscape
	 *            To compare
	 * @param landscape2
	 *            To compare
	 * @return diff
	 */
	private static int getMaxHeightDiff(ELandscapeType landscape, ELandscapeType landscape2) {
		return landscape.isWater() || landscape == ELandscapeType.MOOR || landscape == ELandscapeType.MOORINNER || landscape2.isWater()
				|| landscape2 == ELandscapeType.MOOR || landscape2 == ELandscapeType.MOORINNER ? 0 : MAX_HEIGHT_DIFF;
	}

}
