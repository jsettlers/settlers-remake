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
		final DataInputStream in =
		        new DataInputStream(new BufferedInputStream(
		                ResourceManager.getFile("images/texturemap")));

		ArrayList<ImageIndexTexture> textures =
		        new ArrayList<ImageIndexTexture>();

		byte[] header = new byte[4];
		in.read(header);
		if (header[0] != 'T' || header[1] != 'E' || header[2] != 'X'
		        || header[3] != '1') {
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
				InputStream inputStream =
				        ResourceManager.getFile("images/" + textures.size());
				textures.add(new ImageIndexTexture(inputStream));
			}

			images[i] =
			        new ImageIndexImage(textures.get(textureFileNumber),
			                offsetX, offsetY, width, height, umin, vmin, umax,
			                vmax);
		}
	}
}
