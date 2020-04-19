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

import go.graphics.GLDrawContext;
import go.graphics.TextureHandle;

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
import jsettlers.graphics.image.reader.AdvancedDatFileReader;
import jsettlers.graphics.image.reader.DatBitmapReader;
import jsettlers.graphics.image.reader.ImageArrayProvider;
import jsettlers.graphics.image.reader.ImageMetadata;
import jsettlers.graphics.image.reader.bytereader.ByteReader;

/**
 * This is a map of multiple images of one sequence. It always contains the settler image and the torso. This class allows packing the settler images
 * to a single, big texture.
 * 
 * @author Michael Zangl
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
	private TextureHandle texture = null;
	private ShortBuffer buffers;
	private ByteBuffer byteBuffer;
	private String name;

	private final File cacheFile;

	/**
	 * Creates a new {@link MultiImageMap}.
	 * 
	 * @param width
	 *            The width of the base image.
	 * @param height
	 *            The height of the base image.
	 * @param id
	 *            The id of the map.
	 * @see #addSequences(AdvancedDatFileReader, int[])
	 */
	public MultiImageMap(int width, int height, String id, String name) {
		this.width = width;
		this.height = height;
		this.name = name;
		File root = new File(ResourceManager.getResourcesDirectory(), "cache");
		cacheFile = new File(root, "cache-" + id);
	}

	private void allocateBuffers() {
		byteBuffer = ByteBuffer.allocateDirect(width * height * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		buffers = byteBuffer.asShortBuffer();
	}

	/**
	 * Adds a list of textures to this file. The images can be referenced by the image handles added to addTo.
	 * 
	 * @param dfr
	 *            The reader to read the textures from.
	 * @param sequenceIndexes
	 *            The indexes where the sequences start.
	 * @throws IOException
	 *             If the file could not be read.
	 */
	public synchronized void addSequences(AdvancedDatFileReader dfr, int[] sequenceIndexes) throws IOException {
		allocateBuffers();

		ImageMetadata settlermeta = new ImageMetadata();
		ImageMetadata reusableTorsometa = new ImageMetadata();
		for (int seqindex : sequenceIndexes) {
			long[] settlers = dfr.getSettlerPointers(seqindex);
			long[] torsos = dfr.getTorsoPointers(seqindex);

			for (int i = 0; i < settlers.length; i++) {
				ByteReader reader;
				reader = dfr.getReaderForPointer(settlers[i]);
				DatBitmapReader.uncompressImage(reader,
						dfr.getSettlerTranslator(), settlermeta,
						this);

				ImageMetadata torsometa;
				if (torsos != null) {
					torsometa = reusableTorsometa;
					reader = dfr.getReaderForPointer(torsos[i]);
					if (reader != null) {
						DatBitmapReader.uncompressImage(reader,
								dfr.getTorsoTranslator(),
								torsometa, this);
					}
				}
			}
		}

		// request a opengl rerender, or do it ourselves on the next image
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

	/**
	 * Checks if this image map is can be loaded from the cache instead of regenerating it.
	 * 
	 * @return <code>true</code> iff this file is cached.
	 */
	public synchronized boolean hasCache() {
		return cacheFile.isFile();
	}

	@Override
	public void startImage(int imageWidth, int imageHeight) throws IOException {
		if (this.width < drawx + imageWidth) {
			if (linebottom + imageHeight <= this.height) {
				linetop = linebottom;
				drawx = 0;
			} else {
				drawEnabled = false;
				System.err.println("Error adding image to texture: "
						+ "there is no space to open a new row");
				return;
			}
		}

		if (linetop + imageHeight < this.height) {
			drawEnabled = true;
			textureValid = false;
			drawpointer = drawx + linetop * this.width;
			drawx += imageWidth;
			linebottom = Math.max(linebottom, linetop + imageHeight);
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

	/**
	 * Gets the width of the underlying texture.
	 * 
	 * @return The width.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the underlying texture.
	 * 
	 * @return The height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the texture handle.
	 * 
	 * @param gl
	 *            The gl context to use when creating the texutre.
	 * @return A valid texture handle.
	 */
	public TextureHandle getTexture(GLDrawContext gl) {
		if (!textureValid || !texture.isValid()) {
			if (texture != null) {
				gl.deleteTexture(texture);
			}
			try {
				loadTexture(gl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	private synchronized void loadTexture(GLDrawContext gl) throws
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
		texture = gl.generateTexture(width, height, buffers, name);
		System.out.println("opengl Texture: " + texture
				+ ", thread: " + Thread.currentThread().toString());
		if (texture != null) {
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
