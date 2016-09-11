package jsettlers.logic.map.grid.partition.manager.materials.offers;

import jsettlers.common.position.ShortPoint2D;

/**
 * Created by Andreas Eberle on 13.08.2016.
 */
public enum EOfferPriority {
	LOW,
	NORMAL;

	public static final EOfferPriority[] VALUES = values();
	public static final int NUMBER_OF_PRIORITIES = VALUES.length;

	public static final EOfferPriority LOWEST = LOW;
	/**
	 * Offers with this priority can be used by all requesters
	 */
	public static final EOfferPriority OFFER_TO_ALL = NORMAL;
}
