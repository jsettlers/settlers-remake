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
package jsettlers.graphics.map.minimap;

import java.util.Arrays;

/**
 * This class runs as background thread and updates the lines in the original ui.
 * 
 * @author Michael Zangl
 */
class LineLoader extends AbstractLineLoader {

	/**
	 * The minimap we work for.
	 */
	private final Minimap minimap;

	/**
	 * The minimap image, including settlers.
	 */
	private short[][] buffer = new short[1][1];

	/**
	 * Create a new LineLoader for the original ui mini map.
	 * 
	 * @param minimap
	 *            The minimap to create the loader for.
	 * @param modeSettings
	 *            The settings (people shown, ...) to use.
	 */
	public LineLoader(Minimap minimap, MinimapMode modeSettings) {
		super(minimap, modeSettings);
		this.minimap = minimap;
	}

	@Override
	protected void resizeBuffer(int width, int height) {
		buffer = new short[height][width];
		for (short[] line : buffer) {
			Arrays.fill(line, BLACK);
		}
		minimap.setBufferArray(buffer);
	}

	@Override
	protected void markLineUpdate(int line) {
		minimap.setUpdatedLine(line);
	}

	@Override
	protected void setBuffer(int currentline, int x, short color) {
		buffer[currentline][x] = color;
	}
}
