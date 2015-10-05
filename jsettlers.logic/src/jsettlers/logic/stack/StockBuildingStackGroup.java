package jsettlers.logic.stack;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is a group of stacks that are at the same position. They request materials for a stock building.
 * 
 * @author Michael Zangl
 */
public class StockBuildingStackGroup {
	public class StockBuildingStack extends RequestStack {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5707770600972786482L;

		public StockBuildingStack(IRequestsStackGrid grid, ShortPoint2D position, EMaterialType materialType, EBuildingType buildingType) {
			super(grid, position, materialType, buildingType, EPriority.STOCK);
		}

		@Override
		public void deliveryAccepted() {
			super.deliveryAccepted();
			if (currentDeliveredMaterial != null && currentDeliveredMaterial != getMaterialType()) {
				throw new IllegalStateException("This stack does not accept deliveries.");
			}
			setDeliveredMaterial(getMaterialType());
		}

		@Override
		protected void materialDelivered() {
			super.materialDelivered();
			grid.createOneStockOffer(getPos(), getMaterialType());
		}

		@Override
		public boolean isStockRequest() {
			return true;
		}
	}

	private final ArrayList<StockBuildingStack> requestStacks = new ArrayList<>();
	private IRequestsStackGrid grid;
	private ShortPoint2D position;
	private EBuildingType buildingType;

	private EMaterialType currentDeliveredMaterial = null;
	private EMaterialType[] materials;

	public StockBuildingStackGroup(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType) {
		this.grid = grid;
		this.position = position;
		this.buildingType = buildingType;
	}

	public void setDeliveredMaterial(EMaterialType materialType) {
		for (StockBuildingStack stack : requestStacks) {
			if (stack.getMaterialType() != materialType) {
				stack.releaseRequests();
			} else {
				// FIXME: This seems to make bearers drop the material.
				stack.setPriority(EPriority.STOCK_STARTED);
			}
		}
	}

	public void setAcceptedMaterials(EMaterialType[] materials) {
		this.materials = materials;
		if (currentDeliveredMaterial == null) {
			releaseAll();
			reAddRequestStacks();
		} else {
			// TODO: Stop request if current material is not in new list.
		}
	}

	private void reAddRequestStacks() {
		requestStacks.clear();
		for (EMaterialType m : materials) {
			StockBuildingStack stack = new StockBuildingStack(grid, position, m, buildingType);
			requestStacks.add(stack);
		}
		System.out.println("Stock: Added all request stacks at " + position);
	}

	public void releaseAll() {
		for (StockBuildingStack stack : requestStacks) {
			stack.releaseRequests();
		}
	}

}
