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
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.AlgorithmConstants;
import synchronic.timer.NetworkTimer;

/**
 * Thread to calculate the markings for the user if he want's to construct a new building.<br>
 * This is a singleton class.
 * 
 * @author Andreas Eberle
 * 
 */
public class ConstructMarksThread implements Runnable {
	private final IConstructionMarkableMap map;
	private final byte player;
	private final Thread thread;

	/**
	 * area of tiles to be checked.
	 */
	private MapRectangle mapArea = null;
	private EBuildingType buildingType = null;

	private IMapArea lastArea = null;
	private boolean canceled;

	public ConstructMarksThread(IConstructionMarkableMap map, byte player) {
		this.map = map;
		this.player = player;

		thread = new Thread(this, "constrMarksThread");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		while (!canceled) {
			try {
				synchronized (this) {
					while (buildingType == null) {
						this.wait();
					}
				}

				while (buildingType != null && !canceled) {
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

			} catch (InterruptedException e) {
				// do nothing
			} catch (Throwable e) { // this thread must never be destroyed due to errors
				e.printStackTrace();
			}
		}
	}

	private void calculateConstructMarks() {
		MapRectangle mapArea = this.mapArea; // local variables needed to prevent errors caused by synchronization
		EBuildingType buildingType = this.buildingType;

		if (buildingType == null || mapArea == null) {
			return;
		}
		if (lastArea != null) {
			removeConstructionMarks(lastArea, mapArea);
		}

		final BuildingAreaBitSet buildingSet = buildingType.getBuildingAreaBitSet();

		final short minX = (short) (mapArea.getLineStartX(0));
		final short maxX = (short) (mapArea.getLineEndX(mapArea.getLines() - 1));
		final short minY = (mapArea.getMinY());

		final short width = (short) (maxX - minX + 1);
		final short height = mapArea.getHeight();
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

		for (short line = 0; line < mapArea.getLines(); line++) {
			final short mapY = (short) mapArea.getLineY(line);
			final int endX = mapArea.getLineEndX(line);
			for (short mapX = (short) mapArea.getLineStartX(line); mapX < endX; mapX++) {
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
		lastArea = mapArea;
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
		for (ShortPoint2D pos : new MapShapeFilter(area, map.getWidth(), map.getHeight())) {
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
		for (ShortPoint2D pos : new MapShapeFilter(area, map.getWidth(), map.getHeight())) {
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

	public void cancel() {
		canceled = true;
		thread.interrupt();
	}

}
