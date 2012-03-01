package jsettlers.algorithms.path.astar;

import java.io.File;
import java.util.Random;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.AStarJPS;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStar;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.MapLoader;
import random.RandomSingleton;

public class PathfinderSpeedComparision {
	private static final int NUMBER_OF_PATHS = 200;
	private static final int PATH_RANDOM_SEED = 1234;

	public static void main(String args[]) throws MapLoadException, InterruptedException {
		MainGrid mainGrid = getMap();
		short width = mainGrid.getGraphicsGrid().getWidth();
		short height = mainGrid.getGraphicsGrid().getHeight();
		IAStarPathMap map = mainGrid.getPathfinderGrid();

		IAStar astar1 = new HexAStar(map, width, height);
		IAStar astar2 = new AStarJPS(map, width, height);

		for (int i = 0; i < 30; i++) {
			System.out.println("\n");
		}

		testAStar(PATH_RANDOM_SEED, astar1, map, 10, width, height); // just to get the arrays in the cache
		long aStar1Time = testAStar(PATH_RANDOM_SEED, astar1, map, NUMBER_OF_PATHS, width, height);

		testAStar(PATH_RANDOM_SEED, astar2, map, 10, width, height); // just to get the arrays in the cache
		long aStar2Time = testAStar(PATH_RANDOM_SEED, astar2, map, NUMBER_OF_PATHS, width, height);

		System.out.println("aStar1 avg: " + ((float) aStar1Time) / NUMBER_OF_PATHS + " ms");
		System.out.println("aStar2 avg: " + ((float) aStar2Time) / NUMBER_OF_PATHS + " ms");
	}

	private static MainGrid getMap() throws MapLoadException {
		RandomSingleton.load(123456L);
		MapLoader loader = new MapLoader(new File("../jsettlers.common/resources/maps/bigmap.map"));

		return loader.getMainGrid();
	}

	private static long testAStar(int randomSeed, IAStar astar, IAStarPathMap map, int numberOfPaths, short width, short height)
			throws InterruptedException {

		Random random = new Random(randomSeed);
		IPathCalculateable requester = new TestPathRequester();
		System.out.println("found path: ");

		Thread.sleep(300);

		MilliStopWatch watch = new MilliStopWatch();
		watch.start();

		for (int i = 0; i < numberOfPaths; i++) {
			ISPosition2D start = getUnblocktPosition(requester, random, map, width, height);
			ISPosition2D target = getUnblocktPosition(requester, random, map, width, height);

			astar.findPath(requester, start.getX(), start.getY(), target.getX(), target.getY());
			System.out.print(i + ", ");
		}

		watch.stop();

		System.out.println();
		return watch.getDiff();
	}

	private static ISPosition2D getUnblocktPosition(IPathCalculateable requester, Random random, IAStarPathMap map, short width, short height) {
		short x, y;
		do {
			x = (short) random.nextInt(width);
			y = (short) random.nextInt(height);
		} while (map.isBlocked(requester, x, y));

		return new ShortPoint2D(x, y);
	}

	private static final class TestPathRequester implements IPathCalculateable {
		@Override
		public byte getPlayer() {
			return 0;
		}

		@Override
		public ISPosition2D getPos() {
			return null;
		}

		@Override
		public boolean needsPlayersGround() {
			return false;
		}
	}
}
