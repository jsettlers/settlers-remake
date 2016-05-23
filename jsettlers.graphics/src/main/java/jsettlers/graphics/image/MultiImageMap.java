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
import go.graphics.TextureHandle;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.Arrays;

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
<<<<<<< ours
 * This is a map of multiple images of one sequence. It always contains the settler image and the torso. This class allows packing the settler images
 * to a single, big texture.
=======
 * This is a map of multiple images of one sequence. It always contains the settler image and the torso
>>>>>>> theirs
 * 
 * @author Michael Zangl
 */
public class MultiImageMap implements ImageArrayProvider, GLPreloadTask {

	/**
	 * Change this every time you change the file format.
	 */
	protected static final int CACHE_MAGIC = 0x8273433;
	protected static final byte TYPE_IMAGE = 1;
	protected static final byte TYPE_NULL_IMAGE = 2;
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
	// TODO: Release this buffer once we sent it over to OpenGL and the cache file was written successfully. We can always read it back from the cache
	// file if we need to.
	private ByteBuffer byteBuffer;

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
	 * @see #addSequences(AdvancedDatFileReader, int[], Sequence[])
	 */
	public MultiImageMap(int width, int height, String id) {
		this.width = width;
		this.height = height;
		File root = new File(ResourceManager.getResourcesDirectory(), "cache");
		cacheFile = new File(root, "cache-" + id);
	}

	/**
	 * Load this texture. The texture may be loaded from either the cache file or the original file.
	 * 
	 * @param dfr
	 * @param sequenceIndexes
	 *            The sequences to load.
	 * @param addTo
	 */
	public void load(AdvancedDatFileReader dfr, int[] sequenceIndexes, Sequence<Image>[] addTo) {
		if (hasCache()) {
			ImageProvider.traceImageLoad("Cache for " + dfr + ": In cache");
			try {
				CacheFileReader cacheReader = new CacheFileReader(cacheFile);
				allocateBuffers();
				for (int s : sequenceIndexes) {
					// TODO: Check sequence index matches
					cacheReader.readImageSequence(this, addTo);
				}
				cacheReader.readTexture(byteBuffer);
				cacheReader.close();
				ImageProvider.traceImageLoad("Cache for " + dfr + ": Done loading cached");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				loadOriginal(dfr, sequenceIndexes, addTo);
			}
		} else {
			loadOriginal(dfr, sequenceIndexes, addTo);
		}
	}

	private void loadOriginal(AdvancedDatFileReader dfr, int[] sequenceIndexes, Sequence<Image>[] addTo) {
		ImageProvider.traceImageLoad("Cache for " + dfr + ": Loading original file");
		CacheFileWriter cacheWriter = new CacheFileWriter(cacheFile);
		cacheWriter.open();
		try {
			loadFromDatFile(dfr, sequenceIndexes, addTo, cacheWriter);
			cacheWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Error handling, do not leak cacheWriter
		}
		ImageProvider.traceImageLoad("Cache for " + dfr + ": Done, written to " + cacheFile + " => " + cacheFile.isFile());
	}

	private void loadFromDatFile(AdvancedDatFileReader dfr, int[] sequenceIndexes,
								 Sequence<Image>[] addTo, CacheFileWriter cacheWriter) throws IOException {
		ImageProvider.traceImageLoad("Preloading for file: " + dfr + " -> settlers -> " + Arrays.toString(sequenceIndexes));
		allocateBuffers();

		for (int seqindex : sequenceIndexes) {
			loadSequenceFromDatFile(dfr, addTo, seqindex, cacheWriter);
		}

		cacheWriter.writeTexture(byteBuffer);
		// request a opengl rerender, or do it ourselves on the next image
		textureValid = false;
		ImageProvider.getInstance().addPreloadTask(this);
	}
	private void loadSequenceFromDatFile(AdvancedDatFileReader dfr, Sequence<Image>[] addTo, int seqindex, CacheFileWriter cacheWriter)
			throws IOException
	{
		ImageMetadata settlermeta = new ImageMetadata();
		ImageMetadata torsometa = new ImageMetadata();
		long[] settlers = dfr.getSettlerPointers(seqindex);
		long[] torsos = dfr.getTorsoPointers(seqindex);

		Image[] images = new Image[settlers.length];
		cacheWriter.writeSequenceStart(seqindex, settlers.length);
		for (int i = 0; i < settlers.length; i++) {
			ImageProvider.traceImageLoad("Preload image: " + dfr + " -> settlers -> " + seqindex + " -> " + i);

			ByteReader reader;
			reader = dfr.getReaderForPointer(settlers[i]);
			DatBitmapReader.uncompressImage(reader, dfr.getSettlerTranslator(), settlermeta, this);
			if (settlermeta.width == 0 || settlermeta.height == 0) {
				ImageProvider.traceImageLoad("Using null image");
				cacheWriter.writeNullImage();
				images[i] = NullImage.getInstance();
				continue;
			}
			int settlerx = drawx - settlermeta.width;
			int settlery = linetop;

			int torsox = 0;
			int torsoy = 0;
			if (torsos != null) {
				reader = dfr.getReaderForPointer(torsos[i]);
				if (reader != null) {
					DatBitmapReader.uncompressImage(reader, dfr.getTorsoTranslator(), torsometa, this);
					torsox = drawx - torsometa.width;
					torsoy = linetop;
				}
			}
			ImageProvider.traceImageLoad("Allocated at settlerx = " + settlerx + ", settlery = " + settlery + ", w=" + settlermeta.width + ", h="
					+ settlermeta.height);

			MultiImageImage image = new MultiImageImage(this, settlermeta, settlerx, settlery, torsos == null || torsometa.width == 0
					|| torsometa.height == 0 ? null : torsometa, torsox, torsoy);
			images[i] = image;
			cacheWriter.writeImage(image);
		}
		addTo[seqindex] = new ArraySequence<Image>(images);
	}

	private void allocateBuffers() {
		byteBuffer = ByteBuffer.allocateDirect(width * height * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		buffers = byteBuffer.asShortBuffer();
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
				texture.delete();
			}
			try {
				loadTexture(gl);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return texture;
	}

	private synchronized void loadTexture(GLDrawContext gl) throws IOException,
			IOException {
		if (buffers == null) {
			readBufferFromCache();
		}

		buffers.rewind();
		texture = gl.generateTexture(width, height, buffers);
		ImageProvider.traceImageLoad("Allocated multi texture: " + texture
				+ ", thread: " + Thread.currentThread().toString());
		if (texture != null) {
			textureValid = true;
		}
		buffers = null;
		byteBuffer = null;
	}

	private void readBufferFromCache() throws FileNotFoundException, IOException {
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

	@Override
	public void run(GLDrawContext context) {
		getTexture(context);
	}

	private static class CacheFileWriter {
		private final File cacheFile;
		private final File tempFile;
		private DataOutputStream out;
		private boolean hadWriteError;

		public CacheFileWriter(File cacheFile) {
			super();
			this.cacheFile = cacheFile;
			tempFile = new File(cacheFile.getParentFile(), cacheFile.getName() + ".tmp");
		}

		public void open() {
			try {
				cacheFile.getParentFile().mkdirs();
				cacheFile.delete();
				out = new DataOutputStream(new FileOutputStream(tempFile));
				out.writeInt(CACHE_MAGIC);
			} catch (IOException e) {
				onWriteError(e);
			}
		}

		/**
		 * Write a sequence start.
		 * 
		 * @param seqIndex
		 *            The index of the sequence.
		 * @param imageCount
		 *            The number of {@link #writeImage(MultiImageImage)} calls that follow.
		 */
		public void writeSequenceStart(int seqIndex, int imageCount) {
			if (!hadWriteError) {
				try {
					if (imageCount == 0) {
						throw new IOException("There may be no empty sequences in chache files.");
					}
					out.writeInt(seqIndex);
					out.writeInt(imageCount);
				} catch (IOException e) {
					onWriteError(e);
				}
			}
		}

		public void writeImage(MultiImageImage image) {
			if (!hadWriteError) {
				try {
					out.writeByte(TYPE_IMAGE);
					image.writeTo(out);
				} catch (IOException e) {
					onWriteError(e);
				}
			}
		}

		public void writeNullImage() {
			if (!hadWriteError) {
				try {
					out.writeByte(TYPE_NULL_IMAGE);
				} catch (IOException e) {
					onWriteError(e);
				}
			}
		}

		public void writeTexture(ByteBuffer byteBuffer) {
			if (!hadWriteError) {
				try {
					byte[] line = new byte[4096];
					byteBuffer.rewind();
					out.writeInt(byteBuffer.remaining());
					while (byteBuffer.hasRemaining()) {
						int length = Math.min(line.length, byteBuffer.remaining());
						byteBuffer.get(line, 0, length);
						out.write(line, 0, length);
					}
				} catch (IOException e) {
					onWriteError(e);
				}
			}
		}

		private void onWriteError(IOException e) {
			hadWriteError = true;
			e.printStackTrace();
			// TODO: Error reporting.
		}

		public void close() {
			if (!hadWriteError) {
				try {
					out.close();

					tempFile.renameTo(cacheFile);
				} catch (IOException e) {
					onWriteError(e);
				}
			}
		}

	}

	private static class CacheFileReader {

		private final File cacheFile;
		private final DataInputStream in;

		public CacheFileReader(File cacheFile) throws IOException {
			this.cacheFile = cacheFile;
			in = new DataInputStream(new FileInputStream(cacheFile));

			int magic = in.readInt();
			if (magic != CACHE_MAGIC) {
				throw new IOException("Invalid cache magic byte: " + magic);
			}
		}

		public void readImageSequence(MultiImageMap map, Sequence<Image>[] addTo) throws IOException {
			int seqIndex = in.readInt();
			int imageCount = in.readInt();
			if (imageCount == 0) {
				throw new IOException("There may be no empty sequences in chache files.");
			}
			ImageProvider.traceImageLoad("Reading sequence from cache " + seqIndex + ", length=" + imageCount);

			Image[] images = new Image[imageCount];
			for (int i = 0; i < imageCount; i++) {
				byte type = in.readByte();
				switch (type) {
				case TYPE_IMAGE:
					images[i] = MultiImageImage.readFrom(map, in);
					break;
				case TYPE_NULL_IMAGE:
					images[i] = NullImage.getInstance();
					break;
				default:
					throw new IOException("Unsupported image type: " + type);
				}
			}
			addTo[seqIndex] = new ArraySequence<Image>(images);
		}

		public void readTexture(ByteBuffer byteBuffer) throws IOException {
			byteBuffer.rewind();
			int available = in.readInt();
			if (byteBuffer.remaining() != available) {
				throw new IOException("Buffer lengths do not match. Expected " + byteBuffer.remaining() + " got " + available);
			}
			byte[] line = new byte[4096];
			while (byteBuffer.hasRemaining()) {
				int length = Math.min(line.length, byteBuffer.remaining());
				in.read(line, 0, length);
				byteBuffer.put(line, 0, length);
			}
		}

		public void close() throws IOException {
			in.close();
		}

	}
}
