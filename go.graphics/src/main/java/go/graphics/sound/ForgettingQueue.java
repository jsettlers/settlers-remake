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
package go.graphics.sound;

/**
 * This is sort of a blocking queue. If there is nobody to use the request, the request is thrown away.
 * 
 * @author michael
 *
 * @param <T>
 */
public class ForgettingQueue<T> {
	private T current = null;
	private float lvolume;
	private float rvolume;

	public synchronized void offer(T e, float lvolume, float rvolume) {
		current = e;
		this.lvolume = lvolume;
		this.rvolume = rvolume;
		this.notify();
	}

	public synchronized Sound<T> take() throws InterruptedException {
		while (current == null) {
			this.wait();
		}
		Sound<T> r = new Sound<>(current, lvolume, rvolume);
		current = null;
		return r;
	}

	public static class Sound<T2> {
		private final T2 data;
		private final float lvolume2;
		private final float rvolume2;

		private Sound(T2 data, float lvolume, float rvolume) {
			this.data = data;
			lvolume2 = lvolume;
			rvolume2 = rvolume;
		}

		public T2 getData() {
			return data;
		}

		public float getLvolume() {
			return lvolume2;
		}

		public float getRvolume() {
			return rvolume2;
		}

		public float getVolume() {
			return Math.max(lvolume2, rvolume2);
		}

		public float getBalance() {
			if (rvolume2 < .001f) {
				return -1;
			} else if (lvolume2 < .001f) {
				return 1;
			} else if (lvolume2 > rvolume2) {
				return -1 + rvolume2 / lvolume2;
			} else {
				return 1 - lvolume2 / rvolume2;
			}
		}

	}
}
