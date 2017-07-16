/*
 * Copyright (c) 2016 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package jsettlers.common.buildings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.position.RelativePoint;

@RunWith(Parameterized.class)
public class BuildingConfigurationsTest {

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> buildingTypes() {
		List<Object[]> result = new ArrayList<>();
		for (EBuildingType buildingType : EBuildingType.VALUES) {
			result.add(new Object[] { buildingType });
		}
		return result;
	}

	private final EBuildingType buildingType;

	public BuildingConfigurationsTest(EBuildingType buildingType) {
		this.buildingType = buildingType;
	}

	@Test
	public void testDoorIsNotBlockedButProtected() {
		assumeTrue(EBuildingType.TEMPLE != buildingType); // temple uses door location for the wine bowl
		assumeTrue(EBuildingType.MARKET_PLACE != buildingType); // market place does not use the door

		assertFalse(isBlocked(buildingType.getDoorTile()));
		assertTrue(isProtected(buildingType.getDoorTile()));
	}

	@Test
	public void testStacksAreNotBlockedButProtected() {
		for (RelativeStack stack : buildingType.getConstructionStacks()) {
			assertFalse(isBlocked(stack));
			assertTrue(isProtected(stack));
		}
		for (RelativeStack stack : buildingType.getRequestStacks()) {
			assertFalse(isBlocked(stack));
			assertTrue(isProtected(stack));
		}
		for (RelativeStack stack : buildingType.getOfferStacks()) {
			assertFalse(isBlocked(stack));
			assertTrue(isProtected(stack));
		}
	}

	private boolean isBlocked(RelativePoint position) {
		return contains(buildingType.getBlockedTiles(), position);
	}

	private boolean isProtected(RelativePoint position) {
		return contains(buildingType.getProtectedTiles(), position);
	}

	private static boolean contains(RelativePoint[] positions, RelativePoint positionToCheck) {
		for (RelativePoint current : positions) {
			if (current.equals(positionToCheck)) {
				return true;
			}
		}
		return false;
	}
}
