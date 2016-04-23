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
package jsettlers.common.mapobject;

/**
 * This interface is used to define an object that can be displayed on the map.
 * 
 * @author michael
 * 
 */
public interface IMapObject {

	/**
	 * Gets the type of the object.
	 * 
	 * @return The type of the object to display. May not be <code>null</code>.
	 */
	EMapObjectType getObjectType();

	/**
	 * this value is used for different things:<br>
	 * for trees:<br>
	 * - when the tree is growing, the value increases from 0 to 1 according to it's size.<br>
	 * - when the tree has been cut, the value increases from 0 to 1. The upper constants define which value defines which state.
	 * <p />
	 * for stones:<br>
	 * - the value gives the number of stones that can be picked from this stone.
	 * <p />
	 * for buildings:<br>
	 * - the value gives the construction state
	 * 
	 * @return a positive float, normally from 0..1
	 */
	float getStateProgress();

	/**
	 * Gets the next map object for that position.
	 * 
	 * @return The next object at the same position
	 */
	IMapObject getNextObject();

	/**
	 * Returns the first {@link IMapObject} on this stack having the given {@link EMapObjectType}.
	 * 
	 * @param type
	 *            The {@link EMapObjectType} to look for.
	 * @return The first {@link IMapObject} found on this stack or <code>null</code> if none of this type has been found.
	 */
	IMapObject getMapObject(EMapObjectType type);
}
