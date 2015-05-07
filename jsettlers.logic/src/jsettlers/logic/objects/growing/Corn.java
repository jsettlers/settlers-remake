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
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.objects.IMapObjectsManagerGrid;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;

/**
 * This is a Corn on the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Corn extends GrowingObject {
	private static final long serialVersionUID = -7535441306083940418L;

	public static final float GROWTH_DURATION = 7 * 60;
	public static final float DECOMPOSE_DURATION = 3 * 60;
	public static final float REMOVE_DURATION = 2 * 60;

	/**
	 * Creates a new Corn.
	 * 
	 * @param grid
	 */
	public Corn(ShortPoint2D pos) {
		super(pos, EMapObjectType.CORN_GROWING);
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
		return EMapObjectType.CORN_DEAD;
	}

	@Override
	protected EMapObjectType getAdultState() {
		return EMapObjectType.CORN_ADULT;
	}

	@Override
	protected void handlePlacement(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		super.handlePlacement(x, y, mapObjectsManager, grid);

		for (ShortPoint2D curr : getEarthArea(x, y)) {
			grid.setLandscape(curr.x, curr.y, ELandscapeType.EARTH);
		}
	}

	@Override
	protected void handleRemove(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		super.handleRemove(x, y, mapObjectsManager, grid);

		for (ShortPoint2D curr : getEarthArea(x, y)) {
			makePositionGrassIfPossible(curr.x, curr.y, mapObjectsManager, grid);
		}
	}

	private void makePositionGrassIfPossible(int x, int y, MapObjectsManager mapObjectsManager, IMapObjectsManagerGrid grid) {
		for (ShortPoint2D pos : getEarthArea(x, y)) {
			if (grid.hasMapObjectType(pos.x, pos.y, EMapObjectType.CORN_GROWING, EMapObjectType.CORN_ADULT, EMapObjectType.CORN_DEAD)) {
				return; // return if there is a map object in the area holding this position earth
			}
		}

		// no other corn keeps this position as earth => make it grass
		grid.setLandscape(x, y, ELandscapeType.GRASS);
	}

	private static HexGridArea getEarthArea(int x, int y) {
		return new HexGridArea(x, y, 0, 1);
	}

}
