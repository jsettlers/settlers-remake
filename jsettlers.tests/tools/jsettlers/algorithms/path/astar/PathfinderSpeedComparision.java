package jsettlers.algorithms.path.astar;

import java.io.File;
import java.util.Random;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculatable;
import jsettlers.logic.algorithms.path.astar.AbstractAStar;
import jsettlers.logic.algorithms.path.astar.BucketQueueAStar;
import jsettlers.logic.algorithms.path.astar.normal.AStarJPS;
import jsettlers.logic.algorithms.path.astar.normal.HexAStar;
import jsettlers.logic.algorithms.path.astar.normal.IAStarPathMap;
import jsettlers.logic.map.newGrid.MainGrid;
import jsettlers.logic.map.save.DirectoryMapLister;
import jsettlers.logic.map.save.loader.MapLoader;
import networklib.synchronic.random.RandomSingleton;

public class PathfinderSpeedComparision {
	private static final int NUMBER_OF_PATHS = 200;
	private static final int MIN_START_TARGET_DISTANCE = 800;

	private static final int PATH_RANDOM_SEED = 1234;

	public static void main(String args[]) throws MapLoadException, InterruptedException {
		MainGrid mainGrid = getMap();
		short width = mainGrid.getGraphicsGrid().getWidth();
		short height = mainGrid.getGraphicsGrid().getHeight();
		IAStarPathMap map = mainGrid.getPathfinderGrid();

		AbstractAStar astars[] = { new BucketQueueAStar(map, width, height), new HexAStar(map, width, height), new AStarJPS(map, width, height) };

		for (int i = 0; i < 30; i++) {
			System.out.println("\n");
		}

		long aStarTimes[] = new long[astars.length];
		for (int i = 0; i < astars.length; i++) {
			testAStar(PATH_RANDOM_SEED, astars[i], map, 10, width, height); // just to get the arrays in the cache
			aStarTimes[i] = testAStar(PATH_RANDOM_SEED, astars[i], map, NUMBER_OF_PATHS, width, height);
		}

		for (int i = 0; i < astars.length; i++) {
			System.out.println("aStar" + i + " (" + astars[i].getClass() + ") avg: " + ((float) aStarTimes[i]) / NUMBER_OF_PATHS + " ms");
		}

		System.exit(0);
	}

	private static MainGrid getMap() throws MapLoadException {
		RandomSingleton.load(123456L);
		MapLoader loader = MapLoader.getLoaderForFile(new DirectoryMapLister.ListedMapFile(new File("../jsettlers.common/resources/maps/bigmap.map"),
				false));
		return loader.loadMainGrid(null).getMainGrid();
	}

	private static long testAStar(int randomSeed, AbstractAStar astar, IAStarPathMap map, int numberOfPaths, short width, short height)
			throws InterruptedException {

		Random random = new Random(randomSeed);
		IPathCalculatable requester = new TestPathRequester();
		System.out.println("found path: ");

		Thread.sleep(300);

		MilliStopWatch watch = new MilliStopWatch();

		for (int i = 0; i < numberOfPaths; i++) {
			ShortPoint2D start = getUnblocktRandomPosition(requester, random, map, width, height);
			ShortPoint2D target = getUnblocktRandomPosition(requester, random, map, width, height);

			if (ShortPoint2D.getOnGridDist(target.x - start.x, target.y - start.y) < MIN_START_TARGET_DISTANCE) { // only tests paths with at least
																													// 200 length
				i--;
				continue;
			}

			astar.findPath(requester, start.x, start.y, target.x, target.y);
			System.out.print(i + ", ");
		}

		System.out.println();
		return watch.getDiff();
	}

	public static ShortPoint2D getUnblocktRandomPosition(IPathCalculatable requester, Random random, IAStarPathMap map, int width, int height) {
		short x, y;
		do {
			x = (short) random.nextInt(width);
			y = (short) random.nextInt(height);
		} while (map.isBlocked(requester, x, y));

		return new ShortPoint2D(x, y);
	}
}
