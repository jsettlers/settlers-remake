/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.common.statistics;

/**
 * This interface supplies game time information to the UI.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IGameTimeProvider {
	IGameTimeProvider DUMMY_IMPLEMENTATION = new IGameTimeProvider() {
		@Override
		public int getGameTime() {
			return 0;
		}

		@Override
		public float getGameSpeed() {
			return 0;
		}

		@Override
		public boolean isGamePausing() {
			return false;
		}
	};

	/**
	 * Gets the game time.
	 * 
	 * @return The current game time in milliseconds.
	 */
	int getGameTime();

	/**
	 * Get the current game time factor
	 *
	 * @return The current game speed in multiples of 1
	 */
	float getGameSpeed();

	/**
	 * Gets if the game is pausing.
	 * 
	 * @return true if the game is pausing, false if not.
	 */
	boolean isGamePausing();
}