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
package jsettlers.network.synchronic.timer;

/**
 * Container class for a {@link INetworkTimerable}, it's periodic execution delay and it's current execution delay.
 * 
 * @author Andreas Eberle
 * 
 */
public final class ScheduledTimerable {

	private final INetworkTimerable timerable;
	private final short delay;
	private short currDelay;

	public ScheduledTimerable(INetworkTimerable timerable, short delay) {
		this.timerable = timerable;
		this.delay = delay;
		this.currDelay = delay;
	}

	public INetworkTimerable getTimerable() {
		return timerable;
	}

	/**
	 * Checks if this task needs to be executed. (Is able to execute tasks serveral times if needed
	 * 
	 * @param timeSlice
	 *            number of milliseconds of the game time that expired since the last call.
	 */
	public void checkExecution(short timeSlice) {
		currDelay -= timeSlice;
		while (currDelay <= 0) {
			currDelay += delay;
			timerable.timerEvent();
		}
	}
}
