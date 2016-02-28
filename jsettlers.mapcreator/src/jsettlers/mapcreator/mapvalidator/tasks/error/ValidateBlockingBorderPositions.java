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

import jsettlers.common.position.ShortPoint2D;
import jsettlers.mapcreator.mapvalidator.result.fix.FreeBorderFix;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;

/**
 * Check if all border positions are blocking
 * 
 * @author Andreas Butti
 *
 */
public class ValidateBlockingBorderPositions extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateBlockingBorderPositions() {
	}

	@Override
	public void doTest() {
		FreeBorderFix fix = new FreeBorderFix();
		addHeader("blockingborder.header", fix);

		int width = data.getWidth();
		int height = data.getHeight();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (1 <= y && y < height - 2 && 1 <= x && x < width - 2) {
					continue;
				}

				if (!data.getLandscape(x, y).isBlocking) {
					ShortPoint2D p = new ShortPoint2D(x, y);
					addErrorMessage("blockingborder.at-position", p, x, y);
					fix.addPosition(p);
				}
			}
		}
	}

}
