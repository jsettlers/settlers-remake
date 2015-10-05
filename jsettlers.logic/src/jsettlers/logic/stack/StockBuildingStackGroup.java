package jsettlers.logic.stack;

import java.util.ArrayList;
import java.util.Iterator;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.material.MaterialSet;
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
			if (getCurrentDeliveredStack() != null && getCurrentDeliveredStack() != this) {
				throw new IllegalStateException("This stack does not accept deliveries.");
			}
			setDeliveredStack(this);
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

		public boolean isActiveStockStack() {
			return isinDelivery() || hasMaterial();
		}

		public void killEvent() {
			releaseRequests();
			// TODO: Convert offers to non-stock offers.
		}
	}

	private final ArrayList<StockBuildingStack> requestStacks = new ArrayList<>();
	private IRequestsStackGrid grid;
	private ShortPoint2D position;
	private EBuildingType buildingType;

	/**
	 * The stack that currently receives the delivery.
	 */
	private StockBuildingStack currentDeliveredStack = null;
	private MaterialSet materials = new MaterialSet();

	public StockBuildingStackGroup(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType) {
		this.grid = grid;
		this.position = position;
		this.buildingType = buildingType;
	}

	public void setDeliveredStack(StockBuildingStack currentDeliveredStack) {
		if (currentDeliveredStack == getCurrentDeliveredStack()) {
			// ignored.
			return;
		}
		this.currentDeliveredStack = currentDeliveredStack;

		for (StockBuildingStack stack : requestStacks) {
			if (stack != currentDeliveredStack) {
				stack.releaseRequests();
			} else {
				// FIXME: This seems to make bearers drop the material.
				stack.setPriority(EPriority.STOCK_STARTED);
			}
		}
	}

	public StockBuildingStack getCurrentDeliveredStack() {
		if (currentDeliveredStack != null && !currentDeliveredStack.isActiveStockStack()) {
			currentStackInactivated();
		}
		return currentDeliveredStack;
	}

	/**
	 * The current stack is not active any more (e.g. empty).
	 * <p>
	 * Allow all stacks to re-request the materials.
	 */
	private void currentStackInactivated() {
		currentDeliveredStack.releaseRequests();
		currentDeliveredStack = null;
		reAddRequestStacks();
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

	public void setAcceptedMaterials(MaterialSet acceptedMaterials) {
		if (materials.equals(acceptedMaterials)) {
			return;
		}
		for (Iterator<StockBuildingStack> iterator = requestStacks.iterator(); iterator.hasNext();) {
			StockBuildingStack stack = iterator.next();
			if (!acceptedMaterials.contains(stack.getMaterialType())) {
				// new material
				stack.releaseRequests();
				iterator.remove();
			}
		}

		for (EMaterialType m : acceptedMaterials.toArray()) {
			if (!materials.contains(m)) {
				// new material
				addStack(m);
			}
		}

		this.materials = acceptedMaterials;
	}

	private void addStack(EMaterialType m) {
		if (getCurrentDeliveredStack() == null) {
			requestStacks.add(new StockBuildingStack(grid, position, m, buildingType));
		}
	}

	private void reAddRequestStacks() {
		requestStacks.clear();
		for (EMaterialType m : materials.toArray()) {
			addStack(m);
		}
		System.out.println("Stock: Added all request stacks at " + position);
	}

	public void killEvent() {
		for (StockBuildingStack stack : requestStacks) {
			stack.killEvent();
		}
	}
}
