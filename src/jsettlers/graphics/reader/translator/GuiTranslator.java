package jsettlers.graphics.reader.translator;

import java.io.IOException;

import jsettlers.graphics.image.GuiImage;
import jsettlers.graphics.reader.ImageMetadata;
import jsettlers.graphics.reader.bytereader.ByteReader;

public class GuiTranslator implements DatBitmapTranslator<GuiImage> {

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
		return HeaderType.GUI;
	}

	@Override
    public GuiImage createImage(ImageMetadata metadata, short[] array) {
	    return new GuiImage(metadata, array);
    }

}
