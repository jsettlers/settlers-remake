package jsettlers.logic.map.newGrid.landscape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;

/**
 * This grid stores the height and the {@link ELandscapeType} of every position.
 * 
 * @author Andreas Eberle
 */
public final class LandscapeGrid implements Serializable, IWalkableGround, IFlattenedResettable {
	private static final long serialVersionUID = -751261669662036483L;

	private final byte[] heightGrid;
	private final byte[] landscapeGrid;
	private final byte[] resourceAmount;
	private final byte[] temporaryFlatened;
	private final byte[] resourceType;

	private final short width;
	private final short height;

	private final FlattenedResetter flattenedResetter;

	private transient final int[] debugColor;
	private transient IGraphicsBackgroundListener backgroundListener;

	public LandscapeGrid(short width, short height) {
		this.width = width;
		this.height = height;
		this.heightGrid = new byte[width * height];
		this.landscapeGrid = new byte[width * height];
		this.resourceAmount = new byte[width * height];
		this.resourceType = new byte[width * height];
		this.temporaryFlatened = new byte[width * height];

		this.debugColor = new int[width * height];
		resetDebugColors();

		this.flattenedResetter = new FlattenedResetter(this);
		setBackgroundListener(null);
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		setBackgroundListener(null);
		resetDebugColors();
	}

	public final byte getHeightAt(short x, short y) {
		return heightGrid[getIdx(x, y)];
	}

	public final void setHeightAt(short x, short y, byte height) {
		this.heightGrid[getIdx(x, y)] = height;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final ELandscapeType getLandscapeTypeAt(short x, short y) {
		return ELandscapeType.values[landscapeGrid[getIdx(x, y)]];
	}

	public final void setDebugColor(short x, short y, int rgba) {
		debugColor[getIdx(x, y)] = rgba;
	}

	public final int getDebugColor(int x, int y) {
		return debugColor[getIdx(x, y)];
	}

	public final void resetDebugColors() {
		for (int i = 0; i < debugColor.length; i++) {
			debugColor[i] = -1;
		}
	}

	private final int getIdx(int x, int y) {
		return y * width + x;
	}

	public final void setLandscapeTypeAt(short x, short y, ELandscapeType landscapeType) {
		if (landscapeType == ELandscapeType.FLATTENED && this.landscapeGrid[getIdx(x, y)] != ELandscapeType.FLATTENED.ordinal) {
			flattenedResetter.addPosition(x, y);
		}

		this.landscapeGrid[getIdx(x, y)] = landscapeType.ordinal;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public final void changeHeightAt(short x, short y, byte delta) {
		this.heightGrid[getIdx(x, y)] += delta;
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
		this.resourceType[getIdx(x, y)] = resourceType.ordinal;
		this.resourceAmount[getIdx(x, y)] = amount;
	}

	/**
	 * gets the resource amount at the given position
	 * 
	 * @param x
	 * @param y
	 * @return The amount of resources, where 0 is no resources and {@link Byte.MAX_VALUE} means full resources.
	 */
	public final byte getResourceAmountAt(short x, short y) {
		return resourceAmount[getIdx(x, y)];
	}

	public final EResourceType getResourceTypeAt(short x, short y) {
		return EResourceType.values[resourceType[getIdx(x, y)]];
	}

	public final boolean hasResourceAt(short x, short y, EResourceType resourceType) {
		return getResourceTypeAt(x, y) == resourceType && resourceAmount[getIdx(x, y)] > 0;
	}

	public final void pickResourceAt(short x, short y) {
		resourceAmount[getIdx(x, y)]--;
	}

	/**
	 * This class is used as null object to get rid of a lot of null checks
	 * 
	 * @author Andreas Eberle
	 */
	private static final class NullBackgroundListener implements IGraphicsBackgroundListener, Serializable {
		private static final long serialVersionUID = -332117701485179252L;

		@Override
		public final void backgroundChangedAt(short x, short y) {
		}
	}

	@Override
	public final void walkOn(int x, int y) {
		int i = getIdx(x, y);
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
		if (getLandscapeTypeAt((short) x, (short) y).isGrass() && getLandscapeTypeAt((short) x, (short) (y - 1)).isGrass()
				&& getLandscapeTypeAt((short) (x - 1), (short) (y - 1)).isGrass() && getLandscapeTypeAt((short) (x - 1), (short) y).isGrass()
				&& getLandscapeTypeAt((short) x, (short) (y + 1)).isGrass() && getLandscapeTypeAt((short) (x + 1), (short) (y + 1)).isGrass()
				&& getLandscapeTypeAt((short) (x + 1), (short) y).isGrass()) {
			setLandscapeTypeAt((short) x, (short) y, ELandscapeType.FLATTENED);
		}
	}

	public float getResourceAmountAround(short x, short y, EResourceType type) {
		int minx = Math.max(x - 1, 0);
		int maxx = Math.max(x + 1, width - 1);
		int miny = Math.max(y - 1, 0);
		int maxy = Math.max(y + 1, height - 1);
		int found = 0;
		for (int currentx = minx; currentx <= maxx; currentx++) {
			for (int currenty = miny; currenty <= maxy; currenty++) {
				if (resourceType[getIdx(currentx, currenty)] == type.ordinal) {
					found += resourceAmount[getIdx(x, y)];
				}
			}
		}
		return (float) found / Byte.MAX_VALUE / 9;
	}

	@Override
	public boolean countFlattenedDown(short x, short y) {
		int i = getIdx(x, y);

		temporaryFlatened[i]--;
		if (temporaryFlatened[i] <= -30) {
			temporaryFlatened[i] = 0;
			setLandscapeTypeAt(x, y, ELandscapeType.GRASS);
			return true;
		} else {
			return false;
		}
	}

}
