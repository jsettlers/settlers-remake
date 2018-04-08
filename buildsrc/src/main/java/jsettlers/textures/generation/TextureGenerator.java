/*
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
 */
package jsettlers.textures.generation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class lets you generate a texture that can be understood by the graphics module. It generates the .texture file.
 *
 * @author michael
 */
public final class TextureGenerator {

	private static class ImageData {
		ProvidedImage data = null;
		ProvidedImage torso = null;
		String name;
	}

	private final File rawDirectory;
	private final File outDirectory;
	private final TextureIndex textureIndex;

	public TextureGenerator(TextureIndex textureIndex, File rawDirectory, File outDirectory) {
		this.textureIndex = textureIndex;
		this.rawDirectory = rawDirectory;
		this.outDirectory = outDirectory;
	}

	void processTextures(File resourceDirectory) {
		processTextures(resourceDirectory, resourceDirectory);
	}

	private void processTextures(File resourceDirectory, File directory) {
		File[] files = directory.listFiles();
		if (files == null) {
			return;
		}

		Arrays.stream(files)
				.parallel()
				.filter(File::isDirectory)
				.forEach(subDirectory -> processTextures(resourceDirectory, subDirectory));
		Arrays.stream(files)
				.parallel()
				.filter(File::isFile)
				.filter(file -> file.getName().endsWith(".png"))
				.filter(file -> !file.getName().endsWith(".t.png")) // torso files are added with their corresponding image file
				.forEach(file -> processTexturesFile(resourceDirectory, file));
	}

	private void processTexturesFile(File baseDirectory, File file) {
		ImageData imageData = addIdToTexture(baseDirectory, file);
		storeImageData(imageData);
	}

	private ImageData addIdToTexture(File baseDirectory, File imageFile) {
		String name = calculateName(baseDirectory, imageFile);

		ImageData imageData = new ImageData();
		imageData.name = name;
		imageData.data = getImage(imageFile);

		File torsoFile = new File(imageFile.getPath().replace(".png", ".t.png"));
		if (torsoFile.exists()) {
			imageData.torso = getImage(torsoFile);
		}

		if (imageData.data == null) {
			System.err.println("WATNING: loading image " + name + ": No image file found.");
		}
		return imageData;
	}

	private String calculateName(File baseDirectory, File file) {
		StringBuilder name = new StringBuilder(file.getName());
		File currentFile = file.getParentFile();

		while (!baseDirectory.equals(currentFile)) {
			name.insert(0, currentFile.getName() + "-");
			currentFile = currentFile.getParentFile();
		}

		return name.toString();
	}

	private void storeImageData(ImageData imageData) {
		storeImage(imageData.name, imageData.data, imageData.torso != null);
		if (imageData.torso != null) {
			storeImage(imageData.name, imageData.torso, false);
		}
	}

	private void storeImage(String name, ProvidedImage data, boolean hasTorso) {
		try {
			if (data != null) {
				int texture = textureIndex.getNextTextureIndex();
				TexturePosition position = addAsNewImage(data, texture);
				textureIndex.registerTexture(name, texture, data.getOffsetX(), data.getOffsetY(), data.getWidth(), data.getHeight(), hasTorso, position);
			}
		} catch (Throwable t) {
			System.err.println("WARNING: Problem writing image " + name + ". Problem was: " + t.getMessage());
		}
	}

	// This is slow.
	private TexturePosition addAsNewImage(ProvidedImage data, int texture) throws IOException {
		int size = getNextPOT(Math.max(data.getWidth(), data.getHeight()));
		TextureFile file = new TextureFile(new File(outDirectory, texture + ""), size, size);
		TexturePosition position = file.addImage(data.getData(), data.getWidth());
		file.write();
		return position;
	}

	private static int getNextPOT(int height) {
		int i = 2;
		while (i < height) {
			i *= 2;
		}
		return i;
	}

	private ProvidedImage getImage(File imageFile) {
		try {
			int[] offsets = getOffsets(imageFile);
			BufferedImage image = ImageIO.read(imageFile);

			return new ProvidedImage(image, offsets);
		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading image " + imageFile + ". Problem was: " + t.getMessage());
			return null;
		}
	}

	private int[] getOffsets(File imageFile) {
		File offset = new File(imageFile.getPath() + ".offset");

		if (!offset.exists()) {
			return new int[] { 0, 0 };
		}

		int[] offsets = new int[2];

		try (Scanner in = new Scanner(offset)) {
			offsets[0] = in.nextInt();
			in.skip("\\s+");
			offsets[1] = in.nextInt();
			in.close();
			return offsets;

		} catch (Throwable t) {
			System.err.println("WARNING: Problem reading offsets for " + imageFile + ", assuming (0,0). Problem was: " + t.getMessage());
			return new int[] { 0, 0 };
		}
	}
}
