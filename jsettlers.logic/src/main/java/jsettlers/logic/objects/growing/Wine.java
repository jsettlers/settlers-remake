/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.logic.objects.growing;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.objects.IMapObjectsManagerGrid;
import jsettlers.logic.map.grid.objects.MapObjectsManager;

/**
 * This is a Corn on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Wine extends GrowingObject {
	private static final long serialVersionUID = -7535441306083940418L;

	public static final float GROWTH_DURATION = 7 * 60;
	public static final float DECOMPOSE_DURATION = 3 * 60;
	public static final float REMOVE_DURATION = 2 * 60;

	/**
	 * Creates a new Corn.
	 * 
	 * @param grid
	 */
	public Wine(ShortPoint2D pos) {
		super(pos, EMapObjectType.WINE_GROWING);
	}

	@Override
	protected float getGrowthDuration() {
		return GROWTH_DURATION;
	}

	@Override
	protected float getDecomposeDuration() {
		return DECOMPOSE_DURATION;
	}

	@Override
	protected EMapObjectType getDeadState() {
		return EMapObjectType.WINE_DEAD;
	}

	@Override
	protected EMapObjectType getAdultState() {
		return EMapObjectType.WINE_HARVESTABLE;
	}

	@Override
	protected void handlePlacement(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		super.handlePlacement(x, y, mapObjectsManager, grid);

		grid.setLandscape(x, y, ELandscapeType.EARTH);
	}

	@Override
	protected void handleRemove(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		super.handleRemove(x, y, mapObjectsManager, grid);

		grid.setLandscape(x, y, ELandscapeType.GRASS);
	}
}
