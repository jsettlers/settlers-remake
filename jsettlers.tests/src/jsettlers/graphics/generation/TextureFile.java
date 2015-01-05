package jsettlers.graphics.generation;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * This is a texture file. It contains a short array and can write images to the file.
 * 
 * @author michael
 */
public class TextureFile {

	private final File file;
	private int drawx = 0; // x coordinate of free space
	// top pixel of current line
	private int linetop = 0;
	// first pixel that does not belong to the current line.
	private int linebottom = 0;
	private final ByteBuffer buffer;
	private final ShortBuffer shortBuffer;

	private final int width;
	private final int height;

	public TextureFile(File file, int width, int height) {
		this.file = file;
		this.width = width;
		this.height = height;

		buffer = ByteBuffer.allocate(width * height * 2);
		buffer.order(ByteOrder.nativeOrder());
		shortBuffer = buffer.asShortBuffer();
	}

	public TexturePosition addImage(ShortBuffer imageData, int width) {
		imageData.rewind();
		int height = (imageData.remaining() + width - 1) / width;
		int startx;
		int starty;

		// compute start
		if (drawx + width > this.width) {
			drawx = 0;
			linetop = linebottom;
		}
		startx = drawx;
		starty = linetop;
		// update for next image
		drawx += width;
		linebottom = Math.max(linebottom, linetop + height);

		// draw!
		short[] buffer = new short[width];
		for (int y = 0; imageData.hasRemaining(); y++) {
			shortBuffer.position((starty + y) * this.width + startx);
			imageData.get(buffer);
			shortBuffer.put(buffer);
		}

		return new TexturePosition((float) startx / this.width, (float) starty
				/ this.height, (float) (startx + width + 1) / this.width,
				(float) (starty + height + 1) / this.height);
	}

	public void write() throws IOException {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
				new FileOutputStream(file)));
		shortBuffer.rewind();
		while (shortBuffer.hasRemaining()) {
			out.writeShort(shortBuffer.get());
		}
		out.close();
	}
}
