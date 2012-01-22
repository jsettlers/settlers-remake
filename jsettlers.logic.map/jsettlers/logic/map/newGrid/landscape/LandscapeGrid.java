package jsettlers.logic.map.newGrid.landscape;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;

/**
 * This grid stores the height and the {@link ELandscapeType} of every position.
 * 
 * @author Andreas Eberle
 */
public class LandscapeGrid implements Serializable, IWalkableGround {
	private static final long serialVersionUID = -751261669662036483L;

	private final byte[][] heightGrid;
	private final ELandscapeType[][] landscapeGrid;
	private final byte[][] resourceAmount;
	private final byte[] temporaryFlatened;
	private final EResourceType[][] resourceType;

	private transient IGraphicsBackgroundListener backgroundListener;

	private final short width;

	public LandscapeGrid(short width, short height) {
		this.width = width;
		this.heightGrid = new byte[width][height];
		this.landscapeGrid = new ELandscapeType[width][height];
		this.resourceAmount = new byte[width][height];
		this.resourceType = new EResourceType[width][height];
		this.temporaryFlatened = new byte[width * height];

		setBackgroundListener(null);
	}

	public final byte getHeightAt(short x, short y) {
		return heightGrid[x][y];
	}

	public final void setHeightAt(short x, short y, byte height) {
		this.heightGrid[x][y] = height;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final ELandscapeType getLandscapeTypeAt(short x, short y) {
		return landscapeGrid[x][y];
	}

	public final void setLandscapeTypeAt(short x, short y,
	        ELandscapeType landscapeType) {
		this.landscapeGrid[x][y] = landscapeType;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final void changeHeightAt(short x, short y, byte delta) {
		this.heightGrid[x][y] += delta;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final void setBackgroundListener(
	        IGraphicsBackgroundListener backgroundListener) {
		if (backgroundListener != null) {
			this.backgroundListener = backgroundListener;
		} else {
			this.backgroundListener = new NullBackgroundListener();
		}
	}

	public final void setResourceAt(short x, short y,
	        EResourceType resourceType, byte amount) {
		this.resourceType[x][y] = resourceType;
		this.resourceAmount[x][y] = amount;
	}

	/**
	 * gets the resource amount at the given position
	 * 
	 * @param x
	 * @param y
	 * @return The amount of resources, where 0 is no resources and
	 *         {@link Byte.MAX_VALUE} means full resources.
	 */
	public final byte getResourceAmountAt(short x, short y) {
		return resourceAmount[x][y];
	}

	public final EResourceType getResourceTypeAt(short x, short y) {
		return resourceType[x][y];
	}

	public final boolean hasResourceAt(short x, short y,
	        EResourceType resourceType) {
		return getResourceTypeAt(x, y) == resourceType
		        && resourceAmount[x][y] > 0;
	}

	public final void pickResourceAt(short x, short y) {
		resourceAmount[x][y]--;
	}

	/**
	 * This class is used as null object to get rid of a lot of null checks
	 * 
	 * @author Andreas Eberle
	 */
	private static class NullBackgroundListener implements
	        IGraphicsBackgroundListener, Serializable {
		private static final long serialVersionUID = -332117701485179252L;

		@Override
		public final void backgroundChangedAt(short x, short y) {
		}
	}

	@Override
	public final void walkOn(int x, int y) {
		int i = width * y + x;
		if (temporaryFlatened[i] < 100) {
			temporaryFlatened[i] += 3;
			if (temporaryFlatened[i] > 20) {
				flaten(x, y);
			}
		}
	}

	/**
	 * Sets the landscape to flattened after a settler walked on it.
	 * 
	 * @param x
	 * @param y
	 */
	private void flaten(int x, int y) {
		if (getLandscapeTypeAt((short) x, (short) y).isGrass()
		        && getLandscapeTypeAt((short) x, (short) (y - 1)).isGrass()
		        && getLandscapeTypeAt((short) (x - 1), (short) (y - 1))
		                .isGrass()
		        && getLandscapeTypeAt((short) (x - 1), (short) y).isGrass()
		        && getLandscapeTypeAt((short) x, (short) (y + 1)).isGrass()
		        && getLandscapeTypeAt((short) (x + 1), (short) (y + 1))
		                .isGrass()
		        && getLandscapeTypeAt((short) (x + 1), (short) y).isGrass()) {
			setLandscapeTypeAt((short) x, (short) y, ELandscapeType.FLATTENED);
		}
	}

	public float getResourceAmountAround(short x, short y, EResourceType type) {
		int minx = Math.max(x - 1, 0);
		int maxx = Math.max(x + 1, width - 1);
		int miny = Math.max(y - 1, 0);
		int maxy = Math.max(y + 1, resourceAmount[0].length - 1);
		int found = 0;
		for (int currentx = minx; currentx <= maxx; currentx++) {
			for (int currenty = miny; currenty <= maxy; currenty++) {
				if (resourceType[currentx][currenty] == type) {
					found += resourceAmount[currentx][currenty];
				}
			}
		}
		return (float) found / Byte.MAX_VALUE / 9;
	}
}
