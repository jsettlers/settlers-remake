package jsettlers.logic.movable.components;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.movable.ManageableWorkerWrapper;
import jsettlers.logic.movable.Notification;

/**
 * @author homoroselaps
 */

public class BricklayerComponent extends Component {
	private static final long serialVersionUID = -3315837368825352398L;

	private IConstructableBuilding constructionSite = null;
	private ShortPoint2D bricklayerTargetPos = null;
	private EDirection lookDirection = null;

	public static class BricklayerJob extends Notification {
		public final IConstructableBuilding constructionSite;
		public final ShortPoint2D bricklayerTargetPos;
		public final EDirection lookDirection;

		public BricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection lookDirection) {
			this.constructionSite = constructionSite;
			this.bricklayerTargetPos = bricklayerTargetPos;
			this.lookDirection = lookDirection;
		}
	}

	public ShortPoint2D getBricklayerTargetPos() { return bricklayerTargetPos; }

	public EDirection getLookDirection() { return lookDirection; }

	public boolean hasJob() {
		return constructionSite != null;
	}

	public void resetJob() {
		constructionSite = null;
		bricklayerTargetPos = null;
		lookDirection = null;
	}

	public void jobFinished() {
		resetJob();
		entity.gameFieldComponent().movableGrid.addJobless(new ManageableWorkerWrapper(entity));
	}

	public void abortJob() {
		if (constructionSite != null) {
			constructionSite.bricklayerRequestFailed(bricklayerTargetPos, lookDirection);
		}
		jobFinished();
	}

	public void setBricklayerJob(BricklayerJob job) {
		constructionSite = job.constructionSite;
		bricklayerTargetPos = job.bricklayerTargetPos;
		lookDirection = job.lookDirection;
	}

	public boolean assignBricklayerJob(IConstructableBuilding constructionSite, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		if (!hasJob()) {
			entity.raiseNotification(new BricklayerComponent.BricklayerJob(constructionSite, bricklayerTargetPos, direction));
			return true;
		}
		return false;
	}

	public boolean isBricklayerRequestActive() {
		return constructionSite != null && constructionSite.isBricklayerRequestActive();
	}

	public boolean tryTakeMaterialFromConstructionSite() {
		return constructionSite != null && constructionSite.tryToTakeMaterial();
	}
}
