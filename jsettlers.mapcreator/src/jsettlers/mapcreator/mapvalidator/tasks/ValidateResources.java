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
package jsettlers.mapcreator.mapvalidator.tasks;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.localization.EditorLabels;
import jsettlers.mapcreator.mapvalidator.result.fix.InvalidResourceFix;

/**
 * Validate resources on wrong lanscape
 * 
 * @author Andreas Butti
 *
 */
public class ValidateResources extends AbstractValidationTask {

	/**
	 * Constructor
	 */
	public ValidateResources() {
	}

	@Override
	public void doTest() {
		InvalidResourceFix fix = new InvalidResourceFix();
		addHeader("resource.header", fix);

		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				ELandscapeType landacape = data.getLandscape(x, y);
				EResourceType resource = data.getResourceType(x, y);
				if (data.getResourceAmount(x, y) > 0 && !mayHoldResource(landacape, resource)) {
					String landscapeName = EditorLabels.getLabel("landscape." + landacape.name());
					String resourceName = Labels.getName(resource);
					ShortPoint2D p = new ShortPoint2D(x, y);
					addErrorMessage("resource.text", p, landscapeName, resourceName);
					fix.addInvalidResource(p);
				}
			}
		}
	}

	/**
	 * Check resource type on landacape
	 * 
	 * @param landscape
	 *            Landacape
	 * @param resourceType
	 *            Resource
	 * @return true if valid
	 */
	public static boolean mayHoldResource(ELandscapeType landscape, EResourceType resourceType) {
		if (resourceType == EResourceType.FISH) {
			return landscape.isWater();
		} else {
			return landscape == ELandscapeType.MOUNTAIN || landscape == ELandscapeType.MOUNTAINBORDER;
		}
	}

}
