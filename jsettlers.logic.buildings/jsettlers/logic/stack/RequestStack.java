package jsettlers.logic.stack;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.constants.Constants;

public class RequestStack {

	private final ISPosition2D position;
	private final EMaterialType materialType;
	private final IRequestsStackGrid grid;

	public RequestStack(IRequestsStackGrid grid, ISPosition2D position, EMaterialType materialType) {
		this.grid = grid;
		this.position = position;
		this.materialType = materialType;

		for (int i = 0; i < Constants.STACK_SIZE; i++) {
			requestMaterial();
		}
	}

	protected void requestMaterial() {
		grid.request(position, materialType, (byte) 0);
	}

	public boolean hasMaterial() {
		return grid.hasMaterial(position, materialType);
	}

	public void pop() {
		grid.popMaterial(position, materialType);
		requestMaterial();
	}

	public boolean isFullfilled() {
		return true;
	}

	public ISPosition2D getPosition() {
		return position;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

}
