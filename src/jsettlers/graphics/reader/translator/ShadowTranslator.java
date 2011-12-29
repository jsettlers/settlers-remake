package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.ShadowImage;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

/**
 * This class translates shadows.
 * @author michael
 *
 */
public class ShadowTranslator implements DatBitmapTranslator<ShadowImage> {
	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		return 0;
	}
	
	@Override
    public HeaderType getHeaderType() {
        return HeaderType.DISPLACED;
    }
	
	@Override
	public short getTransparentColor() {
		return 0x0001;
	}

	@Override
    public ShadowImage createImage(ImageMetadata metadata, short[] array) {
	    return new ShadowImage(metadata, array);
    }
}
