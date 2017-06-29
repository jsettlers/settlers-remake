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
package jsettlers.mapcreator.mapvalidator.tasks.warning;

import java.util.HashSet;
import java.util.Set;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.graphics.localization.Labels;
import jsettlers.mapcreator.mapvalidator.ErrorMarker;
import jsettlers.mapcreator.mapvalidator.tasks.AbstractValidationTask;

/**
 * Check if there all resources needed to play a settler game
 * 
 * @author Andreas Butti
 */
public class ValidateMinumumLifeResources extends AbstractValidationTask {

	/**
	 * Checked resources
	 */
	private static final EResourceType[] REQUIRED = { EResourceType.COAL, EResourceType.GOLDORE, EResourceType.IRONORE, EResourceType.FISH };

	/**
	 * All river types
	 */
	private static final ELandscapeType[] RIVERS = { ELandscapeType.RIVER1, ELandscapeType.RIVER2, ELandscapeType.RIVER3, ELandscapeType.RIVER4 };

	/**
	 * Constructor
	 */
	public ValidateMinumumLifeResources() {
	}

	@Override
	public void doTest() {
		addHeader("minlife.header", null);

		checkResources();
		checkTree();
		checkStone();
		checkRiver();
	}

	/**
	 * Check if there is at least one river
	 */
	private void checkRiver() {
		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				ELandscapeType landscape = data.getLandscape(x, y);
				for (ELandscapeType r : RIVERS) {
					if (r == landscape) {
						// river found, nothing to do
						return;
					}
				}
			}
		}

		addWarningMessage(ErrorMarker.MISSING_LIFE_RESOURCE, "minlife.missing.river", null);
	}

	/**
	 * Check if there is at least one tree
	 */
	private void checkTree() {
		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				MapDataObject obj = data.getMapObject(x, y);
				if (obj instanceof MapTreeObject) {
					return;
				}
			}
		}

		addWarningMessage(ErrorMarker.MISSING_LIFE_RESOURCE, "minlife.missing.tree", null);
	}

	/**
	 * Check if there is at least one stone
	 */
	private void checkStone() {
		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				MapDataObject obj = data.getMapObject(x, y);
				if (obj instanceof StoneMapDataObject) {
					return;
				}
			}
		}

		addWarningMessage(ErrorMarker.MISSING_LIFE_RESOURCE, "minlife.missing.stone", null);
	}

	/**
	 * Check minimum Resource, do not check if they are reachable by all player etc.
	 */
	private void checkResources() {
		Set<EResourceType> foundResources = new HashSet<>();

		for (short x = 0; x < data.getWidth(); x++) {
			for (short y = 0; y < data.getHeight(); y++) {
				EResourceType resource = data.getResourceType(x, y);
				if (data.getResourceAmount(x, y) > 0) {
					foundResources.add(resource);
				}
			}
		}

		for (EResourceType r : REQUIRED) {
			if (!foundResources.contains(r)) {
				addWarningMessage(ErrorMarker.MISSING_LIFE_RESOURCE, "minlife.missing", null, Labels.getName(r));
			}
		}
	}

}
