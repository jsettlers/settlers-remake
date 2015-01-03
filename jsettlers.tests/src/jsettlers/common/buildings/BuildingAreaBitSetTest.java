package jsettlers.common.buildings;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.BitSet;

import jsettlers.common.position.RelativePoint;

import org.junit.Test;

/**
 * Test for class {@link BuildingAreaBitSet}.
 * 
 * @author Andreas Eberle
 * 
 */
public class BuildingAreaBitSetTest {

	@Test
	public void testJumpMaps() {
		final boolean[][] blockedMap = {
				{ true, true, true, true, false, false, false },
				{ true, true, false, true, false, true, false },
				{ true, true, true, true, false, true, false },
				{ true, false, true, true, false, false, false },
				{ false, false, false, false, false, false, false },
				{ true, true, false, false, true, true, true },
				{ true, false, false, false, false, false, false } };
		RelativePoint[] relativePoints = createRelativePoints(blockedMap);

		BuildingAreaBitSet buildingArea = new BuildingAreaBitSet(relativePoints);

		assertEquals(blockedMap.length, buildingArea.height);
		assertEquals(blockedMap[0].length, buildingArea.width);

		assertBlockedEquals(blockedMap, buildingArea.bitSet, buildingArea.width);

		final short[][] xJumps = {
				{ 1, 2, 3, 4, 0, 0, 0 },
				{ 1, 2, 0, 1, 0, 1, 0 },
				{ 1, 2, 3, 4, 0, 1, 0 },
				{ 1, 0, 1, 2, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 2, 0, 0, 1, 2, 3 },
				{ 1, 0, 0, 0, 0, 0, 0 } };

		assertArraysEquals(xJumps, buildingArea.xJumps, buildingArea.width);

		final short[][] yJumps = {
				{ 1, 1, 1, 1, 0, 0, 0 },
				{ 2, 2, 0, 2, 0, 1, 0 },
				{ 3, 3, 1, 3, 0, 2, 0 },
				{ 4, 0, 2, 4, 0, 0, 0 },
				{ 0, 0, 0, 0, 0, 0, 0 },
				{ 1, 1, 0, 0, 1, 1, 1 },
				{ 2, 0, 0, 0, 0, 0, 0 } };

		assertArraysEquals(yJumps, buildingArea.yJumps, buildingArea.width);
	}

	private void assertArraysEquals(short[][] array2d, short[] arrayFlat, short width) {
		for (int y = 0; y < array2d.length; y++) {
			for (int x = 0; x < array2d[y].length; x++) {
				assertEquals("(" + x + "|" + y + ") ", array2d[y][x], arrayFlat[x + y * width]);
			}
		}
	}

	private void assertBlockedEquals(boolean[][] blockedMap, BitSet bitSet, short width) {
		for (int y = 0; y < blockedMap.length; y++) {
			for (int x = 0; x < blockedMap[y].length; x++) {
				assertEquals(blockedMap[y][x], bitSet.get(x + y * width));
			}
		}
	}

	public static RelativePoint[] createRelativePoints(boolean[][] blockedMap) {
		ArrayList<RelativePoint> positions = new ArrayList<RelativePoint>();

		int xOffset = blockedMap[0].length / 2;
		int yOffset = blockedMap.length / 2;

		for (int y = 0; y < blockedMap.length; y++) {
			for (int x = 0; x < blockedMap[y].length; x++) {
				if (blockedMap[y][x]) {
					positions.add(new RelativePoint(x - xOffset, y - yOffset));
				}
			}
		}

		return positions.toArray(new RelativePoint[positions.size()]);
	}
}
