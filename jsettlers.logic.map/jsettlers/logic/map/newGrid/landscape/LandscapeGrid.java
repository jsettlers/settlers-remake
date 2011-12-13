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

	private IGraphicsBackgroundListener backgroundListener;

	public LandscapeGrid(short width, short height) {
		this.heightGrid = new byte[width][height];
		this.landscapeGrid = new ELandscapeType[width][height];

		setBackgroundListener(null);
	}

	public byte getHeightAt(short x, short y) {
		return heightGrid[x][y];
	}

	public void setHeightAt(short x, short y, byte height) {
		this.heightGrid[x][y] = height;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public ELandscapeType getLandscapeTypeAt(short x, short y) {
		return landscapeGrid[x][y];
	}

	public void setLandscapeTypeAt(short x, short y, ELandscapeType landscapeType) {
		this.landscapeGrid[x][y] = landscapeType;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public void changeHeightAt(short x, short y, byte delta) {
		this.heightGrid[x][y] += delta;
		backgroundListener.backgroundChangedAt(x, y);
	}

	public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
		if (backgroundListener != null) {
			this.backgroundListener = backgroundListener;
		} else {
			this.backgroundListener = new NullBackgroundListener();
		}
	}

	/**
	 * This class is used as null object to get rid of a lot of null checks
	 * 
	 * @author Andreas Eberle
	 * 
	 */
	private class NullBackgroundListener implements IGraphicsBackgroundListener {
		@Override
		public final void backgroundChangedAt(short x, short y) {
		}
	}
}
