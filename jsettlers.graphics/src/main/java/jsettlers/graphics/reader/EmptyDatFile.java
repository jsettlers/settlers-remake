package jsettlers.graphics.reader;

import java.io.IOException;
import java.io.RandomAccessFile;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.reader.translator.DatBitmapTranslator;
import jsettlers.graphics.reader.translator.LandscapeTranslator;
import jsettlers.graphics.sequence.ArraySequence;
import jsettlers.graphics.sequence.Sequence;

public class EmptyDatFile implements DatFileReader {
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
		return new ArraySequence<>(new LandscapeImage[0]);
	}

	@Override
	public Sequence<GuiImage> getGuis() {
		return new ArraySequence<>(new GuiImage[0]);
	}

	@Override
	public DatBitmapTranslator<LandscapeImage> getLandscapeTranslator() {
		return new LandscapeTranslator(DatFileType.RGB555);
	}

	@Override
	public ByteReader getReaderForLandscape(int index) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void generateImageMap(int width, int height, int[] sequences, String id) throws IOException {
		throw new UnsupportedOperationException();
	}

}
