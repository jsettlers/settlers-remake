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
package jsettlers.graphics.map.draw;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


import jsettlers.common.images.DirectImageLink;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
//import jsettlers.common.images.TextureMap;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.ImageIndexFile;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileSet;
import jsettlers.graphics.reader.DatFileType;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is the main image provider. It provides access to all images.
 * <p>
 * Settlers supports two image modes, one rgb mode (555 bits) and one rgb mode (565 bits).
 * 
 * @author michael
 */
public final class ImageProvider {
	private static final String FILE_PREFIX = "siedler3_";
	private static final int LAST_SEQUENCE_NUMBER = 2;
	private static final List<Integer> HIGHRES_IMAGE_FILE_NUMBERS = Arrays.asList(3, 14);

	private final Queue<GLPreloadTask> tasks = new ConcurrentLinkedQueue<GLPreloadTask>();
	private ImageIndexFile indexFile = null;

	private static final DatFileSet EMPTY_SET = new DatFileSet() {
		@Override
		public SequenceList<Image> getSettlers() {
			return new SequenceList<Image>() {
				@Override
				public Sequence<Image> get(int index) {
					return null;
				}

				@Override
				public int size() {
					return 0;
				}
			};
		}

		@Override
		public Sequence<LandscapeImage> getLandscapes() {
			return new ArraySequence<LandscapeImage>(new LandscapeImage[0]);
		}

		@Override
		public Sequence<GuiImage> getGuis() {
			return new ArraySequence<GuiImage>(new GuiImage[0]);
		}
	};

	/**
	 * Enable image loading trace.
	 */
	private static final boolean TRACE_IMAGE_LOADING = true;

	private static ImageProvider instance;

	private final Hashtable<Integer, AdvancedDatFileReader> readers = new Hashtable<Integer, AdvancedDatFileReader>();

	/**
	 * The lookup paths for the dat files.
	 */
	private final List<File> lookupPaths = new ArrayList<File>();

	private ImageProvider() {
	}

	/**
	 * Gets an instance of an image provider
	 * 
	 * @return The provider
	 */
	public static ImageProvider getInstance() {
		if (instance == null) {
			instance = new ImageProvider();
		}
		return instance;
	}

	/**
	 * Adds a new path to look for dat files.
	 *
	 * @param path
	 *            The directory. It may not exist, but must not be null.
	 * @return this
	 */
	public ImageProvider addLookupPath(File path) {
		this.lookupPaths.add(path);
		return this;
	}

	/**
	 * Tries to get a file content.
	 * 
	 * @param file
	 *            The file number to search for.
	 * @return The content as set or <code> null </code>
	 */
	public synchronized AdvancedDatFileReader getFileReader(int file) {
		Integer integer = Integer.valueOf(file);
		AdvancedDatFileReader set = this.readers.get(integer);
		if (set == null) {
			set = createFileReader(file);
			if (set != null) {
				this.readers.put(integer, set);
			}
		}
		return set;
	}

	public synchronized DatFileSet getFileSet(int file) {
		AdvancedDatFileReader set = getFileReader(file);
		if (set != null) {
			return set;
		} else {
			return EMPTY_SET;
		}
	}

	/**
	 * Gets an image by a link.
	 *
	 * @param link
	 *            The link that describes the image
	 * @return The image or a null image.
	 */
	public Image getImage(ImageLink link) {
		return getImage(link, -1, -1);
	}

	/**
	 * Gets the highest resolution image that fits the given size.
	 *
	 * @param link
	 *            The link that describes the image
	 * @param width
	 *            The width the image should have (at least).
	 * @param height
	 *            The height the image should have (at least).
	 * @return The image or a null image.
	 */
	public Image getImage(ImageLink link, float width, float height) {
		if (link == null) {
			return NullImage.getInstance();
		} else if (link instanceof DirectImageLink) {
			return getDirectImage((DirectImageLink) link);
		} else {
			OriginalImageLink olink = (OriginalImageLink) link;
			if (olink.getType() == EImageLinkType.LANDSCAPE) {
				return getLandscapeImage(olink.getFile(), olink.getSequence());
			} else {
				return getDetailedImage(olink, width, height);
			}
		}
	}

	/**
	 * Returns a GUI or SETTLER type image and if available a higher resolution version. This is also based on whether the image's dimensions in
	 * pixels will fit into both the specified width and height. This is so that an image is always scaled up as downsizing an image can introduce
	 * artifacts and it would be wasteful to be calculating the translation of excess pixels from a large image to a smaller one. However should the
	 * smallest image be oversized it will still be returned.
	 */
	private Image getDetailedImage(OriginalImageLink link, float width, float height) {
		Image image = getSequencedImage(link, 0);
		if (!HIGHRES_IMAGE_FILE_NUMBERS.contains(link.getFile())) { // Higher resolution images are only available in some files.
			return image;
		}
		int sequenceNumber = 0;
		Image higherResImg = image;
		while (higherResImg.getWidth() < width && higherResImg.getHeight() < height) {
			image = higherResImg;
			if (++sequenceNumber > LAST_SEQUENCE_NUMBER) {
				break;
			}
			higherResImg = getSequencedImage(link, sequenceNumber);
		}
		return image;
	}

	/**
	 * Expects a valid sequence number.
	 *
	 * @param link
	 * @param sequenceNumber
	 *            must be an integer from 0 to 2.
	 * @return the image matching the specified indexes.
	 */
	private Image getSequencedImage(OriginalImageLink link, int sequenceNumber) {
		if (link.getType() == EImageLinkType.SETTLER) {
			return getSettlerSequence(link.getFile(), link.getSequence()).getImageSafe(link.getImage() + sequenceNumber);
		} else {
			return getGuiImage(link.getFile(), link.getSequence() + sequenceNumber);
		}
	}

	private Image getDirectImage(DirectImageLink link) {
		if (indexFile == null) {
			indexFile = new ImageIndexFile();
		}

		int index = 0; //TextureMap.getIndex(link.getName());
		return indexFile.getImage(index);
	}

	/**
	 * Gets a landscape texture.
	 *
	 * @param file
	 *            The file number it is in.
	 * @param seqnumber
	 *            It's sequence number.
	 * @return The image, or an empty image.
	 */
	private SingleImage getLandscapeImage(int file, int seqnumber) {
		DatFileSet set = getFileSet(file);

		if (set != null) {
			Sequence<LandscapeImage> landscapes = set.getLandscapes();
			if (seqnumber < landscapes.length()) {
				return (SingleImage) landscapes.getImageSafe(seqnumber);
			}
		}
		return NullImage.getInstance();
	}

	/**
	 * Gets a given gui image.
	 *
	 * @param file
	 *            The file the image is in.
	 * @param seqnumber
	 *            The image number.
	 * @return The image.
	 */
	public SingleImage getGuiImage(int file, int seqnumber) {
		DatFileSet set = getFileSet(file);

		if (set != null) {
			return (SingleImage) set.getGuis().getImageSafe(seqnumber);
		} else {
			return NullImage.getInstance();
		}
	}

	/**
	 * Gets an settler sequence.
	 *
	 * @param file
	 *            The file of the sequence.
	 * @param seqnumber
	 *            The number of the sequence in the file.
	 * @return The settler sequence.
	 */
	public Sequence<? extends Image> getSettlerSequence(int file, int seqnumber) {
		DatFileSet set = getFileSet(file);
		if (set != null && set.getSettlers().size() > seqnumber) {
			return set.getSettlers().get(seqnumber);
		} else {
			return ArraySequence.getNullSequence();
		}
	}

	/**
	 * marks all loaded images as invalid. TODO: ensure that they get deleted
	 */
	public void invalidateAll() {
		readers.clear();
		Background.invalidateTexture();
	}

	private File findFileInPaths(String fileName) {
		for (File path : this.lookupPaths) {
			File searched = new File(path, fileName);
			if (searched.isFile() && searched.canRead()) {
				return searched;
			}
		}
		return null;
	}

	private AdvancedDatFileReader createFileReader(int fileIndex) {
		String numberString = String.format("%02d", fileIndex);
		for (DatFileType type : DatFileType.values()) {
			String fileName = FILE_PREFIX + numberString + type.getFileSuffix();
			if (TRACE_IMAGE_LOADING) {
				traceImageLoad("Searching file " + fileName);
			}

			File file = findFileInPaths(fileName);

			if (file != null) {
				if (TRACE_IMAGE_LOADING) {
					traceImageLoad("Opening file " + fileName);
				}
				return new AdvancedDatFileReader(file, type);
			}
		}
		System.err.println("Could not find/load graphic file " + numberString);
		return null;
	}

	/**
	 * Starts preloading the images, if lookup paths have been set.
	 * 
	 * @return
	 */
	public Thread startPreloading() {
		if (!lookupPaths.isEmpty()) {
			Thread thread = new Thread(new ImagePreloadTask(), "image preloader");
			thread.start();
			traceImageLoad("Started image preload thread with id=" + thread.getId());
			return thread;
		} else {
			traceImageLoad("No lookup path set. Image preloading failed.");
			return null;
		}
	}

	/**
	 * Adds a preload task that is executed on the OpenGl thread with a opengl context.
	 * <p>
	 * The task may never be executed.
	 */
	public void addPreloadTask(GLPreloadTask task) {
		traceImageLoad("Scheduling preload task " + task);
		tasks.add(task);
	}

	public static void traceImageLoad(String message) {
		if (TRACE_IMAGE_LOADING) {
			System.out.println("[IMAGES] " + message);
		}
	}
}
