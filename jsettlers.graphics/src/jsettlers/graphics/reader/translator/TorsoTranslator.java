package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.Torso;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

public class TorsoTranslator implements DatBitmapTranslator<Torso> {
	@Override
	public short getTransparentColor() {
		return 0x00;
	}

	@Override
	public short readUntransparentColor(ByteReader reader) throws IOException {
		int read = (reader.read8() & 0x1f); // only 5 bit.
		return (short) (read << 11 | read << 6 | read << 1 | 0x01);
		// return (short) ((read & 0xff) | 0xff00);
	}

	@Override
	public HeaderType getHeaderType() {
		return HeaderType.DISPLACED;
	}

	@Override
	public Torso createImage(ImageMetadata metadata, short[] array) {
		return new Torso(metadata, array);
	}
}
