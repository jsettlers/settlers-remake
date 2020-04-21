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
package jsettlers.graphics.map.draw.settlerimages;

/**
 * This is a map item of settler images.
 * 
 * @author michael
 *
 */
public class SettlerImageMapItem {
	private final int file;

	private final int sequenceIndex;

	private final int start;

	private final int duration;

	public SettlerImageMapItem(int file, int sequenceIndex, int start,
			int duration) {
		this.file = file;
		this.sequenceIndex = sequenceIndex;
		this.start = start;
		this.duration = duration;
	}

	public int getFile() {
		return this.file;
	}

	public int getSequenceIndex() {
		return this.sequenceIndex;
	}

	public int getStart() {
		return this.start;
	}

	public int getDuration() {
		return this.duration;
	}
}
