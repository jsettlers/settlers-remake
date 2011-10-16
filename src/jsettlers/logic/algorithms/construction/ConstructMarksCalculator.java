package jsettlers.logic.algorithms.construction;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.IMapArea;
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
public class ConstructMarksCalculator extends Thread {
	/**
	 * area of tiles to be checked.
	 */
	private static IMapArea mapArea = null;
	private static boolean refreshNeeded = false;
	private static EBuildingType buildingType = null;
	private final IConstructionMarkableMap map;
	private final byte player;

	public ConstructMarksCalculator(IConstructionMarkableMap map, byte player) {
		super("constrMarks");
		this.map = map;
		this.player = player;
	}

	@Override
	public void run() {
		int timeSinceLastRefresh = 0;
		while (true) {
			try {
				if (buildingType != null) {
					if (refreshNeeded || timeSinceLastRefresh >= AlgorithmConstants.CONSTRUCT_MARKS_MAX_REFRESH_TIME) {
						if (!NetworkTimer.isPausing()) {
							StopWatch watch = new MilliStopWatch();
							watch.start();

							calculateConstructMarks();

							watch.stop("calculation of construction marks");
							refreshNeeded = false;
						}
						timeSinceLastRefresh = 0;
					}
				} else {
					refreshNeeded = true;
				}

				Thread.sleep(30);
				timeSinceLastRefresh += 30;
			} catch (Throwable e) { // this thread must never be destroyed due to errors
				e.printStackTrace();
			}
		}
	}

	private void calculateConstructMarks() {
		if (buildingType == null || mapArea == null) {
			return;
		}

		RelativePoint[] usedPositions = buildingType.getProtectedTiles();

		for (ISPosition2D pos : mapArea) {
			map.setConstructMarking(pos, calculateConstrMarkVal(usedPositions, pos));
		}
	}

	private byte calculateConstrMarkVal(RelativePoint[] usedPositions, ISPosition2D position) {
		int sum = 0;

		for (RelativePoint curr : usedPositions) {
			ISPosition2D currPos = curr.calculatePoint(position);

			if (!map.isBuildingPlaceable(currPos, player)) {
				return -1;
			}
			sum += map.getHeightAt(currPos);
		}

		int avg = sum / usedPositions.length;
		int diff = 0;
		for (RelativePoint curr : usedPositions) {
			diff += Math.abs(map.getHeightAt(curr.calculatePoint(position)) - avg);
		}

		return (byte) (diff / usedPositions.length);
	}

	public void setScreen(IMapArea mapArea) {
		ConstructMarksCalculator.mapArea = new MapShapeFilter(mapArea, map.getWidth(), map.getHeight());
		refreshMarkings();
	}

	public void setBuildingType(EBuildingType type) {
		ConstructMarksCalculator.buildingType = type;
		refreshMarkings();
	}

	public void refreshMarkings() {
		refreshNeeded = true;
	}
}
