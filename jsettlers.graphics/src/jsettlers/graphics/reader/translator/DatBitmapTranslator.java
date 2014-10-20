package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.Image;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

/**
 * This interfaces defines methods that a reader for bitmaps in dat files must
 * provide to convert the dat files to opengl images.
 * 
 * @author michael
 */
public interface DatBitmapTranslator<T extends Image> {

	/**
	 * Returns whether bitmaps of this translator use a long header with offsetX
	 * and Y.
	 * 
	 * @return true The header type the image uses..
	 */
	HeaderType getHeaderType();

	/**
	 * Reads a color from the reader and progresses the reader so that it stands
	 * after the color.
	 * 
	 * @param reader
	 *            THe reader to read from
	 * @return A short indicating the color, e.g. in 5-5-5-1 RGBA format.
	 * @throws IOException If an error occured.
	 */
	short readUntransparentColor(ByteReader reader)
	        throws IOException;

	/**
	 * gets the color that is used as transparent.
	 * @return A short indicating the color.
	 */
	short getTransparentColor();

	/**
	 * Creates a image of the given type.
	 * @param data The data.
	 * @return The image
	 */
	T createImage(ImageMetadata metadata, short[] array);
	
}
