package jsettlers.graphics.reader;

import jsettlers.common.Color;

public enum DatFileType {
	RGB555(".7c003e01f.dat", new byte[] {
			(byte) 0x7c,
			0x00,
			0x00,
			(byte) 0xe0,
			0x03, }),
	RGB565(".f8007e01f.dat", new byte[] {
			(byte) 0xf8,
			0x00,
			0x00,
			(byte) 0xe0,
			0x07, }), ;

	private final String fileSuffix;
	private final byte[] startMagic;

	DatFileType(String fileSuffix, byte[] startMagic) {
		this.fileSuffix = fileSuffix;
		this.startMagic = startMagic;
	}

	public String getFileSuffix() {
		return fileSuffix;
	}

	public byte[] getFileStartMagic() {
		return startMagic;
	}

	/**
	 * Converts a color in the current format to a rgba 5551 color.
	 * 
	 * @param color
	 * @return
	 */
	public short convertTo5551(int color) {
		if (this == RGB565) {
			color = (short) Color.convert565to555(color);
		}
		return (short) ((color << 1) | 0x01);
	}
}
