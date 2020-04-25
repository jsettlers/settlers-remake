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
package jsettlers.algorithms.fogofwar;

import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

/**
 * interface specifying the methods needed by the fog of war to operate on a grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IFogOfWarGrid {

	IMovable getMovableAt(short x, short y);

	IMapObject getMapObjectsAt(short x, short y);

	ConcurrentLinkedQueue<? extends IViewDistancable> getMovableViewDistancables();

	ConcurrentLinkedQueue<? extends IViewDistancable> getBuildingViewDistancables();

}
