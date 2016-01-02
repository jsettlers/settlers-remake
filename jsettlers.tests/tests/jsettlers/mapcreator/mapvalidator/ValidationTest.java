package jsettlers.mapcreator.mapvalidator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jsettlers.TestUtils;
import jsettlers.common.map.MapLoadException;
import jsettlers.logic.map.save.IMapListFactory;
import jsettlers.logic.map.save.MapList;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.mapvalidator.result.AbstractErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ErrorEntry;
import jsettlers.mapcreator.mapvalidator.result.ValidationList;
import jsettlers.mapcreator.mapvalidator.tasks.ValidateResources;

/**
 * Unit Test to test Editor map validation
 * 
 * @author Andreas Butti
 */
public class ValidationTest {

	private IMapListFactory mapListFactory = new IMapListFactory() {
		@Override
		public MapList getMapList() {
			return new MapList(new File("validationmaps"));
		}
	};

	private MapData loadMap(String name) throws MapLoadException {
		MapList mapList = mapListFactory.getMapList();
		return new MapData(mapList.getMapByName(name).getMapData());
	}

	private class TestResultListener implements ValidationResultListener {
		private ValidationList list;

		@Override
		public void validationFinished(ValidationList list) {
			this.list = list;
		}
	};

	private TestResultListener resultListener = new TestResultListener();

	/**
	 * Checks if the result error list contains this ID
	 * 
	 * @param id
	 *            ID to check
	 * @param contains
	 *            true: Should contains, false: should not contains
	 */
	private void assertListContains(String id, boolean contains) {
		boolean found = false;
		ValidationList l = resultListener.list;
		for (int i = 0; i < l.size(); i++) {
			AbstractErrorEntry e = l.get(i);
			if (!(e instanceof ErrorEntry)) {
				continue;
			}
			ErrorEntry entry = (ErrorEntry) e;

			if (entry.getTypeId().equals(id)) {
				found = true;
				break;
			}
		}

		if (contains == found) {
			// check passed
			return;
		}

		System.out.println("=======================");
		for (int i = 0; i < l.size(); i++) {
			AbstractErrorEntry e = l.get(i);
			if (!(e instanceof ErrorEntry)) {
				continue;
			}
			ErrorEntry entry = (ErrorEntry) e;

			System.out.println(entry.getText());
			System.out.println(entry.getTypeId());
			System.out.println("---");
		}
		System.out.println("=======================");

		// check failed
		fail("Check \"" + id + "\" " + contains + " failed!");
	}

	@Before
	public void setUp() {
		TestUtils.setupResourcesManager();
	}

	@Test
	public void testFishOnLand() throws Exception {
		MapData map = loadMap("test_fish_on_land");

		ValidatorRunnable validator = new ValidatorRunnable(resultListener, map, new ValidateResources());
		validator.run();

		assertNotNull(resultListener.list);
		assertListContains("resource.text", true);
	}

	@Test
	public void testFishValid() throws Exception {
		MapData map = loadMap("test_fish_valid");

		ValidatorRunnable validator = new ValidatorRunnable(resultListener, map, new ValidateResources());
		validator.run();

		assertNotNull(resultListener.list);
		assertListContains("validation.resource.text", false);
	}

}
