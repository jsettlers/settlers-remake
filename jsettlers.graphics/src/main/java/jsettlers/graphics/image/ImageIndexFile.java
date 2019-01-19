/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.graphics.image;

import jsettlers.common.images.TextureMap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * This class loads the image index from a file.
 *
 * @author Michael Zangl
 */
public class ImageIndexFile {
	private static final int SHORTS_PER_IMAGE = 12;
	private ImageIndexImage[] images = null;

	/**
	 * Gets the image reference with the given index.
	 *
	 * @param index
	 * 		The index of the image in the file.
	 * @return The image.
	 */
	public Image getImage(int index) {
		if (images == null) {
			try {
				load();
			} catch (IOException e) {
				e.printStackTrace();
				images = new ImageIndexImage[0];
			}
		}
		if (index < images.length) {
			return images[index];
		} else {
			return NullImage.getInstance();
		}
	}

	private void load() throws IOException {
		final DataInputStream in = new DataInputStream(new BufferedInputStream(getResource("texturemap")));

		ArrayList<ImageIndexTexture> textures = new ArrayList<>();

		byte[] header = new byte[4];
		in.read(header);
		if (header[0] != 'T' || header[1] != 'E' || header[2] != 'X' || header[3] != '1') {
			throw new IOException("Texture file has wrong version.");
		}

		int length = in.available() / 2 / SHORTS_PER_IMAGE;

		images = new ImageIndexImage[length];
		for (int i = 0; i < length; i++) {
			images[i] = readNextImage(in, textures);
		}
	}

	private ImageIndexImage readNextImage(final DataInputStream in, ArrayList<ImageIndexTexture> textures) throws IOException {
		int offsetX = in.readShort();
		int offsetY = in.readShort();
		short width = in.readShort();
		short height = in.readShort();
		short textureFileNumber = in.readShort();
		int torsoIndex = in.readInt();
		boolean isTorso = in.readShort() > 0;

		float uMin = (float) in.readShort() / Short.MAX_VALUE;
		float vMin = (float) in.readShort() / Short.MAX_VALUE;
		float uMax = (float) in.readShort() / Short.MAX_VALUE;
		float vMax = (float) in.readShort() / Short.MAX_VALUE;

		while (textureFileNumber >= textures.size()) {
			textures.add(new ImageIndexTexture(getResource("images_" + textures.size()), "texturemap-image"+textures.size()));
		}

		ImageIndexImage primaryImage = new ImageIndexImage(textures.get(textureFileNumber), offsetX, offsetY, width, height, uMin, vMin, uMax, vMax, isTorso);

		if (torsoIndex >= 0) {
			primaryImage.setTorso(images[torsoIndex]);
		}
		return primaryImage;
	}

	private InputStream getResource(String string) {
		return TextureMap.class.getResourceAsStream(string);
	}
}
