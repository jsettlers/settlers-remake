package jsettlers.logic.map.newGrid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.movable.EMovableType;
import jsettlers.logic.algorithms.queue.ITypeAcceptor;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;

public final class MovableTypeAcceptor implements ITypeAcceptor<IManageableWorker>, Serializable {
	private static final long serialVersionUID = 111392803354934224L;

	public EMovableType movableType = null;

	@Override
	public final boolean accepts(IManageableWorker worker) {
		return this.movableType == worker.getMovableType();
	}
}