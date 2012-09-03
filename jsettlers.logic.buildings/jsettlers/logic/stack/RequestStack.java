package jsettlers.logic.stack;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

public class RequestStack implements Serializable, IMaterialRequester {
	private static final long serialVersionUID = 8082718564781798767L;

	private final ShortPoint2D position;
	private final EMaterialType materialType;
	private final IRequestsStackGrid grid;

	private boolean requesting = true;

	public RequestStack(IRequestsStackGrid grid, ShortPoint2D position, EMaterialType materialType) {
		this.grid = grid;
		this.position = position;
		this.materialType = materialType;

		for (int i = 0; i < Constants.STACK_SIZE; i++) {
			requestMaterial();
		}
	}

	protected void requestMaterial() {
		if (requesting)
			grid.request(this, materialType, (byte) 0);
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

	public ShortPoint2D getPosition() {
		return position;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public byte getStackSize() {
		return grid.getStackSize(position, materialType);
	}

	public void releaseRequests() {
		requesting = false;
		grid.releaseRequestsAt(position, materialType);
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	@Override
	public boolean isDiggerRequestActive() {
		return requesting;
	}

	@Override
	public void requestFailed() {
		requestMaterial(); // so just request again
	}

}
