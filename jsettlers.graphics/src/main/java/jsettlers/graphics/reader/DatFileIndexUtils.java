package jsettlers.graphics.reader;

import jsettlers.common.images.AnimationSequence;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.sequence.Sequence;

/**
 * Different settlers versions use different image indexes.
 * <p>
 * In the game, we always reference the image indexes of the gold edition.
 * <p>
 * This utility class holds the information about which index remapping to use for which file.
 *
 * @author michael
 */
public class DatFileIndexUtils {

	private DatFileIndexUtils() {
	}

	private static class FakeShipsDatFile extends EmptyDatFile {
		private final ImageProvider fallback;

		private FakeShipsDatFile(ImageProvider fallback) {
			this.fallback = fallback;
		}

		@Override
		public SequenceList<Image> getSettlers() {
			return new SequenceList<Image>() {
				@Override
				public int size() {
					// TODO @andreas: Change this to the max number of sequences in the ship file
					return 1000;
				}

				@Override
				public Sequence<Image> get(int index) {
					// TODO @andreas: implement mapping
					// You can use your own implementation instead of WrappedAnimation if you need more complex rewrites
					if (index == 0) {
						// e.g. "cargo_ship_body", 0, 6
						// sequence length needs to be 6 (6 directions)
						return new WrappedAnimation(fallback, new AnimationSequence("ready", 0, 2));
					} else if (index == 2) {
						// e.g. "cargo_ship_front", 0, 6
						return new WrappedAnimation(fallback, new AnimationSequence("ready", 0, 2));
					} else if (index == 3) {
						// e.g. "cargo_ship_construction", 0, 6
						return new WrappedAnimation(fallback, new AnimationSequence("ready", 0, 2));
					} else {
						return new WrappedAnimation(fallback, new AnimationSequence("ready", 0, 2));
					}
				}
			};
		}
	}

	/**
	 * Returns a reader that uses gold version indexes
	 *
	 * @param fileIndex
	 * @param reader
	 * 		The reader, using any index (auto-detected)
	 * @return
	 */
	public static DatFileReader autoTranslate(int fileIndex, DatFileReader reader, ImageProvider fallback) {
		if (fileIndex == 36 && reader.getSettlers().size() == 0) {
			// No ships available
			return new FakeShipsDatFile(fallback);
		}
		return reader;
	}

}
