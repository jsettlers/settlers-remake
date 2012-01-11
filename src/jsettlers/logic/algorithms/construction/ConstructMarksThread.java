package jsettlers.logic.algorithms.construction;

import java.util.BitSet;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.algorithms.AlgorithmConstants;
import synchronic.timer.NetworkTimer;

/**
 * Thread to calculate the markings for the user if he want's to construct a new building.<br>
 * This is a singleton class.
 * 
 * @author Andreas Eberle
 * 
 */
public class ConstructMarksThread extends Thread {
	private final IConstructionMarkableMap map;
	private final byte player;

	/**
	 * area of tiles to be checked.
	 */
	private MapRectangle mapArea = null;
	private EBuildingType buildingType = null;

	private IMapArea lastArea = null;
	private BuildingSet buildingSet;

	public ConstructMarksThread(IConstructionMarkableMap map, byte player) {
		super("constrMarksThread");
		this.map = map;
		this.player = player;

		this.setDaemon(true);
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				synchronized (this) {
					while (buildingType == null) {
						this.wait();
					}
				}

				while (buildingType != null) {
					if (!NetworkTimer.isPausing()) {
						StopWatch watch = new MilliStopWatch();
						watch.start();

						calculateConstructMarks();

						watch.stop("calculation of construction marks");
					}
					synchronized (this) {
						wait(AlgorithmConstants.CONSTRUCT_MARKS_MAX_REFRESH_TIME);
					}
				}
				removeConstructionMarks(lastArea);
				lastArea = null;

			} catch (Throwable e) { // this thread must never be destroyed due to errors
				e.printStackTrace();
			}
		}
	}

	private void calculateConstructMarks() {
		MapRectangle area = this.mapArea; // local variables needed to prevent errors caused by synchronization
		EBuildingType currBuildingType = this.buildingType;

		if (currBuildingType == null || area == null) {
			return;
		}
		if (lastArea != null) {
			removeConstructionMarks(lastArea, area);
		}

		final short minX = (short) (area.getLineStartX(0));
		final short maxX = (short) (area.getLineEndX(area.getLines() - 1));
		final short minY = (area.getMinY());

		final short width = (short) (maxX - minX + 1);
		final short height = area.getHeight();
		final short setWidth = (short) (width + buildingSet.width);
		final short setHeight = (short) (height + buildingSet.height);

		BitSet areaSet = new BitSet(setWidth * setHeight);

		final ELandscapeType[] landscapeTypes = buildingType.getGroundtypes();
		for (short y = 0; y < setHeight; y++) {
			for (short x = 0; x < setWidth; x++) {
				boolean canConstruct = map.canUsePositionForConstruction((short) (minX + x), (short) (y + minY), landscapeTypes, player);
				areaSet.set(x + y * setWidth, canConstruct);
			}
		}

		for (short y = (short) -buildingSet.minY; y < height; y++) {
			for (short x = (short) -buildingSet.minX; x < width; x++) {
				final short mapX = (short) (minX + x);
				final short mapY = (short) (minY + y);

				if (map.isInBounds(mapX, mapY)) { // needed because of map.setConstructMarking()
					byte value;
					if (checkPosition(x, y, areaSet, setWidth, buildingSet)) {
						value = (byte) 1;
					} else {
						value = -1;
					}
					map.setConstructMarking(mapX, mapY, value);
				}
			}
		}

		lastArea = area;
	}

	private static final boolean checkPosition(short offsetX, short offsetY, BitSet areaSet, short areaWidth, BuildingSet buildingSet) {
		for (short x = 0; x < buildingSet.width; x++) {
			for (short y = 0; y < buildingSet.height; y++) {
				if (buildingSet.getWithoutOffset(x, y)) { // is position needed?
					if (!areaSet.get((x + offsetX + buildingSet.minX) + (y + offsetY + buildingSet.minY) * areaWidth)) { // is position not ok?
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Removes all construction marks in the given area.
	 * 
	 * @param area
	 *            The area to remove the marks
	 */
	private void removeConstructionMarks(IMapArea area) {
		for (ISPosition2D pos : new MapShapeFilter(area, map.getWidth(), map.getHeight())) {
			map.setConstructMarking(pos.getX(), pos.getY(), (byte) -1);
		}
	}

	/**
	 * Removes all construction marks in the given area.
	 * 
	 * @param area
	 *            The area to remove the marks
	 * @param notIn
	 *            The area of marks that should be skipped.
	 */
	private void removeConstructionMarks(IMapArea area, IMapArea notIn) {
		for (ISPosition2D pos : new MapShapeFilter(area, map.getWidth(), map.getHeight())) {
			if (!notIn.contains(pos)) {
				map.setConstructMarking(pos.getX(), pos.getY(), (byte) -1);
			}
		}
	}

	private byte calculateConstrMarkVal(short x, short y, RelativePoint[] usedPositions) {
		int sum = 0;

		for (RelativePoint curr : usedPositions) {
			sum += map.getHeightAt(curr.calculateX(x), curr.calculateY(y));
		}

		int avg = sum / usedPositions.length;
		int diff = 0;
		for (RelativePoint curr : usedPositions) {
			diff += Math.abs(map.getHeightAt(curr.calculateX(x), curr.calculateY(y)) - avg);
		}

		return (byte) (diff / usedPositions.length);
	}

	public void setScreen(MapRectangle mapArea) {
		this.mapArea = mapArea;
		refreshMarkings();
	}

	public void setBuildingType(EBuildingType type) {
		this.buildingType = type;
		if (type != null) {
			this.buildingSet = new BuildingSet(type);
		} else {
			this.buildingSet = null;
		}
		refreshMarkings();
	}

	public synchronized void refreshMarkings() {
		this.notifyAll();
	}

	final String printBitSet(BitSet set, final short width, final short height) {
		StringBuffer buffer = new StringBuffer();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (set.get(x + y * width)) {
					buffer.append('x');
				} else {
					buffer.append(' ');
				}
			}
			buffer.append('\n');
		}
		return buffer.toString();
	}

	class BuildingSet {
		final BitSet bitSet;
		final short width;
		final short height;
		final short minX;
		final short minY;
		final short maxX;
		final short maxY;

		public BuildingSet(EBuildingType type) {
			RelativePoint[] protectedTiles = type.getProtectedTiles();
			short minX = protectedTiles[0].getDx();
			short maxX = protectedTiles[0].getDx();
			short minY = protectedTiles[0].getDy();
			short maxY = protectedTiles[0].getDy();
			for (int i = 0; i < protectedTiles.length; i++) {
				minX = min(minX, protectedTiles[i].getDx());
				maxX = max(maxX, protectedTiles[i].getDx());
				minY = min(minY, protectedTiles[i].getDy());
				maxY = max(maxY, protectedTiles[i].getDy());
			}

			this.minX = minX;
			this.minY = minY;
			this.maxX = maxX;
			this.maxY = maxY;

			this.width = (short) (maxX - minX + 1);
			this.height = (short) (maxY - minY + 1);

			this.bitSet = new BitSet(width * height);

			for (int i = 0; i < protectedTiles.length; i++) {
				set(protectedTiles[i].getDx(), protectedTiles[i].getDy());
			}
		}

		public boolean getWithoutOffset(short x, short y) {
			return this.bitSet.get((x) + width * (y));
		}

		private final void set(short x, short y) {
			this.bitSet.set((x - minX) + width * (y - minY));
		}

		private final short max(short first, short second) {
			if (first > second) {
				return first;
			} else {
				return second;
			}
		}

		private final short min(short first, short second) {
			if (first < second) {
				return first;
			} else {
				return second;
			}
		}

		@Override
		public String toString() {
			return printBitSet(bitSet, width, height);
		}
	}
}
