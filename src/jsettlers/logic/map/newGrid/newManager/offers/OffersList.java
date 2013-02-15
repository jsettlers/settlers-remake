package jsettlers.logic.map.newGrid.newManager.offers;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableList;

/**
 * This class builds a data structure to hold {@link MaterialOffer}s and access them with range searches.
 * 
 * @author Andreas Eberle
 * 
 */
public final class OffersList implements Serializable {
	private static final long serialVersionUID = 3747575330300586115L;

	private final PositionableList<MaterialOffer>[] offersLists;

	/**
	 * Constructor to create a new {@link OffersList}.
	 */
	@SuppressWarnings("unchecked")
	public OffersList() {
		offersLists = new PositionableList[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			offersLists[i] = new PositionableList<MaterialOffer>();
		}
	}

	/**
	 * Insert an offered material at the given position.
	 * 
	 * @param position
	 *            The position the offered material is located.
	 * @param material
	 *            The material that is offered at the given position.
	 */
	public void addOffer(ShortPoint2D position, EMaterialType material) {
		PositionableList<MaterialOffer> list = offersLists[material.ordinal];

		MaterialOffer existingOffer = list.getObjectAt(position);
		if (existingOffer != null) {
			existingOffer.incAmount();
		} else {
			list.insert(new MaterialOffer(position, (byte) 1));
		}
	}

	/**
	 * Checks if there are any offers for the given {@link EMaterialType}.
	 * 
	 * @param materialType
	 *            The {@link EMaterialType} to be checked.
	 * @return Returns true if there are no offers for the given {@link EMaterialType},<br>
	 *         false otherwise.
	 */
	public boolean isEmpty(EMaterialType materialType) {
		return offersLists[materialType.ordinal].isEmpty();
	}

	/**
	 * 
	 * @param materialType
	 *            {@link EMaterialType} of the offer.
	 * @param position
	 *            The position to be used for the search.
	 * @return Returns an offer of the given {@link EMaterialType} that's close to the given position or <br>
	 *         null if no offer for the given {@link EMaterialType} exists.
	 */
	public MaterialOffer removeOfferCloseTo(EMaterialType materialType, ShortPoint2D position) {
		PositionableList<MaterialOffer> offerSlot = offersLists[materialType.ordinal];
		MaterialOffer offer = offerSlot.getObjectCloseTo(position);

		if (offer != null) {
			if (offer.decAmount() <= 0) { // if the offer is now empty.
				offerSlot.remove(offer);
			}
		}
		return offer;
	}
}
