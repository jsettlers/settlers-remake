package jsettlers.logic.map.newGrid.partition.manager;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.queue.ITypeAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager.Offer;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableHashMap;

/**
 * The map of offers in the current partition. Keeps track of all offers.
 * <p>
 * It is also an acceptor that accepts all materials for which we have offers.
 * 
 */
public class OfferMap extends PositionableHashMap<PartitionManager.Offer> implements ITypeAcceptor<EMaterialType> {
	private static final long serialVersionUID = 194211819683736498L;

	int[] count = new int[EMaterialType.values().length];

	@Override
	public void set(ISPosition2D position, Offer object) {
		removeObjectAt(position);
		count[object.materialType.ordinal()]++;
		super.set(position, object);
	}

	@Override
	public Offer removeObjectAt(ISPosition2D position) {
		Offer removed = super.removeObjectAt(position);
		if (removed != null) {
			count[removed.materialType.ordinal()]--;
		}
		return removed;
	}

	@Override
	public boolean accepts(EMaterialType type) {
		return count[type.ordinal()] > 0;
	}
}
