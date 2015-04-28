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
package jsettlers.algorithms.heap;

/**
 * This interface defines what an element, that may be stored in a heap, must provide for the heap to work.
 * 
 * @author andreas
 */
public interface MinHeapable {
	/**
	 * Gets the rank of the element.
	 * <p>
	 * The rank may not change without noticing the heap of the change.
	 * 
	 * @return The rank. Any float value.
	 */
	float getRank();

	/**
	 * Gets the last value given to {@link #setHeapIdx(int)}. If the index was not set, it must return -1.
	 * 
	 * @return The heap index.
	 */
	int getHeapIdx();

	/**
	 * Sets the heap index.
	 * 
	 * @param idx
	 *            The heap index.
	 */
	void setHeapIdx(int idx);
}
