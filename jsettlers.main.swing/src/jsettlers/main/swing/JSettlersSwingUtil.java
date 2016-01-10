/*******************************************************************************
 * Copyright (c) 2015
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing;

import jsettlers.logic.map.MapLoader;
import jsettlers.logic.map.save.MapFileHeader;

import java.awt.image.BufferedImage;

/**
 * @author Andreas Butti
 * @author codingberlin
 */
public class JSettlersSwingUtil {

	public static BufferedImage createBufferedImageFrom(MapLoader mapLoader) {
		short[] imageColors = mapLoader.getImage();
		int xOffset = MapFileHeader.PREVIEW_IMAGE_SIZE;
		BufferedImage resultingImage = new BufferedImage(MapFileHeader.PREVIEW_IMAGE_SIZE + xOffset,
				MapFileHeader.PREVIEW_IMAGE_SIZE, BufferedImage.TYPE_INT_ARGB);

		xOffset--;
		for (int y = 0; y < MapFileHeader.PREVIEW_IMAGE_SIZE; y++) {
			for (int x = 0; x < MapFileHeader.PREVIEW_IMAGE_SIZE; x++) {
				int index = y * MapFileHeader.PREVIEW_IMAGE_SIZE + x;
				jsettlers.common.Color c = jsettlers.common.Color.fromShort(imageColors[index]);
				resultingImage.setRGB(x + xOffset, y, c.getARGB());
			}
			if (xOffset > 1) {
				xOffset--;
			}
		}
		return resultingImage;
	}

}
