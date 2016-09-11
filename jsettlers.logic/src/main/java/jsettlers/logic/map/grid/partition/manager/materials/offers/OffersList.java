/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.logic.map.grid.partition.manager.materials.offers;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.data.IMaterialCounts;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList.IMovedVisitor;
import jsettlers.logic.map.grid.partition.manager.datastructures.PrioritizedPositionableList;

/**
 * This class builds a data structure to hold {@link MaterialOffer}s and access them with range searches.
 *
 * @author Andreas Eberle
 */
public final class OffersList implements IMaterialCounts, Serializable {
	private static final long serialVersionUID = 3747575330300586115L;


	private final PrioritizedPositionableList<EOfferPriority, MaterialOffer>[] offersLists;
	private final short[] numberOfOffers = new short[EMaterialType.NUMBER_OF_MATERIALS];
	private final IOffersCountListener countListener;

	/**
	 * Constructor to create a new {@link OffersList}.
	 */
	@SuppressWarnings("unchecked")
	public OffersList(IOffersCountListener countListener) {
		offersLists = new PrioritizedPositionableList[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			offersLists[i] = new PrioritizedPositionableList<>(EOfferPriority.NUMBER_OF_PRIORITIES);
		}

		if (countListener != null) {
			this.countListener = countListener;
		} else {
			this.countListener = IOffersCountListener.DEFAULT_IMPLEMENTATION;
		}
	}

	/**
	 * Insert an offered material at the given position.
	 *
	 * @param position The position the offered material is located.
	 * @param material The material that is offered at the given position.
	 */
	public void addOffer(ShortPoint2D position, EMaterialType material, EOfferPriority offerPriority) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> list = offersLists[material.ordinal];

		MaterialOffer existingOffer = list.getObjectAt(position, offerPriority);
		if (existingOffer != null) {
			existingOffer.incAmount();
		} else {
			list.insert(offerPriority, new MaterialOffer(position,offerPriority, (byte) 1));
		}

		numberOfOffers[material.ordinal]++;
		countListener.offersCountChanged(material, +1);
	}

	/**
	 * Checks if there are any offers for the given {@link EMaterialType}.
	 *
	 * @param materialType The {@link EMaterialType} to be checked.
	 * @param  minimumIncludedPriority The lowest priority to be included in
	 * @return Returns true if there are no offers for the given {@link EMaterialType},<br>
	 * false otherwise.
	 */
	public boolean isEmpty(EMaterialType materialType, EOfferPriority minimumIncludedPriority) {
		return offersLists[materialType.ordinal].isEmpty(minimumIncludedPriority);
	}

	/**
	 * @param materialType {@link EMaterialType} of the offer.
	 * @param position     The position to be used for the search.
	 * @return Returns an offer of the given {@link EMaterialType} that's close to the given position or <br>
	 * null if no offer for the given {@link EMaterialType} exists.
	 */
	public MaterialOffer removeOfferCloseTo(EMaterialType materialType, EOfferPriority minimumIncludedPriority, ShortPoint2D position) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		MaterialOffer offer = offerSlot.getObjectCloseTo(position, minimumIncludedPriority);

		decrementOfferAmount(offerSlot, materialType, offer);
		return offer;
	}


	private void decrementOfferAmount(PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot, EMaterialType materialType, MaterialOffer offer) {
		if (offer != null) {
			if (offer.decAmount() <= 0) { // if the offer is now empty.
				offerSlot.remove(offer);
			}
			numberOfOffers[materialType.ordinal]--;
			countListener.offersCountChanged(materialType, -1);
		}
	}

	/**
	 * FOR TESTS ONLY!
	 *
	 * @param position position to look for the offer
	 * @param materialType type of material of the offer
	 * @param offerPriority offerPriority of the offer
	 * @return
	 */
	public MaterialOffer getOfferObjectAt(ShortPoint2D position, EMaterialType materialType, EOfferPriority offerPriority) {
		PrioritizedPositionableList<EOfferPriority, MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		return offerSlot.getObjectAt(position, offerPriority);
	}

	public void moveOffersAtPositionTo(ShortPoint2D position, final OffersList otherList) {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			final int materialTypeIdx = i;
			final EMaterialType materialType = EMaterialType.VALUES[materialTypeIdx];
			offersLists[materialTypeIdx].moveObjectsAtPositionTo(position, otherList.offersLists[i], new IMovedVisitor<MaterialOffer>() {
				@Override
				public void visit(MaterialOffer moved) { // correct the counts
					numberOfOffers[materialTypeIdx] -= moved.getAmount();
					countListener.offersCountChanged(materialType, -moved.getAmount());
					otherList.numberOfOffers[materialTypeIdx] += moved.getAmount();
					otherList.countListener.offersCountChanged(materialType, +moved.getAmount());
				}
			});
		}
	}

	public void addAll(OffersList otherList) {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			short amount = otherList.numberOfOffers[i];
			if (amount > 0) {
				offersLists[i].addAll(otherList.offersLists[i]);
				numberOfOffers[i] += amount;
				EMaterialType materialType = EMaterialType.VALUES[i];
				otherList.countListener.offersCountChanged(materialType, -amount);
				countListener.offersCountChanged(materialType, amount);
			}
		}
	}

	@Override
	public int getAmountOf(EMaterialType materialType) {
		return numberOfOffers[materialType.ordinal];
	}
}
