/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import jsettlers.logic.map.loading.data.objects.IPlayerIdProvider;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.mapcreator.mapvalidator.result.fix.DeleteObjectFix;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;

/**
 * Check the all player are valid
 * 
 * @author Andreas Butti
 */
public class ValidatePlayer extends AbstractValidationTask {

	/**
	 * Fix for wrong placed settlers
	 */
	private final DeleteObjectFix fix = new DeleteObjectFix();

	/**
	 * Constructor
	 */
	public ValidatePlayer() {
	}

	@Override
	public void doTest() {
		int playerCount = header.getMaxPlayers();
		addHeader("player.header", fix);

		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				MapDataObject mapObject = data.getMapObject(x, y);
				if (mapObject instanceof IPlayerIdProvider) {
					int p = ((IPlayerIdProvider) mapObject).getPlayerId();
					if (p >= playerCount) {
						fix.addInvalidObject(new ShortPoint2D(x, y));
						addErrorMessage("player.text", new ShortPoint2D(x, y));
					}
				}
			}
		}
	}
}
