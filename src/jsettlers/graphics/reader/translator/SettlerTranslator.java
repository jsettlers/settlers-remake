package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.ImageDataPrivider;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.reader.bytereader.ByteReader;

/**
 * This class translates settler images.
 * @author michael
 *
 */
public class SettlerTranslator implements DatBitmapTranslator<SettlerImage> {

	@Override
	public SettlerImage createImage(ImageDataPrivider data) {
		return new SettlerImage(data);
	}

	@Override
	public short getTransparentColor() {
		return 0x00;
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.DISPLACED;
	}

	@Override
	public short readUntransparentColor(ByteReader reader)
	        throws IOException {
		return (short) ((reader.read16() << 1) | 0x01);
	}

}
