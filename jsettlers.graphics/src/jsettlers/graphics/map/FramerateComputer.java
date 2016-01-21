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
package jsettlers.graphics.map;

/**
 * This class keeps track of the frames.
 * 
 * @author Michael Zangl
 */
public class FramerateComputer {
	private static final long RECOMPUTE_INTERVALL = 500;
	private final long[] lastFrames = new long[30];
	private long lastRecompute = 0;
	private int capturedFrames = 0;
	private double rate;

	/**
	 * Called whenever a new frame is displayed.
	 */
	public void nextFrame() {
		long time = System.currentTimeMillis();
		lastFrames[capturedFrames] = time;
		capturedFrames++;
		if ((time - lastRecompute > RECOMPUTE_INTERVALL || capturedFrames >= lastFrames.length)
				&& capturedFrames > 1) {
			recompute();
			lastRecompute = time;
		}
	}

	private void recompute() {
		long time = lastFrames[capturedFrames - 1] - lastFrames[0];
		rate = 1000.0 / time * capturedFrames;
		lastFrames[0] = lastFrames[capturedFrames - 1];
		capturedFrames = 1;
	}

	/**
	 * Gets the current frame rate.
	 * 
	 * @return The rate.
	 */
	public double getRate() {
		return rate;
	}
}
