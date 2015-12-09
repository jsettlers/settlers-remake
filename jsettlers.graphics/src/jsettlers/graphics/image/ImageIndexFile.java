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
package jsettlers.graphics.image;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jsettlers.common.resources.ResourceManager;

/**
 * This class loads the image index from a file.
 * 
 * @author michael
 */
public class ImageIndexFile {
	private ImageIndexImage[] images = null;

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
		final DataInputStream in = new DataInputStream(new BufferedInputStream(ResourceManager.getResourcesFileStream("images/texturemap")));

		ArrayList<ImageIndexTexture> textures = new ArrayList<ImageIndexTexture>();

		byte[] header = new byte[4];
		in.read(header);
		if (header[0] != 'T' || header[1] != 'E' || header[2] != 'X' || header[3] != '1') {
			throw new IOException("Texture file has wrong version.");
		}

		int length = in.available() / 2 / 9;

		images = new ImageIndexImage[length];
		for (int i = 0; i < length; i++) {
			int offsetX = in.readShort();
			int offsetY = in.readShort();
			short width = in.readShort();
			short height = in.readShort();
			int textureFileNumber = in.readShort() & 0x7fff; // < TODO: torso

			float umin = (float) in.readShort() / 0x7fff;
			float vmin = (float) in.readShort() / 0x7fff;
			float umax = (float) in.readShort() / 0x7fff;
			float vmax = (float) in.readShort() / 0x7fff;

			while (textureFileNumber >= textures.size()) {
				InputStream inputStream = ResourceManager.getResourcesFileStream("images/" + textures.size());
				textures.add(new ImageIndexTexture(inputStream));
			}

			images[i] = new ImageIndexImage(textures.get(textureFileNumber), offsetX, offsetY, width, height, umin, vmin, umax, vmax);
		}
	}
}
