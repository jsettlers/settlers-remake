package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IBarrack;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Partition implements Serializable {
	private static final long serialVersionUID = -2087692347209993840L;

	private int counter = 0;
	private final byte player;
	private final PartitionManager manager;

	public Partition(final byte player) {
		this.player = player;
		this.manager = new PartitionManager();
	}

	public Partition(byte player, int size) {
		this(player);
		this.counter = size;
	}

	void decrement() {
		counter--;
	}

	public void increment() {
		counter++;
	}

	public void removePositionTo(final short x, final short y, final Partition newPartitionObject) {
		assert this != newPartitionObject : "ERROR: newManager can not be the same as this manager!!";

		this.decrement();
		newPartitionObject.increment();
		this.manager.removePositionTo(x, y, newPartitionObject.manager, newPartitionObject.player == this.player);

		if (isEmpty())
			manager.stop();
	}

	public boolean isEmpty() {
		return counter <= 0;
	}

	public byte getPlayer() {
		return player;
	}

	public int getNumberOfElements() {
		return counter;
	}

	public boolean pushMaterial(ShortPoint2D position, EMaterialType materialType) {
		return manager.addOffer(position, materialType);
	}

	public void addJobless(IManageableBearer manageable) {
		manager.addJobless(manageable);
	}

	public void addJobless(IManageableBricklayer bricklayer) {
		manager.addJobless(bricklayer);
	}

	public void addJobless(IManageableWorker worker) {
		manager.addJobless(worker);
	}

	public void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
		manager.request(requester, materialType, priority);
	}

	public void requestDiggers(IDiggerRequester requester, byte amount) {
		manager.requestDiggers(requester, amount);
	}

	public void addJobless(IManageableDigger digger) {
		manager.addJobless(digger);
	}

	public void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		manager.requestBricklayer(building, bricklayerTargetPos, direction);
	}

	public void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
		manager.requestBuildingWorker(workerType, workerBuilding);
	}

	public void requestSoilderable(IBarrack barrack) {
		manager.requestSoilderable(barrack);
	}

	public void mergeInto(Partition newPartition) {
		manager.mergeInto(newPartition.manager);
	}

	public void releaseRequestsAt(ShortPoint2D position, EMaterialType materialType) {
		manager.releaseRequestsAt(position, materialType);
	}

	public void removeOfferAt(ShortPoint2D pos, EMaterialType materialType) {
		manager.removeOfferAt(pos, materialType);
	}

	public EMaterialType popToolProduction(ShortPoint2D pos) {
		return manager.popToolProduction(pos);
	}

}
