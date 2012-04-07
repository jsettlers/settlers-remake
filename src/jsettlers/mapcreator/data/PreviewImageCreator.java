package jsettlers.mapcreator.data;

import jsettlers.common.map.IMapData;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.save.MapFileHeader;

/**
 * This class creates a preview image of a map to be saved in the map header.
 * 
 * @author michaelz
 */
public class PreviewImageCreator {
	private final IMapData data;

	/**
	 * Points to use for height compution.
	 */
	private final static RelativePoint[] HEIGHTPOINTS = new RelativePoint[] {
	        new RelativePoint(0, 1),
	        new RelativePoint(1, 1),
	        new RelativePoint(0, 2),
	        new RelativePoint(1, 2),
	        new RelativePoint(2, 2),
	};

	public PreviewImageCreator(IMapData data) {
		this.data = data;
	}

	public short[] getPreviewImage() {
		int size = MapFileHeader.PREVIEW_IMAGE_SIZE;
		short[] image = new short[size * size];

		for (int x = 0; x < data.getWidth(); x++) {
			for (int y = 0; y < data.getHeight(); y++) {
				int imageSpace = toImageSpace(x, y);
				if (image[imageSpace] == 0) {
					image[imageSpace] = getColor(x, y);
				}
			}
		}

		boolean usey = false;
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				if (usey && y > 0 && image[x + y * size] == 0) {
					image[x + y * size] = image[x + (y - 1) * size];
					usey = false;
				} else if (x > 0 && image[x + y * size] == 0) {
					image[x + y * size] = image[x - 1 + y * size];
					usey = true;
				} else if (y > 0 && image[x + y * size] == 0) {
					image[x + y * size] = image[x + (y - 1) * size];
				}
			}
		}
		return image;
	}

	private int toImageSpace(int x, int y) {
		int inImageSpace =
		        scale(x, data.getWidth()) + scale(y, data.getHeight())
		                * MapFileHeader.PREVIEW_IMAGE_SIZE;
		return inImageSpace;
	}

	/**
	 * Scale a coordinate to image space.
	 * 
	 * @param x
	 * @param width
	 * @return
	 */
	private static int scale(int x, int width) {
		int size = MapFileHeader.PREVIEW_IMAGE_SIZE;
		int px = (int) ((double) x / width * size);
		return px < 0 ? 0 : px >= size ? size : px;
	}

	private short getColor(int x, int y) {
		ShortPoint2D current = new ShortPoint2D(x, y);

		int dheight =
		        getLandscapeHeightAround(current, false)
		                - getLandscapeHeightAround(current, true);
		float basecolor = .8f + .15f * dheight;

		return data.getLandscape(x, y).getColor().toShortColor(basecolor);
	}

	private int getLandscapeHeightAround(ShortPoint2D current, boolean upwards) {
		int count = 0;
		int height = 0;
		for (RelativePoint p : HEIGHTPOINTS) {
			ShortPoint2D toTest;
			if (upwards) {
				toTest = p.calculatePoint(current);
			} else {
				toTest = p.invert().calculatePoint(current);
			}

			short x = toTest.getX();
			short y = toTest.getY();
			if (x >= 0 && x < data.getWidth() && y >= 0 && y < data.getHeight()) {
				height += data.getLandscapeHeight(x, y);
				count += 1;
			}
		}

		if (count > 0) {
			return height / count;
		} else {
			return data.getLandscapeHeight(current.getX(), current.getY());
		}
	}
}
