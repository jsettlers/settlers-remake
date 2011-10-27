package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public class Partition {

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

	public void removePositionTo(ISPosition2D position, Partition newPartitionObject) {
		if (this == newPartitionObject) {
			System.out.println("AAAAAAAAAAAAAAAARRRRRRRRRRRRRRRRGGGGGGGGGGHHHHHHHHHHHHhh");
		}

		assert this != newPartitionObject : "ERROR: newManager can not be the same as this manager!!";

		this.decrement();
		newPartitionObject.increment();
		this.manager.removePositionTo(position, newPartitionObject.manager, newPartitionObject.player == this.player);

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

	public boolean pushMaterial(ISPosition2D position, EMaterialType materialType) {
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

	public void request(ISPosition2D position, EMaterialType materialType, byte priority) {
		manager.request(position, materialType, priority);
	}

	public void requestDiggers(FreeMapArea buildingArea, byte heightAvg, byte amount) {
		manager.requestDiggers(buildingArea, heightAvg, amount);
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

}
