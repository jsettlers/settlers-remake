package jsettlers.logic.map.grid.partition.manager.materials.offers;

/**
 * Created by Andreas Eberle on 13.08.2016.
 */
public enum EOfferPriority {
	LOW,
	NORMAL;

	public static final EOfferPriority[] VALUES = values();
	public static final int NUMBER_OF_PRIORITIES = VALUES.length;
}
