/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.common.menu.action;

/**
 * The mode movables should use for move_to
 * @author Michael Zangl
 */
public enum EMoveToMode {
	/**
	 * Normal move
	 */
	NORMAL(false, false),
	/**
	 * Forced move
	 */
	FORCED(true, false),
	/**
	 * Work at destination.
	 * For geologists, thieves and pioneers, this implies that they shoud start their work when they reach the destination
	 * For soldiers, they should start to patrol between the start and end point
	 */
	WORK(true, true),
	/**
	 * Add a waypoint to the route the movable should take on the next move action of any other type
	 */
	ADD_WAYPOINT(false, false);
	
	private final boolean force;
	private final boolean workAtDestination;

	EMoveToMode(boolean doForce, boolean doWorkAtDestination) {
		this.force = doForce;
		this.workAtDestination = doWorkAtDestination;
	}
	
	public boolean isForced() {
		return force;
	}
	
	public boolean doWorkAtDestination() {
		return workAtDestination;
	}
}
