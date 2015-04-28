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
package jsettlers.algorithms.path.astar.queues.bucket;

public abstract class AbstractBucketQueue {
	public abstract void insert(int elementId, float rank);

	public abstract int size();

	public abstract void clear();

	/**
	 * Deletes the element of the heap that has the minimal value.
	 * <p>
	 * If the heap is empty, no action is performed.
	 * 
	 * @return The deleted element, or -1 if the heap was empty.
	 */
	public abstract int deleteMin();

	/**
	 * This method must be called to update the position in the priority queue when the costs of the element had been reduced (the priority has
	 * increased!).
	 * 
	 * @param elementId
	 *            Id of the element.
	 * @param newRank
	 *            the old rank of the element.
	 */
	public abstract void increasedPriority(int elementId, float oldRank, float newRank);

	/**
	 * 
	 * @return Returns true if the priority queue is empty,<br>
	 *         false if it's not empty.
	 */
	public abstract boolean isEmpty();
}
