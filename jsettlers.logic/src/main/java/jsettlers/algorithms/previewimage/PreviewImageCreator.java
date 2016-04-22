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
package jsettlers.algorithms.previewimage;

import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

/**
 * This class creates a preview image of a map that can be saved in the map header.
 * 
 * @author Andreas Eberle
 */
public final class PreviewImageCreator {

	/**
	 * Points to use for height computation.
	 */
	private final static RelativePoint[] HEIGHTPOINTS = new RelativePoint[] { new RelativePoint(0, 1), new RelativePoint(1, 1),
			new RelativePoint(0, 2), new RelativePoint(1, 2), new RelativePoint(2, 2), };

	private final int gridWidth;
	private final int gridHeight;

	private final IPreviewImageDataSupplier dataSupplier;
	private final int previewImageSize;

	/**
	 * Constructor to create a {@link PreviewImageCreator} object.
	 * 
	 * @param gridWidth
	 *            The width of the grid that's the source of the preview image.
	 * @param gridHeight
	 *            The height of the grid that's the source of the preview image.
	 * @param previewImageSize
	 *            The height and width the preview image will have.
	 * @param dataSupplier
	 *            The {@link IPreviewImageDataSupplier} that gives the data of the landscape of the source map.
	 */
	public PreviewImageCreator(int gridWidth, int gridHeight, int previewImageSize, IPreviewImageDataSupplier dataSupplier) {
		this.gridWidth = gridWidth;
		this.gridHeight = gridHeight;
		this.dataSupplier = dataSupplier;
		this.previewImageSize = previewImageSize;
	}

	/**
	 * Calculates a preview image with the data supplied by the {@link IPreviewImageDataSupplier} given in the constructor.
	 * 
	 * @return Array of colors as short values. The array represents a square image of the specified previewImageSize.
	 */
	public short[] getPreviewImage() {
		short[] image = new short[previewImageSize * previewImageSize];

		for (short x = 0; x < gridWidth; x++) {
			for (short y = 0; y < gridHeight; y++) {
				int imageSpace = toImageSpace(x, y);
				if (image[imageSpace] == 0) {
					image[imageSpace] = getColor(x, y);
				}
			}
		}

		boolean usey = false;
		for (int x = 0; x < previewImageSize; x++) {
			for (int y = 0; y < previewImageSize; y++) {
				if (usey && y > 0 && image[x + y * previewImageSize] == 0) {
					image[x + y * previewImageSize] = image[x + (y - 1) * previewImageSize];
					usey = false;
				} else if (x > 0 && image[x + y * previewImageSize] == 0) {
					image[x + y * previewImageSize] = image[x - 1 + y * previewImageSize];
					usey = true;
				} else if (y > 0 && image[x + y * previewImageSize] == 0) {
					image[x + y * previewImageSize] = image[x + (y - 1) * previewImageSize];
				}
			}
		}
		return image;
	}

	private int toImageSpace(int x, int y) {
		int inImageSpace = scale(x, gridWidth, previewImageSize) + scale(y, gridHeight, previewImageSize) * previewImageSize;
		return inImageSpace;
	}

	/**
	 * Scale a coordinate to image space.
	 * 
	 * @param x
	 * @param width
	 * @return
	 */
	private static final int scale(int x, int width, int previewImageSize) {
		int px = (int) ((double) x / width * previewImageSize);
		return px < 0 ? 0 : px >= previewImageSize ? previewImageSize : px;
	}

	private short getColor(short x, short y) {
		ShortPoint2D current = new ShortPoint2D(x, y);

		final int dheight = getLandscapeHeightAround(current, false) - getLandscapeHeightAround(current, true);
		final float basecolor = .8f + .15f * dheight;

		return dataSupplier.getLandscape(x, y).getColor().toShortColor(basecolor);
	}

	private int getLandscapeHeightAround(ShortPoint2D current, boolean upwards) {
		int count = 0;
		int height = 0;
		for (RelativePoint p : HEIGHTPOINTS) {
			ShortPoint2D toTest;
			if (upwards) {
				toTest = p.calculatePoint(current);
			} else {
				toTest = p.invert().calculatePoint(current);
			}

			short x = toTest.x;
			short y = toTest.y;
			if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
				height += dataSupplier.getLandscapeHeight(x, y);
				count += 1;
			}
		}

		if (count > 0) {
			return height / count;
		} else {
			return dataSupplier.getLandscapeHeight(current.x, current.y);
		}
	}
}
