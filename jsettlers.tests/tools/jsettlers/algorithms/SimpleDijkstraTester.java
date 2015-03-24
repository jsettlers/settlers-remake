package jsettlers.algorithms;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.normal.HexAStar;
import jsettlers.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.algorithms.path.test.DummyEmptyAStarMap;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;

public class SimpleDijkstraTester {
	private static final short WIDTH = (short) 200;
	private static final short HEIGHT = (short) 200;

	public static void main(String args[]) {
		IDijkstraPathMap map = new IDijkstraPathMap() {
			@Override
			public boolean fitsSearchType(int x, int y, ESearchType type, IPathCalculatable requester) {
				if (x == 120 && y == 100)
					return true;
				if (x == 110 && y == 110)
					return true;
				if (x == 118 && y == 115)
					return true;

				return false;
			}

			@Override
			public void setDijkstraSearched(int x, int y) {
			}
		};
		DummyEmptyAStarMap aStarMap = new DummyEmptyAStarMap(WIDTH, HEIGHT);
		aStarMap.setBlocked(120, 100, true);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(map, new HexAStar(aStarMap, WIDTH, HEIGHT), WIDTH, HEIGHT);

		IPathCalculatable requester = new IPathCalculatable() {

			@Override
			public ShortPoint2D getPos() {
				return new ShortPoint2D(100, 100);
			}

			@Override
			public byte getPlayerId() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean needsPlayersGround() {
				return false;
			}
		};
		Path path = dijkstra.find(requester, (short) 100, (short) 100, (short) 1, (short) 30, null);
		System.out.println("path:  " + path);
	}
}
