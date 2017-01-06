/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.common.utils.debug;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import jsettlers.common.Color;
import jsettlers.common.utils.interfaces.IBooleanCoordinateValueProvider;
import jsettlers.common.utils.interfaces.ICoordinateValueProvider;

/**
 * Created by Andreas Eberle on 06.01.2017.
 */
public final class DebugImagesHelper {
	public static boolean DEBUG_IMAGES_ENABLED = false;
	public static String DEBUG_PATH = "/tmp/jsettlers";

	public static void setupDebugging() {
		if (DEBUG_IMAGES_ENABLED) {
			File debugFolder = new File(DEBUG_PATH);
			deleteRecursive(debugFolder);

			debugFolder.mkdirs();
		}
	}

	private static void deleteRecursive(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				deleteRecursive(child);
			}
		}
		file.delete();
	}

	public static void writeDebugImageBoolean(String suffix, int width, int height, IBooleanCoordinateValueProvider provider) {
		writeDebugImage(suffix, width, height, (x, y) -> provider.getValue(x, y) ? Color.BLUE : Color.RED);
	}

	public static void writeDebugImage(String suffix, int width, int height, ICoordinateValueProvider<Color> colorProvider) {
		if (!DEBUG_IMAGES_ENABLED) {
			return;
		}

		try {
			BufferedImage debugImage = createDebugImage(width, height, colorProvider);
			ImageIO.write(debugImage, "png", new File(DEBUG_PATH, System.nanoTime() + "-" + suffix + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static BufferedImage createDebugImage(int width, int height, ICoordinateValueProvider<Color> colorProvider) {
		BufferedImage image = new BufferedImage(width + height, height, BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		graphics.clearRect(0, 0, width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int imageX = x + (height - 1 - y);
				image.setRGB(imageX, y, colorProvider.getValue(x, y).getARGB());
			}
		}

		return image;
	}
}
