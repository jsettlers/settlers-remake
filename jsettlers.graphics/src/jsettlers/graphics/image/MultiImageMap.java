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

import go.graphics.GLDrawContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.map.draw.GLPreloadTask;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatBitmapReader;
import jsettlers.graphics.reader.ImageArrayProvider;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is a map of multile images of one sequence. It always contains the settler image and the torso
 * 
 * @author michael
 */
public class MultiImageMap implements ImageArrayProvider, GLPreloadTask {

	private final int width;
	private final int height;
	private int drawx = 0; // x coordinate of free space
	private int linetop = 0;
	private int linebottom = 0;
	private int drawpointer = 0;
	private boolean drawEnabled = false;
	private boolean textureValid = false;
	private int textureIndex = -1;
	private ShortBuffer buffers;
	private ByteBuffer byteBuffer;

	private final File cacheFile;

	public MultiImageMap(int width, int height, String id) {
		this.width = width;
		this.height = height;
		File root = new File(ResourceManager.getResourcesDirectory(), "cache");
		cacheFile = new File(root, "cache-" + id);
	}

	private void allocateBuffers() {
		byteBuffer = ByteBuffer.allocateDirect(width * height * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		buffers = byteBuffer.asShortBuffer();
	}

	public synchronized void addSequences(AdvancedDatFileReader dfr, int[] sequenceIndexes,
			Sequence<Image>[] addTo) throws IOException {
		allocateBuffers();

		ImageMetadata settlermeta = new ImageMetadata();
		ImageMetadata torsometa = new ImageMetadata();
		for (int seqindex : sequenceIndexes) {
			long[] settlers = dfr.getSettlerPointers(seqindex);
			long[] torsos = dfr.getTorsoPointers(seqindex);

			Image[] images = new Image[settlers.length];
			for (int i = 0; i < settlers.length; i++) {
				// System.out.println("Processing seq + " + seqindex +
				// ", image " + i + ":");

				ByteReader reader;
				reader = dfr.getReaderForPointer(settlers[i]);
				DatBitmapReader.uncompressImage(reader,
						dfr.getSettlerTranslator(), settlermeta,
						this);
				int settlerx = drawx - settlermeta.width;
				int settlery = linetop;

				int torsox = 0;
				int torsoy = 0;
				if (torsos != null) {
					reader = dfr.getReaderForPointer(torsos[i]);
					if (reader != null) {
						DatBitmapReader.uncompressImage(reader,
								dfr.getTorsoTranslator(),
								torsometa, this);
						torsox = drawx - torsometa.width;
						torsoy = linetop;
					}
				}
				// System.out.println("Got image Data: settlerx = " + settlerx +
				// ", settlery = " + settlery + ", w=" + settlermeta.width +
				// ", h=" + settlermeta.height);

				images[i] =
						new MultiImageImage(this, settlermeta, settlerx,
								settlery, torsos == null ? null : torsometa,
								torsox, torsoy);
			}
			addTo[seqindex] = new ArraySequence<Image>(images);
		}

		// request a opengl rerender, or do it ourselves on the next image
		// request
		textureValid = false;
		ImageProvider.getInstance().addPreloadTask(this);
	}

	/**
	 * Forces the regeneration of the cache file.
	 */
	public synchronized void writeCache() {
		FileOutputStream out = null;
		try {
			cacheFile.getParentFile().mkdirs();
			cacheFile.delete();
			File tempFile = new File(cacheFile.getParentFile(), cacheFile.getName() + ".tmp");
			out = new FileOutputStream(tempFile);

			try {
				byte[] line = new byte[this.width * 2];
				byteBuffer.rewind();
				while (byteBuffer.hasRemaining()) {
					byteBuffer.get(line);
					out.write(line);
				}
			} finally {
				out.close();
			}

			tempFile.renameTo(cacheFile);

			buffers = null;
			byteBuffer = null;
		} catch (IOException e) {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
				}
			}
			e.printStackTrace();
		}
	}

	public synchronized boolean hasCache() {
		return cacheFile.isFile();
	}

	@Override
	public void startImage(int width, int height) throws IOException {
		if (this.width < drawx + width) {
			if (linebottom + height <= this.height) {
				linetop = linebottom;
				drawx = 0;
			} else {
				drawEnabled = false;
				System.err.println("Error adding image to texture: "
						+ "there is no space to open a new row");
				return;
			}
		}

		if (linetop + height < this.height) {
			drawEnabled = true;
			textureValid = false;
			drawpointer = drawx + linetop * this.width;
			drawx += width;
			linebottom = Math.max(linebottom, linetop + height);
		} else {
			System.err.println("Error adding image to texture: "
					+ "Line to low");
			drawEnabled = false;
			return;
		}
	}

	@Override
	public void writeLine(short[] data, int length) throws IOException {
		if (drawEnabled) {
			int dp = drawpointer;
			buffers.position(dp);
			buffers.put(data, 0, length);
			drawpointer = dp + this.width;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Gets the texture index.
	 * 
	 * @param gl
	 * @return
	 */
	public int getTexture(GLDrawContext gl) {
		if (!textureValid) {
			if (textureIndex > -1) {
				gl.deleteTexture(textureIndex);
			}
			try {
				loadTexture(gl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return textureIndex;
	}

	private synchronized void loadTexture(GLDrawContext gl) throws IOException,
			IOException {
		if (buffers == null) {
			allocateBuffers();
			FileInputStream in = new FileInputStream(cacheFile);
			try {
				byte[] line = new byte[this.width * 2];
				while (in.available() > 0) {
					if (in.read(line) <= 0) {
						throw new IOException();
					}
					byteBuffer.put(line);
				}
				byteBuffer.rewind();
			} finally {
				in.close();
			}
		}

		buffers.rewind();
		textureIndex = gl.generateTexture(width, height, buffers);
		System.out.println("opengl Texture: " + textureIndex
				+ ", thread: " + Thread.currentThread().toString());
		if (textureIndex > -1) {
			textureValid = true;
		}
		buffers = null;
		byteBuffer = null;
	}

	@Override
	public void run(GLDrawContext context) {
		getTexture(context);
	}
}
