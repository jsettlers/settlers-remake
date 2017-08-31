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
package jsettlers.common.material;

/**
 * This enum defines the priority of material requests.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EPriority {
	// NOTE: THE STOPPED PRIORITY MUST HAVE priorityIndex == 0
	STOPPED(0),

	LOW(1),
	HIGH(2), ;

	public static final EPriority[] VALUES = EPriority.values();
	public static final int NUMBER_OF_PRIORITIES = VALUES.length;
	public static final EPriority DEFAULT = EPriority.LOW;

	public final byte ordinal;
	private final int priorityIndex;

	EPriority(int priorityIndex) {
		this.ordinal = (byte) ordinal();
		this.priorityIndex = priorityIndex;
	}

	/**
	 * 
	 * @return Returns the index of the priority. 0 Means the request is stopped. Indexes >= 1 have increasing priority with increasing index.
	 */
	public int getPriorityIndex() {
		return priorityIndex;
	}
}
