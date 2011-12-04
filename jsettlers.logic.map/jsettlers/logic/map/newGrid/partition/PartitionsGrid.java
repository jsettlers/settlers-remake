package jsettlers.logic.map.newGrid.partition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jsettlers.common.Color;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.partitions.IPartionsAlgorithmMap;
import jsettlers.logic.algorithms.partitions.PartitionsAlgorithm;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IMaterialRequester;

/**
 * This class handles the partitions of the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionsGrid implements IPartionsAlgorithmMap, Serializable {
	private static final long serialVersionUID = 8919380724171427679L;

	private final short width;
	private final short height;
	private final short[][] partitions;
	private final byte[][] towers;
	private final boolean[][] borders;
	/**
	 * This array stores the partition objects handled by this class.<br>
	 */
	private final Partition[] partitionObjects = new Partition[1024]; // TODO make the array grow dynamically
	private final Partition nullPartition;
	transient private PartitionsAlgorithm partitionsAlgorithm;
	private final IPartitionableGrid grid;

	private final IAStarPathMap pathfinderMap;

	public PartitionsGrid(final short width, final short height, IPartitionableGrid grid, IAStarPathMap pathfinderMap) {
		this.width = width;
		this.height = height;
		this.grid = grid;
		this.pathfinderMap = pathfinderMap;
		this.partitions = new short[width][height];
		this.towers = new byte[width][height];
		this.borders = new boolean[width][height];
		this.partitionsAlgorithm = new PartitionsAlgorithm(this, pathfinderMap);
		this.nullPartition = new Partition((byte) -1, height * width);

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				this.partitions[x][y] = -1;
			}
		}
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		this.partitionsAlgorithm = new PartitionsAlgorithm(this, pathfinderMap);
	}

	@Override
	public byte getPlayerAt(ISPosition2D position) {
		return isInBounds(position) ? this.getPartitionObject(position.getX(), position.getY()).getPlayer() : -1;
	}

	public byte getPlayerAt(short x, short y) {
		return getPartitionObject(x, y).getPlayer();
	}

	public short getPartitionAt(short x, short y) {
		return this.partitions[x][y];
	}

	private Partition getPartitionObject(ISPosition2D pos) {
		return getPartitionObject(getPartition(pos));
	}

	private Partition getPartitionObject(short x, short y) {
		return getPartitionObject(getPartitionAt(x, y));
	}

	private Partition getPartitionObject(short partition) {
		if (partition >= 0)
			return this.partitionObjects[partition];
		else
			return nullPartition;
	}

	@Override
	public short getPartition(ISPosition2D position) {
		return getPartition(position.getX(), position.getY());
	}

	@Override
	public short getPartition(short x, short y) {
		return this.partitions[x][y];
	}

	@Override
	public void setPartition(final short x, final short y, short newPartition) {
		Partition newPartitionObject = getPartitionObject(newPartition);

		Partition oldPartitionObject = getPartitionObject(x, y);
		oldPartitionObject.removePositionTo(x, y, newPartitionObject);
		grid.setDebugColor(x, y, Color.GREEN);

		this.partitions[x][y] = newPartition;

		grid.changedPartitionAt(x, y);
	}

	public void setPartitionAndPlayerAt(short x, short y, short partition) {
		this.partitions[x][y] = partition;
	}

	@Override
	public final short mergePartitions(final short x1, final short y1, final short x2, final short y2) {
		System.out.println("MERGE!!");

		short firstPartition = getPartition(x1, y1);
		short secondPartition = getPartition(x2, y2);

		assert firstPartition != -1 && secondPartition != -1 : "-1 partitions can not be merged!!";
		assert x1 != x2 || y1 != y2 : "can not merge two equal partitions";

		Partition firstObject = partitionObjects[firstPartition];
		Partition secondObject = partitionObjects[secondPartition];

		short newPartition;

		// for better performance, relabel the smaller partition
		if (firstObject.getNumberOfElements() > secondObject.getNumberOfElements()) {
			newPartition = firstPartition;
			relabelPartition(x2, y2, secondPartition, firstPartition, true);
			secondObject.mergeInto(firstObject);
		} else {
			newPartition = secondPartition;
			relabelPartition(x1, y1, firstPartition, secondPartition, true);
			firstObject.mergeInto(secondObject);

		}

		return newPartition;
	}

	@Override
	public void createPartition(final short x, final short y, byte player) {
		short partition = initializeNewPartition(player);
		setPartition(x, y, partition);
	}

	private short initializeNewPartition(byte player) {
		short partition = getFreePartitionIndex();
		this.partitionObjects[partition] = new Partition(player);
		return partition;
	}

	private short getFreePartitionIndex() {
		for (short i = 0; i < this.partitionObjects.length; i++) {
			if (this.partitionObjects[i] == null || this.partitionObjects[i].isEmpty())
				return i;
		}

		System.err.println("HAVE NO PARTITIONS LEFT!!!");
		return (short) (this.partitionObjects.length - 1);
	}

	@Override
	public void dividePartition(final short x, final short y, ISPosition2D firstPos, ISPosition2D secondPos) {
		System.out.println("DIVIDE!!");
		short newPartition = initializeNewPartition(getPlayerAt(firstPos));
		short oldPartition = getPartition(firstPos);

		partitions[x][y] = -1;// this is needed, because the new partition is not determined yet
		relabelPartition(secondPos.getX(), secondPos.getY(), oldPartition, newPartition, false);
		partitions[x][y] = oldPartition;
	}

	private final byte[] neighborX = EDirection.getXDeltaArray();
	private final byte[] neighborY = EDirection.getYDeltaArray();

	/**
	 * 
	 * @param inX
	 * @param inY
	 * @param oldPartition
	 * @param newPartition
	 * @param justRelabel
	 *            if true, only the partition will be changed.<br>
	 *            if false, the partition will be changed and for every changed position the contents of that position in the old manager will be
	 *            moved to the new manager.
	 */
	private void relabelPartition(short inX, short inY, short oldPartition, short newPartition, boolean justRelabel) {
		final short MAX_LENGTH = 1000;
		final short[] pointsBuffer = new short[MAX_LENGTH]; // array is used to reduce the number of recursions
		pointsBuffer[0] = inX;
		pointsBuffer[1] = inY;
		short length = 2;

		while (length > 0) {
			short y = pointsBuffer[--length];
			short x = pointsBuffer[--length];
			if (partitions[x][y] != oldPartition) {
				continue; // the partition may already have changed.
			}

			if (justRelabel) {
				this.partitions[x][y] = newPartition;
			} else {
				setPartition(x, y, newPartition);
			}
			boolean currIsBlocked = grid.isBlocked(x, y);

			for (byte i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				short currX = (short) (x + neighborX[i]);
				short currY = (short) (y + neighborY[i]);
				if (isInBounds(currX, currY) && partitions[currX][currY] == oldPartition && (!currIsBlocked || grid.isBlocked(currX, currY))) {
					if (length < MAX_LENGTH) {
						pointsBuffer[length++] = currX;
						pointsBuffer[length++] = currY;
					} else {
						relabelPartition(currX, currY, oldPartition, newPartition, justRelabel);
					}
				}
			}
		}
	}

	public void changePlayerAt(short x, short y, byte newPlayer) {
		if (getPlayerAt(x, y) != newPlayer) {
			this.partitionsAlgorithm.calculateNewPartition(x, y, newPlayer);
		}
	}

	private boolean isInBounds(ISPosition2D position) {
		return isInBounds(position.getX(), position.getY());
	}

	@Override
	public boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	public boolean pushMaterial(ISPosition2D position, EMaterialType materialType) {
		return getPartitionObject(position.getX(), position.getY()).pushMaterial(position, materialType);
	}

	public void setBorderAt(short x, short y, boolean isBorder) {
		this.borders[x][y] = isBorder;
	}

	public boolean isBorderAt(short x, short y) {
		return borders[x][y];
	}

	public void addJobless(IManageableBearer manageable) {
		getPartitionObject(manageable.getPos()).addJobless(manageable);
	}

	public void addJobless(IManageableWorker worker) {
		getPartitionObject(worker.getPos()).addJobless(worker);
	}

	public void addJobless(IManageableBricklayer bricklayer) {
		getPartitionObject(bricklayer.getPos()).addJobless(bricklayer);
	}

	public void addJobless(IManageableDigger digger) {
		getPartitionObject(digger.getPos()).addJobless(digger);
	}

	public void request(IMaterialRequester requester, EMaterialType materialType, byte priority) {
		getPartitionObject(requester.getPos()).request(requester, materialType, priority);
	}

	public void requestDiggers(FreeMapArea buildingArea, byte heightAvg, byte amount) {
		getPartitionObject(buildingArea.get(0)).requestDiggers(buildingArea, heightAvg, amount);
	}

	public void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
		getPartitionObject(building.getPos()).requestBricklayer(building, bricklayerTargetPos, direction);
	}

	public void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
		getPartitionObject(workerBuilding.getPos()).requestBuildingWorker(workerType, workerBuilding);
	}

	@Override
	public boolean isBlockedForPeople(short x, short y) {
		return grid.isBlocked(x, y);
	}

	public void requestSoilderable(Barrack barrack) {
		getPartitionObject(barrack.getDoor()).requestSoilderable(barrack);
	}

	public void releaseRequestsAt(ISPosition2D position, EMaterialType materialType) {
		getPartitionObject(position).releaseRequestsAt(position, materialType);
	}

	public List<ISPosition2D> occupyArea(MapShapeFilter toBeOccupied, ISPosition2D occupiersPosition, byte newPlayer) {
		changePlayerAt(occupiersPosition.getX(), occupiersPosition.getY(), newPlayer);

		short newPartition = getPartition(occupiersPosition);
		List<ISPosition2D> occupiedPositions = new ArrayList<ISPosition2D>();

		for (ISPosition2D curr : toBeOccupied) {
			short x = curr.getX();
			short y = curr.getY();
			if (getPartitionAt(x, y) != newPartition && towers[x][y] <= 0) {
				towers[x][y]++;
				occupiedPositions.add(curr);
				setPartition(x, y, newPartition);
			}
		}

		return occupiedPositions;
	}

	public boolean isEnforcedByTower(short x, short y) {
		return towers[x][y] > 0;
	}

	public void freeOccupiedArea(MapShapeFilter occupied, ISPosition2D occupiersPosition) {
		short partiton = getPartition(occupiersPosition);

		for (ISPosition2D curr : occupied) {
			short x = curr.getX();
			short y = curr.getY();
			if (getPartitionAt(x, y) == partiton) {
				towers[x][y]--;
				if (towers[x][y] <= 0) {
					// TODO check towers next to this
				}
			}
		}
	}
}
