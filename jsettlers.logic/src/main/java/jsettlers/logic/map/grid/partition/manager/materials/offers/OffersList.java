/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.manager.materials.offers;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.data.MaterialCounts;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.list.PrioritizedPositionableList;

import java.io.Serializable;

/**
 * This class builds a data structure to hold {@link MaterialOffer}s and access them with range searches.
 *
 * @author Andreas Eberle
 */
public final class OffersList implements Serializable {
	private static final long serialVersionUID = 3747575330300586115L;

	private final PrioritizedPositionableList<EOfferPriority, MaterialOffer>[] offersLists;
	private final MaterialCounts materialCounts;

	/**
	 * Constructor to create a new {@link OffersList}.
	 */
	@SuppressWarnings("unchecked")
	public OffersList(IOffersCountListener countsListener) {
		offersLists = new PrioritizedPositionableList[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			offersLists[i] = new PrioritizedPositionableList<>(EOfferPriority.NUMBER_OF_PRIORITIES);
		}
		this.materialCounts = new MaterialCounts(countsListener);
	}

	/**
	 * Insert an offered material at the given position.
	 *
	 * @param position
	 * 		The position the offered material is located.
	 * @param material
	 * 		The material that is offered at the given position.
	 * @param offerPriority
	 */
	public void addOffer(ShortPoint2D position, EMaterialType material, EOfferPriority offerPriority) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> list = offersLists[material.ordinal];

		MaterialOffer existingOffer = list.getObjectAt(position, offerPriority);
		if (existingOffer != null) {
			existingOffer.incrementAmount();
		} else {
			list.insert(new MaterialOffer(position, material, materialCounts, offerPriority, (byte) 1));
		}
	}

	/**
	 * Insert an offered material at the given position.
	 *
	 * @param position
	 * 		The position the offered material is located.
	 * @param material
	 * 		The material that is offered at the given position.
	 * @param offerPriority
	 * 		The priority of the offer
	 * @param offerListener
	 * 		A listener that will be set to the offer
	 */
	public void addOffer(ShortPoint2D position, EMaterialType material, EOfferPriority offerPriority, IOfferEmptiedListener offerListener) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> list = offersLists[material.ordinal];

		MaterialOffer existingOffer = list.getObjectAt(position, offerPriority);
		if (existingOffer != null && existingOffer instanceof ListenableMaterialOffer) {
			existingOffer.incrementAmount();
		} else {
			list.insert(new ListenableMaterialOffer(position, material, materialCounts, offerPriority, (byte) 1, offerListener));
		}
	}

	/**
	 * Checks if there are any offers for the given {@link EMaterialType}.
	 *
	 * @param materialType
	 * 		The {@link EMaterialType} to be checked.
	 * @param minimumIncludedPriority
	 * 		The lowest priority to be included in
	 * @return Returns true if there are no offers for the given {@link EMaterialType},<br>
	 * false otherwise.
	 */
	public boolean isEmpty(EMaterialType materialType, EOfferPriority minimumIncludedPriority) {
		return offersLists[materialType.ordinal].isEmpty(minimumIncludedPriority);
	}

	/**
	 * @param materialType
	 * 		{@link EMaterialType} of the offer.
	 * @param position
	 * 		The position to be used for the search.
	 * @return Returns an offer of the given {@link EMaterialType} that's close to the given position or <br>
	 * null if no offer for the given {@link EMaterialType} exists.
	 */
	public MaterialOffer getOfferCloseTo(EMaterialType materialType, EOfferPriority minimumIncludedPriority, ShortPoint2D position) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		return offerSlot.getObjectCloseTo(position, minimumIncludedPriority);
	}

	/**
	 * FOR TESTS ONLY!
	 *
	 * @param position
	 * 		position to look for the offer
	 * @param materialType
	 * 		type of material of the offer
	 * @param offerPriority
	 * 		offerPriority of the offer
	 * @return
	 */
	public MaterialOffer getOfferObjectAt(ShortPoint2D position, EMaterialType materialType, EOfferPriority offerPriority) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		return offerSlot.getObjectAt(position, offerPriority);
	}

	public void moveOffersAtPositionTo(ShortPoint2D position, final OffersList otherList) {
		for (int materialTypeIndex = 0; materialTypeIndex < EMaterialType.NUMBER_OF_MATERIALS; materialTypeIndex++) {
			offersLists[materialTypeIndex].moveObjectsAtPositionTo(position, otherList.offersLists[materialTypeIndex], movedOffer -> movedOffer.changeOffersCountListener(otherList.materialCounts));
		}
	}

	public void moveAll(OffersList otherList) {
		for (int materialTypeIndex = 0; materialTypeIndex < EMaterialType.NUMBER_OF_MATERIALS; materialTypeIndex++) {
			offersLists[materialTypeIndex].moveAll(otherList.offersLists[materialTypeIndex], movedOffer -> movedOffer.changeOffersCountListener(materialCounts));
		}
	}

	public void updateOfferPriority(ShortPoint2D position, EMaterialType materialType, EOfferPriority newPriority) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		offerSlot.updatePriorityAt(position, newPriority);
	}

	public MaterialCounts getMaterialCounts() {
		return materialCounts;
	}
}
