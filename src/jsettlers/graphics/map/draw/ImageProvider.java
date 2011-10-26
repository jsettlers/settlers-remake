package jsettlers.graphics.map.draw;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.NullImage;
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
	private static final int THREADS = 3;

	private static ImageProvider instance;

	private Hashtable<Integer, DatFileSet> images =
	        new Hashtable<Integer, DatFileSet>();

	private BitSet requestedFiles = new BitSet();

	/**
	 * The lookup paths for the dat files.
	 */
	private List<File> lookupPaths = new ArrayList<File>();

	private final BlockingQueue<Integer> filesToLoad =
	        new LinkedBlockingQueue<Integer>();

	private ImageProvider() {
		for (int i = 0; i < THREADS; i++) {
			Thread imageLoaderThread =
			        new Thread(new ImageLoadTask(images, filesToLoad,
			                lookupPaths), "image loader");
			imageLoaderThread.setDaemon(true);
			imageLoaderThread.start();
		}
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
		DatFileSet set = tryGetFileSet(file);
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
	private DatFileSet tryGetFileSet(int file) {
		Integer valueOf = Integer.valueOf(file);
		loadDatFile(valueOf);

		DatFileSet set = this.images.get(valueOf);
		return set;
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
	public Image getLandscapeImage(int file, int seqnumber) {
		DatFileSet set = tryGetFileSet(file);

		if (set != null) {
			Sequence<LandscapeImage> landscapes = set.getLandscapes();
			if (seqnumber < landscapes.length()) {
				return landscapes.getImageSafe(seqnumber);
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
	public Image getGuiImage(int file, int seqnumber) {
		DatFileSet set = tryGetFileSet(file);

		if (set != null) {
			return set.getGuis().getImageSafe(seqnumber);
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
		} else if (link.getType() == EImageLinkType.GUI) {
			return getGuiImage(link.getFile(), link.getSequence());
		} else if (link.getType() == EImageLinkType.LANDSCAPE) {
			return getLandscapeImage(link.getFile(), link.getSequence());
		} else {
			return getSettlerSequence(link.getFile(), link.getSequence())
			        .getImageSafe(link.getImage());
		}
	}

	/**
	 * tries to load a dat file by a given number.
	 * 
	 * @param number
	 *            The number of the dat file.
	 * @return true on success.
	 */
	private void loadDatFile(int number) {
		synchronized (requestedFiles) {
			Integer key = Integer.valueOf(number);
			if (!requestedFiles.get(number)) {
				this.filesToLoad.offer(key);
				requestedFiles.set(number);
			}
		}
	}

	/**
	 * preloads the given file
	 * 
	 * @param filenumber
	 */
	public void preload(int filenumber) {
		loadDatFile(filenumber);
	}

	public void waitForPreload(int filenumber) {
		preload(filenumber);
		synchronized (images) {
			while (!isPreloaded(filenumber)) {
				try {
					images.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean isPreloaded(int filenumber) {
		return images.containsKey(Integer.valueOf(filenumber));
	}

}
