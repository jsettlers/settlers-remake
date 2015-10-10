package jsettlers.logic.stack;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.material.MaterialSet;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.stack.StockBuildingStackGroup.StockBuildingStack;

/**
 * This is a group of stacks that are at the same position. They request materials for a stock building.
 * 
 * @author Michael Zangl
 */
public class StockBuildingStackGroup extends StackGroup<StockBuildingStack> {
	public class StockBuildingStack extends StackGroup.GroupedRequestStack<StockBuildingStack> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5707770600972786482L;

		public StockBuildingStack(EMaterialType materialType) {
			super(StockBuildingStackGroup.this, materialType);
		}

		@Override
		protected void materialDelivered() {
			super.materialDelivered();
			getGrid().createOneStockOffer(getPos(), getMaterialType());
		}

		@Override
		public boolean isStockRequest() {
			return true;
		}

	}

	private MaterialSet materials = new MaterialSet();

	public StockBuildingStackGroup(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType) {
		super(grid, position, buildingType);
	}

	public void setAcceptedMaterials(MaterialSet acceptedMaterials) {
		if (materials.equals(acceptedMaterials)) {
			return;
		}

		this.materials = acceptedMaterials;
		getRequestCounts().loadFrom(acceptedMaterials);
	}

	@Override
	protected void requestMaterialRemoval(EMaterialType material) {
		super.requestMaterialRemoval(material);
		grid.makeStockOffersNormal(position, material);
	}

	@Override
	public StockBuildingStack createStack(EMaterialType m) {
		return new StockBuildingStack(m);
	}

	@Override
	protected EPriority getEmptyStackPriority() {
		return EPriority.STOCK;
	}

	@Override
	protected EPriority getStartedStackPriority() {
		return EPriority.STOCK_STARTED;
	}

}
