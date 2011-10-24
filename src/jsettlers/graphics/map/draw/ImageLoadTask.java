package jsettlers.graphics.map.draw;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.image.Torso;
import jsettlers.graphics.reader.DatFileReader;
import jsettlers.graphics.reader.DatFileSet;
import jsettlers.graphics.sequence.Sequence;

public class ImageLoadTask implements Runnable {

	private static final String FILE_SUFFIX = ".7c003e01f.dat";

	private static final String FILE_PREFIX = "siedler3_";

	private static final DatFileSet EMPTY_SET = new DatFileSet() {
		@Override
		public List<Sequence<Torso>> getTorsos() {
			return Collections.emptyList();
		}
		
		@Override
		public List<Sequence<ShadowImage>> getShadows() {
			return Collections.emptyList();
		}
		
		@Override
		public List<Sequence<SettlerImage>> getSettlers() {
			return Collections.emptyList();
		}
		
		@Override
		public List<Sequence<LandscapeImage>> getLandscapes() {
			return Collections.emptyList();
		}
		
		@Override
		public List<Sequence<GuiImage>> getGuis() {
			return Collections.emptyList();
		}
	};

	private final Hashtable<Integer, DatFileSet> imageSets;

	private final BlockingQueue<Integer> filesToLoad;

	private final List<File> lookupPaths;

	/**
	 * Creates a new program that loads the images
	 * 
	 * @param imageSets
	 *            The set the images should be put into
	 * @param filesToLoad
	 *            A Queue that offers the files, should be blocking
	 * @param lookupPaths
	 */
	public ImageLoadTask(Hashtable<Integer, DatFileSet> imageSets,
	        BlockingQueue<Integer> filesToLoad, List<File> lookupPaths) {
		this.imageSets = imageSets;
		this.filesToLoad = filesToLoad;
		this.lookupPaths = lookupPaths;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Integer next = filesToLoad.take();

				String numberString = String.format("%02d", next.intValue());
				String fileName = FILE_PREFIX + numberString + FILE_SUFFIX;

				File file = findFileInPaths(fileName);

				DatFileSet set = null;
				if (file != null) {
					set = loadDatFile(file);
				}
				if (set == null) {
					set = EMPTY_SET;
					System.err.println("Could not find/load file " + fileName);
				}
				synchronized (imageSets) {
					this.imageSets.put(next, set);
					this.imageSets.notifyAll();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private DatFileSet loadDatFile(File file) {
		try {
			DatFileReader datReader = new DatFileReader(file);

			return datReader.getDatFileSet();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
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

}
