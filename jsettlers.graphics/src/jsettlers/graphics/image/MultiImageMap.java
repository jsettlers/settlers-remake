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

/**
 * This is a map of multiple images of one sequence. It always contains the settler image and the torso
 * 
 * @author Michael Zangl
 */
public class MultiImageMap implements ImageArrayProvider, GLPreloadTask {

	/**
	 * Change this every time you change the file format.
	 */
	protected static final int CACHE_MAGIC = 0x8273434;
	protected static final byte TYPE_IMAGE = 1;
	protected static final byte TYPE_NULL_IMAGE = 2;
	protected final MultiImageMapSpecification specification;
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

	public MultiImageMap(MultiImageMapSpecification specification) {
		this.specification = specification;
		specification.bake();
		File root = new File(ResourceManager.getResourcesDirectory(), "cache");
		cacheFile = new File(root, "cache-" + specification.id);
	}

	/**
	 * Load this texture. The texture may be loaded from either the cache file or the original file.
	 * <p>
	 * The backing buffer is filled with the texture.
	 * 
	 * @param dfr
	 *            The Dat-File-Reader to use for loading this image.
	 * @param sequenceIndexes
	 *            The sequences to load.
	 * @param addTo
	 *            The sequence to add the loaded images to.
	 */
	public void load() {
		if (hasCache()) {
			loadFromCache();
		} else {
			loadOriginal();
		}
		// request a opengl rerender, or do it ourselves on the next image
		// request
		textureValid = false;
		ImageProvider.getInstance().addPreloadTask(this);
	}

	private void loadFromCache() {
		ImageProvider.traceImageLoad("Cache for " + specification + ": In cache");
		try {
			CacheFileReader cacheReader = new CacheFileReader(cacheFile);
			allocateBuffers();
			for (int i = 0; i < specification.size(); i++) {
				AdvancedDatFileReader dfr = specification.getReader(i);
				cacheReader.readImageSequence(this, dfr);
			}
			cacheReader.readTexture(byteBuffer);
			cacheReader.close();
			ImageProvider.traceImageLoad("Cache for " + specification + ": Done loading cached");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			loadOriginal();
		}
	}

	private void loadOriginal() {
		ImageProvider.traceImageLoad("Cache for " + specification + ": Loading original file");
		CacheFileWriter cacheWriter = new CacheFileWriter(cacheFile);
		cacheWriter.open();
		try {
			loadFromDatFile(cacheWriter);
			cacheWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: Error handling, do not leak cacheWriter
		}
		ImageProvider.traceImageLoad("Cache for " + specification + ": Done, written to " + cacheFile + " => " + cacheFile.isFile());
	}

	private void loadFromDatFile(
			CacheFileWriter cacheWriter) throws IOException {
		ImageProvider.traceImageLoad("Preloading for file: " + specification);
		allocateBuffers();

		for (int i = 0; i < specification.size(); i++) {
			loadSequenceFromDatFile(i, cacheWriter);
		}

		cacheWriter.writeTexture(byteBuffer);
	}

	protected void loadSequenceFromDatFile(int index, CacheFileWriter cacheWriter)
			throws IOException {
		AdvancedDatFileReader dfr = specification.getReader(index);
		int seqIndex = specification.getSequence(index);

		ImageMetadata settlermeta = new ImageMetadata();
		ImageMetadata torsometa = new ImageMetadata();
		long[] settlers = dfr.getSettlerPointers(seqIndex);
		long[] torsos = dfr.getTorsoPointers(seqIndex);

		Image[] images = new Image[settlers.length];
		cacheWriter.writeSequenceStart(seqIndex, settlers.length);
		for (int i = 0; i < settlers.length; i++) {
			ImageProvider.traceImageLoad("Preload image: " + dfr + " -> settlers -> " + seqIndex + " -> " + i);

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

			MultiImageImage image = generateImage(settlermeta, torsos == null || torsometa.width == 0
					|| torsometa.height == 0 ? null : torsometa, settlerx, settlery, torsox, torsoy);
			cacheWriter.writeImage(image);
			images[i] = image;
		}
		dfr.pushSettlerSequence(seqIndex, new ArraySequence<Image>(images));
	}

	protected MultiImageImage generateImage(ImageMetadata settlermeta, ImageMetadata torsometa, int settlerx, int settlery,
			int torsox,
			int torsoy) {
		return new MultiImageImage(this, settlermeta, settlerx, settlery, torsometa, torsox, torsoy);
	}

	private void allocateBuffers() {
		byteBuffer = ByteBuffer.allocateDirect(specification.width * specification.height * 2);
		byteBuffer.order(ByteOrder.nativeOrder());
		buffers = byteBuffer.asShortBuffer();
		ImageProvider.traceImageLoad("Allocate: " + specification);
	}

	public synchronized boolean hasCache() {
		return cacheFile.isFile();
	}

	@Override
	public void startImage(int width, int height) throws IOException {
		if (specification.width < drawx + width) {
			if (linebottom + height <= specification.height) {
				linetop = linebottom;
				drawx = 0;
			} else {
				drawEnabled = false;
				System.err.println("Error adding image to texture: "
						+ "there is no space to open a new row");
				return;
			}
		}

		if (linetop + height < specification.height) {
			drawEnabled = true;
			textureValid = false;
			drawpointer = drawx + linetop * specification.width;
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
			drawpointer = dp + specification.width;
		}
	}

	public int getWidth() {
		return specification.width;
	}

	public int getHeight() {
		return specification.height;
	}

	/**
	 * Gets the texture index.
	 * 
	 * @param gl
	 * @return
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
			load();
		}

		buffers.rewind();
		texture = gl.generateTexture(specification.width, specification.height, buffers);
		ImageProvider.traceImageLoad("Allocated multi texture: " + texture
				+ ", thread: " + Thread.currentThread().toString());
		if (texture != null) {
			textureValid = true;
		}
		buffers = null;
		byteBuffer = null;
		ImageProvider.traceImageLoad("Deallocate: " + specification);
	}

	@Override
	public void run(GLDrawContext context) {
		getTexture(context);
	}

	protected static class CacheFileWriter {
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

		public void readImageSequence(MultiImageMap map, AdvancedDatFileReader addTo) throws IOException {
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
			addTo.pushSettlerSequence(seqIndex, new ArraySequence<Image>(images));
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

	/**
	 * This defines what to display on the {@link MultiImageMap}
	 * 
	 * @author Michael Zangl
	 */
	public static class MultiImageMapSpecification {
		private final int width;
		private final int height;

		private final String id;

		/**
		 * An array of (file, sequence) pairs.
		 */
		private int[] sequences = new int[64];
		private int sequenceCount = 0;

		private boolean baked = false;

		public MultiImageMapSpecification(int width, int height, String id) {
			super();
			this.width = width;
			this.height = height;
			this.id = id;
		}

		public void add(int file, int sequence) {
			if (baked) {
				throw new IllegalStateException("Cannot add any more images.");
			}

			if (sequenceCount * 2 >= sequences.length) {
				sequences = Arrays.copyOf(sequences, sequences.length * 2);
			}
			sequences[sequenceCount * 2] = file;
			sequences[sequenceCount * 2 + 1] = sequence;
			sequenceCount++;
		}

		public int size() {
			return sequenceCount;
		}

		private AdvancedDatFileReader getReader(int index) throws IOException {
			int file = getFile(index);
			AdvancedDatFileReader reader = ImageProvider.getInstance().getFileReader(file);
			if (reader == null) {
				throw new IOException("Could not create reader for " + file);
			}
			return reader;
		}

		public int getFile(int index) {
			return sequences[index * 2];
		}

		public int getSequence(int index) {
			return sequences[index * 2 + 1];
		}

		public void bake() {
			baked = true;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MultiImageMapSpecification [width=");
			builder.append(width);
			builder.append(", height=");
			builder.append(height);
			builder.append(", id=");
			builder.append(id);
			builder.append("]");
			return builder.toString();
		}
	}
}
