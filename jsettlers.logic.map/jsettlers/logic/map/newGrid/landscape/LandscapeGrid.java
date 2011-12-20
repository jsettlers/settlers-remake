package jsettlers.logic.map.newGrid.landscape;

import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;

/**
 * This grid stores the height and the {@link ELandscapeType} of every position.
 * 
 * @author Andreas Eberle
 * 
 */
public class LandscapeGrid implements Serializable {
	private static final long serialVersionUID = -751261669662036483L;

	private final byte[][] heightGrid;
	private final ELandscapeType[][] landscapeGrid;
	private final byte[][] resourceAmount;
	private final EResourceType[][] resourceType;

	private transient IGraphicsBackgroundListener backgroundListener;

	public LandscapeGrid(short width, short height) {
		this.heightGrid = new byte[width][height];
		this.landscapeGrid = new ELandscapeType[width][height];
		this.resourceAmount = new byte[width][height];
		this.resourceType = new EResourceType[width][height];

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

	public final void setLandscapeTypeAt(short x, short y, ELandscapeType landscapeType) {
		this.landscapeGrid[x][y] = landscapeType;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final void changeHeightAt(short x, short y, byte delta) {
		this.heightGrid[x][y] += delta;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		if (backgroundListener != null) {
			this.backgroundListener = backgroundListener;
		} else {
			this.backgroundListener = new NullBackgroundListener();
		}
	}

	public final void setResourceAt(short x, short y, EResourceType resourceType, byte amount) {
		this.resourceType[x][y] = resourceType;
		this.resourceAmount[x][y] = amount;
	}

	public final byte getResourceAmountAt(short x, short y) {
		return resourceAmount[x][y];
	}

	public final EResourceType getResourceTypeAt(short x, short y) {
		return resourceType[x][y];
	}

	public final boolean hasResourceAt(short x, short y, EResourceType resourceType) {
		return getResourceTypeAt(x, y) == resourceType && resourceAmount[x][y] > 0;
	}

	public final void pickResourceAt(short x, short y) {
		resourceAmount[x][y]--;
	}

	/**
	 * This class is used as null object to get rid of a lot of null checks
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private static class NullBackgroundListener implements IGraphicsBackgroundListener, Serializable {
		private static final long serialVersionUID = -332117701485179252L;

		@Override
		public final void backgroundChangedAt(short x, short y) {
		}
	}
}
