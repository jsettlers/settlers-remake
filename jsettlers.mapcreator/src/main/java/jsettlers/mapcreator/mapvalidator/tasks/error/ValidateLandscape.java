/*******************************************************************************
 * Copyright (c) 2015 - 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.mapcreator.mapvalidator.tasks.error;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.mapvalidator.result.fix.InvalidLandscapeFix;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;

/**
 * Test landscape height and constelation
 * 
 * @author Andreas Butti
 */
public class ValidateLandscape extends AbstractValidationTask {

	/**
	 * Max height diff
	 */
	public static final int MAX_HEIGHT_DIFF = 3;

	/**
	 * Fix
	 */
	private InvalidLandscapeFix landscapeFix = new InvalidLandscapeFix();

	/**
	 * Constructor
	 */
	public ValidateLandscape() {
	}

	@Override
	public void doTest() {
		addHeader("landscape.header", landscapeFix);

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
			ShortPoint2D p = new ShortPoint2D(x, y);
			addErrorMessage("landscape.height", p);
			landscapeFix.addPosition(p);
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
	 * Get diff between two landscapes
	 * 
	 * @param landscape
	 *            To compare
	 * @param landscape2
	 *            To compare
	 * @return diff
	 */
	public static int getMaxHeightDiff(ELandscapeType landscape, ELandscapeType landscape2) {
		return (landscape.isFlat() || landscape2.isFlat()) ? 0 : MAX_HEIGHT_DIFF;
	}

}
