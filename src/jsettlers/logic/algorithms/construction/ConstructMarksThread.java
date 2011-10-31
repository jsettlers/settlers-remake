package jsettlers.logic.algorithms.construction;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
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
public class ConstructMarksThread extends Thread {
	/**
	 * area of tiles to be checked.
	 */
	private IMapArea mapArea = null;
	private EBuildingType buildingType = null;
	private final IConstructionMarkableMap map;
	private final byte player;
	
	private IMapArea lastArea = null; 

	public ConstructMarksThread(IConstructionMarkableMap map, byte player) {
		super("constrMarks");
		this.map = map;
		this.player = player;

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
				removeConstructionMarks(mapArea);
				lastArea = null;

				Thread.sleep(30);
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

		if (lastArea != null) {
			removeConstructionMarks(lastArea, mapArea);
		}
		for (ISPosition2D pos : mapArea) {
			map.setConstructMarking(pos, calculateConstrMarkVal(usedPositions, pos));
		}
		lastArea = mapArea;
	}

	/**
	 * Removes all construction marks in the given area.
	 * @param area The area to remove the marks
	 */
	private void removeConstructionMarks(IMapArea area) {
	    for (ISPosition2D pos : area) {
	    	map.setConstructMarking(pos, (byte) -1);
	    }
    }

	/**
	 * Removes all construction marks in the given area.
	 * @param area The area to remove the marks
	 * @param notIn The area of marks that should be skipped.
	 */
	private void removeConstructionMarks(IMapArea area, IMapArea notIn) {
	    for (ISPosition2D pos : area) {
	    	if (!notIn.contains(pos)) {
	    		map.setConstructMarking(pos, (byte) -1);
	    	}
	    }
    }

	private byte calculateConstrMarkVal(RelativePoint[] usedPositions, ISPosition2D position) {
		int sum = 0;

		for (RelativePoint curr : usedPositions) {
			ISPosition2D currPos = curr.calculatePoint(position);

			if (!map.isBuildingPlaceable(currPos, player) || !isAllowedLandscape(map.getLandscapeTypeAt(currPos))) {
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

	private boolean isAllowedLandscape(ELandscapeType landscapeType) {
	    ELandscapeType[] groundtypes = buildingType.getGroundtypes();
		for (int i = 0; i < groundtypes.length; i++) {
	    	if (groundtypes[i] == landscapeType) {
	    		return true;
	    	}
	    }
		return false;
    }

	public void setScreen(IMapArea mapArea) {
		this.mapArea = new MapShapeFilter(mapArea, map.getWidth(), map.getHeight());
		refreshMarkings();
	}

	public void setBuildingType(EBuildingType type) {
		buildingType = type;
		refreshMarkings();
	}

	public synchronized void refreshMarkings() {
		this.notifyAll();
	}
}
