package jsettlers.graphics.reader;

import java.io.IOException;

import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.reader.bytereader.ByteReader;
import jsettlers.graphics.reader.translator.DatBitmapTranslator;

public interface DatFileReader extends DatFileSet {

	DatBitmapTranslator<LandscapeImage> getLandscapeTranslator();

	ByteReader getReaderForLandscape(int index) throws IOException;

	void generateImageMap(int width, int height, int[] sequences, String id) throws IOException;

}
