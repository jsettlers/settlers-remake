package jsettlers.algorithms;

import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.test.DummyEmptyAStarMap;

public class SimpleDijkstraTester {
	public static void main(String args[]) {
		IDijkstraPathMap map = new IDijkstraPathMap() {
			@Override
			public short getHeight() {
				return 200;
			}

			@Override
			public short getWidth() {
				return 200;
			}

			@Override
			public boolean fitsSearchType(short x, short y, ESearchType type, IPathCalculateable requester) {
				if (x == 120 && y == 100)
					return true;
				if (x == 110 && y == 110)
					return true;
				if (x == 118 && y == 115)
					return true;

				return false;
			}

			@Override
			public void setDijkstraSearched(short x, short y) {
			}
		};
		DummyEmptyAStarMap aStarMap = new DummyEmptyAStarMap((short) 200, (short) 200);
		aStarMap.setBlocked(120, 100, true);

		DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(map, new HexAStar(aStarMap));

		IPathCalculateable requester = new IPathCalculateable() {

			@Override
			public ISPosition2D getPos() {
				return new ShortPoint2D(100, 100);
			}

			@Override
			public byte getPlayer() {
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
