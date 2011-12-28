package jsettlers.graphics.reader;

import java.io.IOException;

public class ShortArrayWriter implements ImageArrayProvider {
	private static final short TRANSPARENT = 0;
	private short[] array;
	private int width;
	private int line;
	
	@Override
	public void startImage(int width, int height) throws IOException {
		if (width == 0 && height == 0) {
			array = new short[1];
		}
		this.width = width;
		array = new short[width * height];
	}

	@Override
    public void writeLine(short[] data, int linelength) throws IOException {
		int offset = line * width;
		for (int i = 0; i < linelength; i++) {
			array[offset + i] = data[i];
		}
		for (int i = linelength; i < width; i++) {
			array[offset + i] = TRANSPARENT;
		}
		
		line++;
	}
	
	public short[] getArray() {
	    return array;
    }

}
