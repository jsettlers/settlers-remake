package jsettlers.logic.buildings.stack.multi;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.stack.IRequestsStackGrid;

/**
 * Created by Andreas Eberle on 18.09.2016.
 */

public class MultiRequestAndOfferStack extends MultiRequestStack {

	/**
	 * Creates a new bounded {@link MultiRequestStack} to request a limited amount of the given {@link EMaterialType} at the given position.
	 *
	 * @param grid         The {@link IRequestsStackGrid} to be used as base for this {@link AbstractRequestStack}.
	 * @param position     The position the stack will be.
	 * @param buildingType
	 * @param priority
	 * @param sharedData
	 */
	public MultiRequestAndOfferStack(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType, EPriority priority, MultiRequestStackSharedData sharedData) {
		super(grid, position, buildingType, priority, sharedData);
	}
}
