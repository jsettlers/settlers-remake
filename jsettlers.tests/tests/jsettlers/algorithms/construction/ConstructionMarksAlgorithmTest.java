package jsettlers.algorithms.construction;

import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.algorithms.construction.NewConstructionMarksAlgorithm;
import jsettlers.common.buildings.BuildingAreaBitSet;
import jsettlers.common.buildings.BuildingAreaBitSetTest;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.RelativePoint;

import org.junit.Test;

/**
 * Test for class {@link NewConstructionMarksAlgorithm}.
 * 
 * @author Andreas Eberle
 * 
 */
public class ConstructionMarksAlgorithmTest {

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

		TestMap map = new TestMap(blocked);
		NewConstructionMarksAlgorithm algorithm = new NewConstructionMarksAlgorithm(map, (byte) 0);
		algorithm.calculateConstructMarks(mapArea, buildingSet, null, null);

		// print(map, blocked, buildingSet);

		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				assertEquals(x + "|" + y, canCostructAt(map, x, y, buildingSet), map.marksSet[x + y * map.width] > 0);
			}
		}
	}

	@SuppressWarnings("unused")
	private void print(TestMap map, boolean[][] blocked, BuildingAreaBitSet buildingSet) {
		System.out.println("blocked | marksSet | canConstruct");

		for (int y = 0; y < map.height; y++) {
			for (int x = 0; x < map.width; x++) {
				print(blocked[y][x]);
			}
			System.out.print("        ");

			for (int x = 0; x < map.width; x++) {
				print(map.marksSet[x + y * map.width]);
			}
			System.out.print("        ");

			for (int x = 0; x < map.width; x++) {
				print(canCostructAt(map, x, y, buildingSet));
			}
			System.out.println();
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

	private void print(boolean bool) {
		if (bool) {
			System.out.print(" x ");
		} else {
			System.out.print(" - ");
		}
	}

	private boolean canCostructAt(TestMap map, int x, int y, BuildingAreaBitSet buildingSet) {
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

		public TestMap(boolean[][] blocked) {
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

		@Override
		public boolean isInBounds(int x, int y) {
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

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					buffer.append(marksSet[x + y * width]);
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
