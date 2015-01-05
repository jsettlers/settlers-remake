package jsettlers.graphics.map.draw;

import go.graphics.GLDrawContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.images.DirectImageLink;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.images.TextureMap;
import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.ImageIndexFile;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.NullImage;
import jsettlers.graphics.image.SingleImage;
import jsettlers.graphics.reader.AdvancedDatFileReader;
import jsettlers.graphics.reader.DatFileSet;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is the main image provider. It provides access to all images.
 * 
 * @author michael
 */
public final class ImageProvider {
	private static final String FILE_SUFFIX = ".7c003e01f.dat";

	private static final String FILE_PREFIX = "siedler3_";

	private Queue<GLPreloadTask> tasks =
			new ConcurrentLinkedQueue<GLPreloadTask>();

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

	private static ImageProvider instance;

	private Hashtable<Integer, AdvancedDatFileReader> readers =
			new Hashtable<Integer, AdvancedDatFileReader>();

	/**
	 * The lookup paths for the dat files.
	 */
	private List<File> lookupPaths = new ArrayList<File>();

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
	 */
	public void addLookupPath(File path) {
		this.lookupPaths.add(path);
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
	 * Gets a landscape texture.
	 * 
	 * @param file
	 *            The file number it is in.
	 * @param seqnumber
	 *            It's sequence number.
	 * @return The image, or an empty image.
	 */
	public SingleImage getLandscapeImage(int file, int seqnumber) {
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
	 * Gets an image by a link.
	 * 
	 * @param link
	 *            The link that describes the image
	 * @return The image or a null image.
	 */
	public Image getImage(ImageLink link) {
		if (link == null) {
			return NullImage.getInstance();
		} else if (link instanceof DirectImageLink) {
			return getDirectImage((DirectImageLink) link);
		} else {
			OriginalImageLink olink = (OriginalImageLink) link;
			if (olink.getType() == EImageLinkType.GUI) {
				return getGuiImage(olink.getFile(), olink.getSequence());
			} else if (olink.getType() == EImageLinkType.LANDSCAPE) {
				return getLandscapeImage(olink.getFile(), olink.getSequence());
			} else {
				return getSettlerSequence(olink.getFile(), olink.getSequence())
						.getImageSafe(olink.getImage());
			}
		}
	}

	private Image getDirectImage(DirectImageLink link) {
		if (indexFile == null) {
			indexFile = new ImageIndexFile();
		}

		int index = TextureMap.getIndex(link.getName());
		return indexFile.getImage(index);
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
		String fileName = FILE_PREFIX + numberString + FILE_SUFFIX;

		File file = findFileInPaths(fileName);

		if (file != null) {
			return new AdvancedDatFileReader(file);
		} else {
			System.err.println("Could not find/load file " + fileName);
			return null;
		}
	}

	public Thread startPreloading() {
		Thread thread = new Thread(new ImagePreloadTask(), "image preloader");
		thread.start();
		return thread;
	}

	/**
	 * Adds a preload task that is executed on the OpenGl thread with a opengl context.
	 * <p>
	 * The task may never be executed.
	 */
	public void addPreloadTask(GLPreloadTask task) {
		tasks.add(task);
	}

	public void runPreloadTasks(GLDrawContext context) {
		GLPreloadTask task;
		while ((task = tasks.poll()) != null) {
			System.out.println("running opengl preload task");
			task.run(context);
		}
	}
}
