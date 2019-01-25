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
package go.graphics;

/**
 * This class keeps track of the frames.
 * 
 * @author Michael Zangl
 */
public class FramerateComputer {
	private static final long RECOMPUTE_INTERVALL = 500*1000*1000;
	private long calcFrameStart = System.nanoTime();
	private double timePerFrame = 0;
	private long calcFrameEnd;
	private long calcLastFrameStart;
	private int capturedFrames = 0;

	/**
	 * Called whenever a new frame is displayed.
	 */
	public void nextFrame() {
		calcFrameEnd = System.nanoTime();
		capturedFrames++;
		if ((calcFrameEnd - calcFrameStart > RECOMPUTE_INTERVALL) && capturedFrames > 1) {
			recompute();
		}
	}

	private void recompute() {
		double deltaT = calcFrameEnd - calcFrameStart;
		timePerFrame = deltaT / capturedFrames;
		calcFrameStart = calcFrameEnd;
		capturedFrames = 1;
	}

	private static final double NS_PER_S = 1000*1000*1000.0;
	private static final double MS_PER_S = 1000.0;

	/**
	 * Gets the current frame rate.
	 * 
	 * @return The rate.
	 */
	public double getRate() {
		return NS_PER_S / timePerFrame;
	}

	public double getTime() {
		return timePerFrame / NS_PER_S;
	}

	public void nextFrame(int fpsLimit) {
		nextFrame();

		long ft = calcFrameEnd-calcLastFrameStart;
		long minft = (long) (NS_PER_S/fpsLimit);
		if(minft > ft) {
			try {
				Thread.sleep((long) ((minft-ft)/NS_PER_S*MS_PER_S));
			} catch (InterruptedException e) {}
		}
		calcLastFrameStart = System.nanoTime();
	}
}
