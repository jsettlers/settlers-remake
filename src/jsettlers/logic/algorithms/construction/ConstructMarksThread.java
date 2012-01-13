package jsettlers.logic.algorithms.construction;

import java.util.BitSet;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.EBuildingType.BuildingAreaBitSet;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.ISPosition2D;
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

		final BuildingAreaBitSet buildingSet = buildingType.getBuildingAreaBitSet();

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
				boolean canConstruct = map.canUsePositionForConstruction((short) (minX + buildingSet.minX + x),
						(short) (y + buildingSet.minY + minY), landscapeTypes, player);
				areaSet.set(x + y * setWidth, canConstruct);
			}
		}

		for (short line = 0; line < area.getLines(); line++) {
			final short mapY = (short) area.getLineY(line);
			final int endX = area.getLineEndX(line);
			for (short mapX = (short) area.getLineStartX(line); mapX < endX; mapX++) {
				if (map.isInBounds(mapX, mapY)) { // needed because of map.setConstructMarking()
					byte value;
					if (checkPosition(mapX - minX, mapY - minY, areaSet, setWidth, buildingSet)) {
						value = map.getConstructionMarkValue(mapX, mapY, buildingType);
						if (value > Byte.MAX_VALUE) {
							value = -1;
						}
					} else {
						value = -1;
					}
					map.setConstructMarking(mapX, mapY, value);
				}
			}
		}
		lastArea = area;
	}

	private static final boolean checkPosition(int offsetX, int offsetY, BitSet areaSet, short areaWidth, BuildingAreaBitSet buildingSet) {
		for (short x = 0; x < buildingSet.width; x++) {
			for (short y = 0; y < buildingSet.height; y++) {
				if (buildingSet.getWithoutOffset(x, y)) { // is position needed?
					if (!areaSet.get((x + offsetX) + (y + offsetY) * areaWidth)) { // is position not ok?
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

	public void setScreen(MapRectangle mapArea) {
		this.mapArea = mapArea;
		refreshMarkings();
	}

	public void setBuildingType(EBuildingType type) {
		this.buildingType = type;
		refreshMarkings();
	}

	public synchronized void refreshMarkings() {
		this.notifyAll();
	}

}
