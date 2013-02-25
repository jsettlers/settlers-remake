package jsettlers.logic.map.newGrid.partition.manager.materials;

import java.io.IOException;

import jsettlers.TestUtils;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.newGrid.partition.manager.settings.PartitionManagerSettings;

import org.junit.Test;

/**
 * This is a test for the {@link MaterialsManager} class.
 * 
 * @author Andreas Eberle
 * 
 */
public class MaterialsManagerTest {
	private final OffersList offersList = new OffersList();
	private final JoblessSupplierMock joblessSupplier = new JoblessSupplierMock();
	private final MaterialsManager manager = new MaterialsManager(joblessSupplier, offersList, new PartitionManagerSettings());

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		offersList.addOffer(pos(20, 20), EMaterialType.PLANK);
		offersList.addOffer(pos(20, 20), EMaterialType.PLANK);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE);

		joblessSupplier.addJoblessAt(new ShortPoint2D(10, 10));

		TestUtils.serializeAndDeserialize(manager);
	}

	private ShortPoint2D pos(int x, int y) {
		return new ShortPoint2D(x, y);
	}

}
