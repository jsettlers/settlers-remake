package jsettlers.algorithms.supergrid.speed;

import java.util.ArrayList;
import java.util.Random;

import jsettlers.TestUtils;
import jsettlers.algorithms.path.astar.PathfinderSpeedComparision;
import jsettlers.algorithms.path.astar.TestPathRequester;
import jsettlers.common.Color;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.MapLoadException;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.algorithms.path.astar.normal.HexAStar;
import jsettlers.logic.algorithms.path.astar.normal.IAStar;
import jsettlers.logic.algorithms.path.astar.supergrid.ISuperGridAStarGrid;
import jsettlers.logic.algorithms.path.astar.supergrid.SuperGridAStar;
import jsettlers.logic.map.newGrid.MainGrid;

public class SuperGridAStarComparision {

	private static final int RANDOM_SEED = 124423;
	private static final int RUNS = 200;

	public static void main(String args[]) throws MapLoadException {
		MainGrid grid = TestUtils.getMap();
		grid.stopThreads();
		short width = grid.getGraphicsGrid().getWidth();
		short height = grid.getGraphicsGrid().getHeight();

		IAStarPathMap pathfinderGrid = grid.getPathfinderGrid();

		HexAStar aStar = new HexAStar(pathfinderGrid, width, height);
		SuperGridAStar superAStar = new SuperGridAStar(getSuperPathfinderGrid(grid), width, height);

		long aStarTime = testAStar(RANDOM_SEED, aStar, pathfinderGrid, RUNS, width, height);
		long superAStarTime = testSuperAStar(RANDOM_SEED, superAStar, aStar, pathfinderGrid, RUNS * 5, width, height);

		System.out.println("aStar: " + aStarTime + "     superGridAStar: " + superAStarTime);
	}

	private static long testSuperAStar(int randomSeed, SuperGridAStar superAStar, IAStar astar, IAStarPathMap map, int numberOfPaths, int width,
			int height) {
		Random random = new Random(randomSeed);
		IPathCalculateable requester = new TestPathRequester();
		System.out.println("found path: ");

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}

		MilliStopWatch watch = new MilliStopWatch();
		watch.start();

		for (int i = 0; i < numberOfPaths; i++) {
			ShortPoint2D start = PathfinderSpeedComparision.getUnblocktPosition(requester, random, map, width, height);
			ShortPoint2D target = PathfinderSpeedComparision.getUnblocktPosition(requester, random, map, width, height);

			ArrayList<ShortPoint2D> superPath = superAStar.findPath(start.getX(), start.getY(), target.getX(), target.getY());
			for (int n = 0; n + 2 < superPath.size();) {
				ShortPoint2D pos1 = superPath.get(n++);
				ShortPoint2D pos2 = superPath.get(++n);
				astar.findPath(requester, pos1.getX(), pos1.getY(), pos2.getX(), pos2.getY());
			}

			System.out.print(i + ", ");
		}

		watch.stop();

		System.out.println();
		return watch.getDiff();
	}

	private static long testAStar(int randomSeed, IAStar astar, IAStarPathMap map, int numberOfPaths, int width, int height) {
		Random random = new Random(randomSeed);
		IPathCalculateable requester = new TestPathRequester();
		System.out.println("found path: ");

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
		}

		MilliStopWatch watch = new MilliStopWatch();
		watch.start();

		for (int i = 0; i < numberOfPaths; i++) {
			ShortPoint2D start = PathfinderSpeedComparision.getUnblocktPosition(requester, random, map, width, height);
			ShortPoint2D target = PathfinderSpeedComparision.getUnblocktPosition(requester, random, map, width, height);

			astar.findPath(requester, start.getX(), start.getY(), target.getX(), target.getY());
			System.out.print(i + ", ");
		}

		watch.stop();

		System.out.println();
		return watch.getDiff();
	}

	private static ISuperGridAStarGrid getSuperPathfinderGrid(final MainGrid grid) {
		return new ISuperGridAStarGrid() {
			final IPathCalculateable requester = new TestPathRequester();

			@Override
			public void setDebugColor(int x, int y, Color color) {
				grid.getPathfinderGrid().setDebugColor((short) x, (short) y, color);
			}

			@Override
			public boolean isBlocked(int x, int y) {
				return grid.getPathfinderGrid().isBlocked(requester, (short) x, (short) y);
			}

			@Override
			public void setBlockedChangedListener(IBlockedChangedListener listener) {
			}
		};
	}
}
