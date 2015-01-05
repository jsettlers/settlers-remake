package jsettlers.mapcreator.tools.buffers;

public class GlobalShapeBuffer {
	private final int width;
	private final int height;
	private final byte[][] data;

	public GlobalShapeBuffer(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new byte[width][height];
	}

	public byte[][] getArray(int usedminx, int usedminy, int usedmaxx,
			int usedmaxy) {
		if (usedminy < 0) {
			usedminy = 0;
		}
		if (usedmaxy >= height) {
			usedmaxy = height - 1;
		}
		if (usedminx < 0) {
			usedminx = 0;
		}
		if (usedmaxx >= width) {
			usedmaxx = width - 1;
		}

		for (int y = usedminy; y < usedmaxy; y++) {
			for (int x = usedminx; x < usedmaxx; x++) {
				data[x][y] = 0;
			}
		}
		return data;
	}

	public byte[][] getArray(int x, int y, int radius) {
		return getArray(x - radius, y - radius, x + radius, y + radius);
	}
}
