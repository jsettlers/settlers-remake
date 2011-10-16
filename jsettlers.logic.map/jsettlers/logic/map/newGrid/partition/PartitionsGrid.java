package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.partitions.IPartionsAlgorithmMap;
import jsettlers.logic.algorithms.partitions.PartitionsAlgorithm;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.map.newGrid.partition.manageables.IManageableBearer;

/**
 * This class handles the partitions of the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionsGrid implements IPartionsAlgorithmMap {

	private final short width;
	private final short height;
	private final short[][] partitions;
	private final byte[][] player;
	private final boolean[][] borders;
	/**
	 * This array stores the partition objects handled by this class.<br>
	 */
	private final Partition[] partitionObjects = new Partition[1024]; // TODO make the array grow dynamically
	private final Partition nullPartition = new Partition((byte) -1);
	private final PartitionsAlgorithm partitionsManager;

	public PartitionsGrid(final short width, final short height, IAStarPathMap pathfinderMap) {
		this.width = width;
		this.height = height;
		this.partitions = new short[width][height];
		this.player = new byte[width][height];
		this.borders = new boolean[width][height];
		this.partitionsManager = new PartitionsAlgorithm(this, pathfinderMap);

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				this.partitions[x][y] = -1;
				this.player[x][y] = -1;
			}
		}
	}

	@Override
	public byte getPlayerAt(ISPosition2D position) {
		return isInBounds(position) ? this.player[position.getX()][position.getY()] : -1;
	}

	public byte getPlayerAt(short x, short y) {
		return this.player[x][y];
	}

	public short getPartition(short x, short y) {
		return this.partitions[x][y];
	}

	private Partition getPartitionObject(ISPosition2D pos) {
		short partition = getPartition(pos);
		if (partition >= 0)
			return this.partitionObjects[partition];
		else
			return nullPartition;
	}

	@Override
	public short getPartition(ISPosition2D position) {
		return this.partitions[position.getX()][position.getY()];
	}

	@Override
	public void setPartition(ISPosition2D position, short partition) {
		setPartition(position.getX(), position.getY(), partition);
	}

	private void setPartition(short x, short y, short partition) {
		decrement(getPartition(x, y));

		this.partitions[x][y] = partition;

		increment(partition);
	}

	private void decrement(short partition) {
		if (partition >= 0)
			this.partitionObjects[partition].decrement();
	}

	private void increment(short partition) {
		if (partition >= 0)
			this.partitionObjects[partition].increment();
	}

	@Override
	public final short mergePartitions(ISPosition2D firstPos, ISPosition2D secondPos) {
		short firstPartition = getPartition(firstPos);
		short secondPartition = getPartition(secondPos);

		assert firstPartition != -1 && secondPartition != -1 : "-1 partitions can not be merged!!";
		assert firstPos != secondPos : "can not merge two equal partitions";

		short oldPartition;
		short newPartition;
		ISPosition2D startPos;

		// for better performance, relabel the smaller partition
		if (partitionObjects[firstPartition].getNumberOfElements() > partitionObjects[secondPartition].getNumberOfElements()) {
			oldPartition = secondPartition;
			newPartition = firstPartition;
			startPos = secondPos;
		} else {
			oldPartition = firstPartition;
			newPartition = secondPartition;
			startPos = firstPos;
		}

		relabelPartition(startPos.getX(), startPos.getY(), oldPartition, newPartition);

		return newPartition;
	}

	@Override
	public void createPartition(ISPosition2D position, byte player) {
		short partition = initializeNewPartition(player);
		setPartition(position, partition);
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
	public void dividePartition(ISPosition2D changedPosition, ISPosition2D firstPos, ISPosition2D secondPos) {
		System.out.println("DIVIDE!!");
		short newPartition = initializeNewPartition(getPlayerAt(firstPos));
		short oldPartition = getPartition(firstPos);

		partitions[changedPosition.getX()][changedPosition.getY()] = -1;// this is needed, because the new partition is not determined yet
		relabelPartition(firstPos.getX(), firstPos.getY(), oldPartition, newPartition);
		partitions[changedPosition.getX()][changedPosition.getY()] = oldPartition;
	}

	private final short[] neighborhoodMatrix = { 0, 1, 1, 0, 1, -1, 0, -1, -1, 0, -1, 1 };

	private void relabelPartition(short inX, short inY, short oldPartition, short newPartition) {
		final short MAX_LENGTH = 1000;
		final short[] points = new short[MAX_LENGTH];
		points[0] = inX;
		points[1] = inY;
		short length = 2;

		while (length > 0) {
			short y = points[--length];
			short x = points[--length];
			setPartition(x, y, newPartition);

			for (byte i = 0; i < 12; i += 2) {
				short currX = (short) (x + neighborhoodMatrix[i]);
				short currY = (short) (y + neighborhoodMatrix[i + 1]);
				if (isInBounds(currX, currY) && partitions[currX][currY] == oldPartition) {
					if (length < MAX_LENGTH) {
						points[length++] = currX;
						points[length++] = currY;
					} else {
						relabelPartition(currX, currY, oldPartition, newPartition);
					}
				}
			}
		}
	}

	public void changePlayerAt(ISPosition2D position, byte newPlayer) {
		if (this.player[position.getX()][position.getY()] != newPlayer) {
			this.player[position.getX()][position.getY()] = newPlayer;
			this.partitionsManager.calculateNewPartition(position, newPlayer);
		}
	}

	private boolean isInBounds(ISPosition2D position) {
		return isInBounds(position.getX(), position.getY());
	}

	public boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	public boolean pushMaterial(ISPosition2D position, EMaterialType materialType) {
		return getPartitionObject(position).pushMaterial(position, materialType);
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
}
