package jsettlers.graphics.map.draw;

import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.DatFileSet;
import jsettlers.graphics.reader.SequenceList;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

/**
 * This is a image loader. It is currently unused.
 * 
 * @author michael
 */
public class ImageLoadTask implements Runnable {

	private static final DatFileSet EMPTY_SET = new DatFileSet() {
		@Override
		public SequenceList<SettlerImage> getSettlers() {
			return new SequenceList<SettlerImage>() {
				@Override
				public Sequence<SettlerImage> get(int index) {
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

	private final Hashtable<Integer, DatFileSet> imageSets;

	private final BlockingQueue<Integer> filesToLoad;

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
	        BlockingQueue<Integer> filesToLoad) {
		this.imageSets = imageSets;
		this.filesToLoad = filesToLoad;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Integer next = filesToLoad.take();

				int fileIndex = next.intValue();
				DatFileSet set = ImageProvider.getInstance().getFileReader(fileIndex);
				
				if (set == null) {
					set = EMPTY_SET;
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


}
