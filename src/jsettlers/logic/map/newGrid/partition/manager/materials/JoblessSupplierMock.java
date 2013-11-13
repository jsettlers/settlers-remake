package jsettlers.logic.map.newGrid.partition.manager.materials;

import java.util.LinkedList;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IMaterialRequest;

public class JoblessSupplierMock implements IJoblessSupplier {
	private static final long serialVersionUID = -4698558305428775896L;

	private LinkedList<IManagerBearer> jobless = new LinkedList<IManagerBearer>();

	public void addJoblessAt(final ShortPoint2D pos) {
		jobless.add(new IManagerBearer() {
			private static final long serialVersionUID = 3833820381369081344L;

			@Override
			public ShortPoint2D getPos() {
				return pos;
			}

			@Override
			public boolean deliver(EMaterialType materialType, ShortPoint2D offerPosition, IMaterialRequest request) {
				request.deliveryAccepted();
				request.deliveryFulfilled();
				return true;
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return jobless.isEmpty();
	}

	@Override
	public IManagerBearer removeJoblessCloseTo(ShortPoint2D position) {
		int closestDist = Integer.MAX_VALUE;
		IManagerBearer closest = null;

		for (IManagerBearer curr : jobless) {
			int currDist = ShortPoint2D.getOnGridDist(curr.getPos().x - position.x, curr.getPos().y - position.y);
			if (closestDist > currDist) {
				closest = curr;
				closestDist = currDist;
			}
		}

		return closest;
	}

}
