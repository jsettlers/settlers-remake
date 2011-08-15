package jsettlers.logic.management.workers.construction;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;

public class BricklayerRequest extends AbstractConstructionWorkerRequest {

	private final IConstructableBuilding constructionSite;
	private final ISPosition2D position;
	private final EDirection lookDirection;

	public BricklayerRequest(IConstructableBuilding constructionSite, ISPosition2D position, EDirection lookDirection) {
		super(EMovableType.BRICKLAYER, constructionSite.getPlayer());
		this.constructionSite = constructionSite;
		this.position = position;
		this.lookDirection = lookDirection;
	}

	@Override
	public ISPosition2D getPos() {
		return position;
	}

	public IConstructableBuilding getConstructionSite() {
		return constructionSite;
	}

	public EDirection getLookDirection() {
		return lookDirection;
	}

}
