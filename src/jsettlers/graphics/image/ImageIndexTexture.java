package jsettlers.graphics.image;

import go.graphics.GLDrawContext;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ShortBuffer;

public final class ImageIndexTexture {
	private int textureIndex = -1;
	private final InputStream file;

	public ImageIndexTexture(InputStream inputStream) {
		this.file = inputStream;
	}

	public int getTextureIndex(GLDrawContext gl) {
		if (textureIndex < 0) {
			loadTexture(gl);
		}
		return textureIndex;
	}

	private void loadTexture(GLDrawContext gl) {
		try {
			final DataInputStream in =
			        new DataInputStream(new BufferedInputStream(
			                file));
			int i = in.available() / 2;
			final int height = nextLowerPOT(Math.sqrt(i));
			final int width = nextLowerPOT(i / height);

			final ShortBuffer data = ShortBuffer.allocate(width * height);
			while (data.hasRemaining()) {
				data.put(in.readShort());
			}
			data.rewind();
			textureIndex = gl.generateTexture(width, height, data);
		} catch (final IOException e) {
			e.printStackTrace();
			textureIndex = 0;
		}
	}

	private static int nextLowerPOT(double number) {
		int i = 2;
		while (i * 2 <= number) {
			i *= 2;
		}
		return i;
	}
}
