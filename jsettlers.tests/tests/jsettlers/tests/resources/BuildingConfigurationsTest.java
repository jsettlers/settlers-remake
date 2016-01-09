package jsettlers.tests.resources;

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

import jsettlers.TestUtils;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.position.RelativePoint;

@RunWith(Parameterized.class)
public class BuildingConfigurationsTest {

	static {
		TestUtils.setupResourcesManager();
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> buildingTypes() {
		List<Object[]> result = new ArrayList<Object[]>();
		for (EBuildingType buildingType : EBuildingType.values) {
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
