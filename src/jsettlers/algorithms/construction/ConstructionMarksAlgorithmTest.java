package jsettlers.algorithms.construction;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import jsettlers.common.buildings.BuildingAreaBitSet;
import jsettlers.common.buildings.BuildingAreaBitSetTest;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.logic.algorithms.construction.NewConstructionMarksAlgorithm;

import org.junit.Test;

/**
 * Test for class {@link NewConstructionMarksAlgorithm}.
 * 
 * @author Andreas Eberle
 * 
 */
public class ConstructionMarksAlgorithmTest {

	private final TestMap map = new TestMap();
	private final NewConstructionMarksAlgorithm algo = new NewConstructionMarksAlgorithm(map, (byte) 0);

	@Test
	public void test() {
		boolean[][] blocked = {
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, true, false, false, false, false, false },
				{ false, false, false, false, false, false, true, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false },
				{ false, false, false, false, false, false, false, false, false, false, false, false } };

		boolean[][] buildingMask = {
				{ false, false, true, false, false },
				{ true, true, true, true, true },
				{ false, false, false, false, false } };

		MapRectangle mapArea = new MapRectangle(-15, -15, 30, 30);

		BuildingAreaBitSet buildingSet = new BuildingAreaBitSet(BuildingAreaBitSetTest.createRelativePoints(buildingMask));

		map.initMap(blocked);
		algo.calculateConstructMarks(mapArea, buildingSet, null, null);

		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				printBool(blocked[y][x]);
			}
			System.out.print("        ");

			for (int x = 0; x < map.width; x++) {
				print(map.marksSet[x + y * map.width]);
			}
			System.out.print("        ");

			for (int x = 0; x < map.width; x++) {
				printBool(canCostructAt(x, y, buildingSet));
			}
			System.out.println();
		}

		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				assertEquals(x + "|" + y, canCostructAt(x, y, buildingSet), map.marksSet[x + y * map.width] > 0);
			}
		}
	}

	private void print(int i) {
		if (i == 0) {
			System.out.print(" 0 ");
		} else if (i == -2) {
			System.out.print(" _ ");
		} else if (i < 0) {
			System.out.print(" - ");
		} else {
			System.out.print(" x ");
		}
	}

	private void printBool(boolean bool) {
		if (bool) {
			System.out.print(" x ");
		} else {
			System.out.print(" - ");
		}
	}

	private boolean canCostructAt(int x, int y, BuildingAreaBitSet buildingSet) {
		for (int dx = 0; dx < buildingSet.width; dx++) {
			for (int dy = 0; dy < buildingSet.height; dy++) {
				int currX = dx + x + buildingSet.minX;
				int currY = dy + y + buildingSet.minY;
				if (buildingSet.bitSet.get(dx + dy * buildingSet.width)
						&& (!map.isInBounds(currX, currY) || map.blockedSet.get(currX + currY * map.width))) {
					return false;
				}
			}
		}

		return true;
	}

	public class TestMap extends AbstractConstructionMarkableMap {
		short width = 10;
		short height = 10;

		int[] marksSet;
		BitSet blockedSet;

		public void initMap(boolean[][] blocked) {
			height = (short) blocked.length;
			width = (short) blocked[0].length;

			marksSet = new int[width * height];
			blockedSet = new BitSet(width * height);

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					blockedSet.set(x + y * width, blocked[y][x]);
				}
			}
		}

		@Override
		public void setConstructMarking(int x, int y, boolean set, RelativePoint[] flattenPositions) {
			if (isInBounds(x, y))
				marksSet[x + y * width] = set ? 1 : -1;
		}

		private boolean isInBounds(int x, int y) {
			return 0 <= x && x < width && 0 <= y && y < height;
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public boolean canUsePositionForConstruction(int x, int y, ELandscapeType[] landscapeTypes, short partitionId) {
			return isInBounds(x, y) && !blockedSet.get(x + y * width);
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();

			for (int y = 0; y < map.height; y++) {
				for (int x = 0; x < map.width; x++) {
					buffer.append(map.marksSet[x + y * map.width]);
				}
				buffer.append("\n");
			}

			return buffer.toString();
		}

		@Override
		public short getPartitionIdAt(int x, int y) {
			return 0;
		}

		@Override
		public boolean canPlayerConstructOnPartition(byte playerId, short partitionId) {
			return true;
		}
	}
}
