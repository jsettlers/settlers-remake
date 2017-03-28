/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.manager.materials;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;
import jsettlers.testutils.TestUtils;
import org.junit.Test;

import java.io.IOException;

/**
 * This is a test for the {@link MaterialsManager} class.
 * 
 * @author Andreas Eberle
 * 
 */
public class MaterialsManagerTest {
	private final OffersList offersList = new OffersList(null);
	private final JoblessSupplierMock joblessSupplier = new JoblessSupplierMock();
	private final MaterialsManager manager = new MaterialsManager(joblessSupplier, offersList, new PartitionManagerSettings());

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		offersList.addOffer(pos(20, 20), EMaterialType.PLANK, EOfferPriority.OFFER_TO_ALL);
		offersList.addOffer(pos(20, 20), EMaterialType.PLANK, EOfferPriority.OFFER_TO_ALL);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE, EOfferPriority.OFFER_TO_ALL);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE, EOfferPriority.OFFER_TO_ALL);
		offersList.addOffer(pos(20, 20), EMaterialType.STONE, EOfferPriority.OFFER_TO_ALL);

		joblessSupplier.addJoblessAt(new ShortPoint2D(10, 10));

		TestUtils.serializeAndDeserialize(manager);
	}

	private ShortPoint2D pos(int x, int y) {
		return new ShortPoint2D(x, y);
	}

}
