package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.LandscapeImage;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

public class LandscapeTranslator implements DatBitmapTranslator<LandscapeImage> {
	@Override
	public short getTransparentColor() {
		return 0;
	}

	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		return (short) ((reader.read16() << 1) | 0x01);
	}

	@Override
    public HeaderType getHeaderType() {
        return HeaderType.LANDSCAPE;
    }

	@Override
    public LandscapeImage createImage(ImageMetadata metadata, short[] array) {
	    return new LandscapeImage(metadata, array);
    }
}
