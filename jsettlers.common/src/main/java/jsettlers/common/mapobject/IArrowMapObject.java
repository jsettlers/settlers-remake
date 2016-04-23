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

import jsettlers.common.movable.EDirection;

/**
 * Specifies a arrow that comes from {@link #getSource()} and flies to {@link #getTarget()}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IArrowMapObject extends IMapObject {

	/**
	 * gets the x coordinate of the source position of the arrow.
	 * 
	 * @return The start x coordinate of the position where it was shot from
	 */
	short getSourceX();

	/**
	 * gets the x coordinate of the source position of the arrow.
	 * 
	 * @return The start x coordinate of the position where it was shot from
	 */
	short getSourceY();

	/**
	 * gets the x coordinate of the target position of the arrow.
	 * 
	 * @return the target x coordinate
	 */
	short getTargetX();

	/**
	 * gets the x coordinate of the target position of the arrow.
	 * 
	 * @return the target x coordinate
	 */
	short getTargetY();

	/**
	 * gets the direction in which the arrow is flying.
	 * 
	 * @return The direciton
	 */
	EDirection getDirection();
}
