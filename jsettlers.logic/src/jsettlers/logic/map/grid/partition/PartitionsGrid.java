/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.partition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.algorithms.traversing.ITraversingVisitor;
import jsettlers.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.FilteredMapArea;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.MutableInt;
import jsettlers.common.utils.Tuple;
import jsettlers.common.utils.collections.IPredicate;
import jsettlers.common.utils.collections.ISerializablePredicate;
import jsettlers.common.utils.collections.IteratorFilter;
import jsettlers.logic.map.grid.flags.IBlockingChangedListener;
import jsettlers.logic.map.grid.partition.data.PartitionDataSupplier;
import jsettlers.logic.map.grid.partition.manager.PartitionManager;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.Team;

/**
 * This class handles the partitions of the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionsGrid implements Serializable, IBlockingChangedListener {
	private static final long serialVersionUID = 8919380724171427679L;

	private static final int NUMBER_OF_START_PARTITION_OBJECTS = 3000;
	private static final float PARTITIONS_EXPAND_FACTOR = 1.5f;

	private static final short NO_PLAYER_PARTITION_ID = 0;

	private final PartitionOccupyingTowerList occupyingTowers = new PartitionOccupyingTowerList();

	final short width;
	final short height;
	private final Player[] players;
	private final IBlockingProvider blockingProvider;

	private final Team[] teams;

	final short[] partitions;
	private final byte[] towers;

	Partition[] partitionObjects = new Partition[NUMBER_OF_START_PARTITION_OBJECTS];

	private final short[] blockedPartitionsForPlayers;

	private transient Object partitionsWriteLock;
	private transient IPlayerChangedListener playerChangedListener = IPlayerChangedListener.DEFAULT_IMPLEMENTATION;

	public PartitionsGrid(short width, short height, byte numberOfPlayers, IPartitionsGridBlockingProvider blockingProvider) {
		this.width = width;
		this.height = height;
		this.blockingProvider = blockingProvider;
		blockingProvider.registerBlockingChangedListener(this);

		this.players = new Player[numberOfPlayers]; // create the players.
		this.blockedPartitionsForPlayers = new short[numberOfPlayers];
		this.teams = new Team[numberOfPlayers];
		for (byte playerId = 0; playerId < numberOfPlayers; playerId++) {
			Team team = new Team(playerId);
			this.players[playerId] = new Player(playerId, team, numberOfPlayers);
			this.teams[playerId] = team;
			this.blockedPartitionsForPlayers[playerId] = createNewPartition(playerId); // create a blocked partition for every player
		}

		this.partitions = new short[width * height];
		this.towers = new byte[width * height];

		// the no player partition (the manager won't be started)
		this.partitionObjects[NO_PLAYER_PARTITION_ID] = new Partition(NO_PLAYER_PARTITION_ID, (byte) -1, width * height);

		initAdditionalFields();
	}

	public short getWidth() {
		return width;
	}

	public short getHeight() {
		return height;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		int normalizedPartitions = checkNormalizePartitions(0);
		System.out.println("Normalized " + normalizedPartitions + " partitions");
		oos.defaultWriteObject();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		initAdditionalFields();
	}

	private void initAdditionalFields() {
		partitionsWriteLock = new Object();
	}

	public boolean isDefaultPartition(short partitionId) {
		return partitionId == NO_PLAYER_PARTITION_ID;
	}

	public short getPartitionIdAt(int x, int y) {
		return partitionObjects[partitions[x + y * width]].partitionId;
	}

	/**
	 * ONLY FOR TESTING: <br>
	 * This method gives the currently set partition at the given partition and not their representative. <br>
	 * Therefore it can be used for viewing the unmerged partitions.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public short getRealPartitionIdAt(int x, int y) {
		return partitions[x + y * width];
	}

	public Partition getPartitionAt(int x, int y) {
		return partitionObjects[partitions[x + y * width]];
	}

	public PartitionManager getPartitionAt(ILocatable locatable) {
		ShortPoint2D pos = locatable.getPos();
		return getPartitionAt(pos.x, pos.y);
	}

	public byte getPlayerIdAt(int x, int y) {
		return getPartitionAt(x, y).playerId;
	}

	public Player getPlayer(int playerId) {
		return players[playerId];
	}

	public Player getPlayerAt(int x, int y) {
		short playerId = partitionObjects[partitions[x + y * width]].playerId;
		if (playerId >= 0) {
			return players[playerId];
		} else {
			return null;
		}
	}

	public boolean ownsPlayerPartition(short partitionId, byte playerId) {
		return partitionObjects[partitionId].playerId == playerId;
	}

	public byte getTowerCountAt(int x, int y) {
		return towers[x + y * width];
	}

	public boolean isEnforcedByTower(int x, int y) {
		return towers[x + y * width] > 0;
	}

	/**
	 * Occupies the given area for the given playerId if it is not already occupied by towers of an enemy.
	 * 
	 * @param playerId
	 *            The id of the occupying player.
	 * @param influencingArea
	 *            The area affected by the tower.
	 */
	public void addTowerAndOccupyArea(byte playerId, MapCircle influencingArea) {
		IMapArea filteredArea = new FilteredMapArea(influencingArea, new ISerializablePredicate<ShortPoint2D>() {
			private static final long serialVersionUID = -6460916149912865762L;

			@Override
			public boolean evaluate(ShortPoint2D pos) {
				return 0 <= pos.x && pos.x < width && 0 <= pos.y && pos.y < height;
			}
		});

		// occupy the area
		occupyArea(playerId, filteredArea, influencingArea.getBorders());

		// add the new tower object
		occupyingTowers.add(new PartitionOccupyingTower(playerId, influencingArea.getCenter(), filteredArea, influencingArea.getBorders(),
				(int) influencingArea.getRadius()));
	}

	/**
	 * Removes the tower at the given position from the grid.
	 * 
	 * @param pos
	 *            The position of the tower.
	 * @return
	 */
	public Iterable<ShortPoint2D> removeTowerAndFreeOccupiedArea(ShortPoint2D pos) {
		// get the tower object and the informations of it.
		PartitionOccupyingTower tower = occupyingTowers.removeAt(pos);
		if (tower == null) {
			return new LinkedList<ShortPoint2D>();
		}

		// reduce the tower counter
		changeTowerCounter(tower.playerId, tower.area, -1);

		checkOtherTowersInArea(tower);

		return tower.area;
	}

	/**
	 * Changes the player of the tower at given position to the new player. After this operation, the given ground area will always be occupied by the
	 * new player.
	 * 
	 * @param towerPosition
	 * @param newPlayerId
	 * @param groundArea
	 * @return
	 */
	public Iterable<ShortPoint2D> changePlayerOfTower(ShortPoint2D towerPosition, byte newPlayerId, final FreeMapArea groundArea) {
		// get the tower object and the informations of it.
		PartitionOccupyingTower tower = occupyingTowers.removeAt(towerPosition);
		if (tower == null) {
			return new LinkedList<ShortPoint2D>(); // return if no tower has been found
		}

		IteratorFilter<ShortPoint2D> areaWithoutGround = new IteratorFilter<ShortPoint2D>(tower.area, new IPredicate<ShortPoint2D>() {
			@Override
			public boolean evaluate(ShortPoint2D pos) {
				return !groundArea.contains(pos);
			}
		});
		// reduce the tower counter
		changeTowerCounter(tower.playerId, areaWithoutGround, -1);

		// let the other towers occupy the area
		checkOtherTowersInArea(tower);

		// set the tower counter of the groundArea to 0 => the ground area will be occupied
		for (ShortPoint2D currPos : groundArea) {
			towers[currPos.x + currPos.y * width] = 0;
		}

		// occupy the area for the new player
		occupyArea(newPlayerId, tower.area, tower.areaBorders);
		PartitionOccupyingTower newTower = new PartitionOccupyingTower(newPlayerId, tower);
		occupyingTowers.add(newTower);

		// recalculate the tower counter for the ground area
		recalculateTowerCounter(newTower, groundArea);

		return newTower.area;
	}

	public void changePlayerAt(ShortPoint2D position, byte playerId) {
		int idx = position.x + position.y * width;
		if (towers[idx] <= 0) {
			short newPartition = createNewPartition(playerId);
			changePartitionUncheckedAt(position.x, position.y, newPartition);
			notifyPlayerChangedListener(position.x, position.y, playerId);

			PartitionsListingBorderVisitor borderVisitor = new PartitionsListingBorderVisitor(this, blockingProvider);
			// visit the direct neighbors of the position
			for (EDirection currDir : EDirection.values) {
				borderVisitor.visit(currDir.gridDeltaX + position.x, currDir.gridDeltaY + position.y);
			}

			checkMergesAndDividesOnPartitionsList(playerId, newPartition, borderVisitor.getPartitionsList());
		}
	}

	/**
	 * Recalculates the tower counter for the given area. <br>
	 * NOTE: The given area must completely belong to the given player!
	 * 
	 * @param area
	 * @param playerId
	 */
	private void recalculateTowerCounter(PartitionOccupyingTower tower, IMapArea area) {
		List<Tuple<Integer, PartitionOccupyingTower>> towersInRange = occupyingTowers.getTowersInRange(tower.position, tower.radius);

		for (ShortPoint2D curr : area) {
			towers[curr.x + curr.y * width] = 0;
		}

		for (Tuple<Integer, PartitionOccupyingTower> currTower : towersInRange) {
			if (currTower.e2.playerId == tower.playerId) {
				final IMapArea currArea = currTower.e2.area;

				// define the positions that need to get their towers count increased.
				IteratorFilter<ShortPoint2D> increasePositions = new IteratorFilter<ShortPoint2D>(area, new IPredicate<ShortPoint2D>() {
					@Override
					public boolean evaluate(ShortPoint2D position) {
						return currArea.contains(position);
					}
				});

				for (ShortPoint2D curr : increasePositions) {
					towers[curr.x + curr.y * width]++;
				}
			}
		}
	}

	/**
	 * Checks if other towers that intersect the area of the given tower can occupy free positions of the area of the given tower and lets them do so.
	 * 
	 * @param tower
	 */
	private void checkOtherTowersInArea(PartitionOccupyingTower tower) {
		// get the positions that may change their owner.
		IPredicate<ShortPoint2D> predicate = new IPredicate<ShortPoint2D>() {
			@Override
			public boolean evaluate(ShortPoint2D pos) {
				final int idx = pos.x + pos.y * width;
				return towers[idx] <= 0;
			}
		};
		// save the free positions in the list because the list must not change during the otherTowers loop
		ArrayList<ShortPoint2D> freedPositions = new IteratorFilter<ShortPoint2D>(tower.area, predicate).toList();

		// check if other towers occupy the area
		if (!freedPositions.isEmpty()) { // if at least one position may change the player
			List<Tuple<Integer, PartitionOccupyingTower>> towersInRange = occupyingTowers.getTowersInRange(tower.position,
					tower.radius);
			// sort the towers by their distance to the removed tower
			Collections.sort(towersInRange, Tuple.<Integer, PartitionOccupyingTower> getE1Comparator());

			for (Tuple<Integer, PartitionOccupyingTower> curr : towersInRange) {
				if (curr.e2.playerId == tower.playerId) {
					continue; // only work on towers of other players.
				}

				PartitionOccupyingTower currTower = curr.e2;

				final IMapArea currArea = currTower.area;

				IteratorFilter<ShortPoint2D> area = new IteratorFilter<ShortPoint2D>(freedPositions, new IPredicate<ShortPoint2D>() {
					@Override
					public boolean evaluate(ShortPoint2D object) {
						return currArea.contains(object);
					}
				});

				occupyArea(currTower.playerId, area, currTower.areaBorders);
			}
		}
	}

	/**
	 * Occupies the given area for the given playerId.
	 * 
	 * @param playerId
	 * @param filteredInfluencingArea
	 * @param borders
	 */
	private void occupyArea(final byte playerId, Iterable<ShortPoint2D> influencingArea, SRectangle borders) {
		IPredicate<ShortPoint2D> predicate = new IPredicate<ShortPoint2D>() {
			@Override
			public boolean evaluate(ShortPoint2D pos) {
				int index = pos.x + pos.y * width;
				return towers[index] <= 0 && partitionObjects[partitions[index]].playerId != playerId;
			}
		};

		Iterable<ShortPoint2D> filtered = new IteratorFilter<ShortPoint2D>(influencingArea, predicate).toList();

		// create PartitionCalculator
		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(filtered, blockingProvider, borders.xMin, borders.yMin,
				borders.xMax, borders.yMax);
		partitioner.calculatePartitions();

		// take over the positions
		short[] newPartitionsMap = acquirePartitionedArea(playerId, partitioner);

		// check for needed merges
		checkForMergesAndDivides(playerId, partitioner, newPartitionsMap);

		// increase the tower counter
		changeTowerCounter(playerId, influencingArea, +1);

		for (ShortPoint2D curr : filtered) {
			notifyPlayerChangedListener(curr.x, curr.y, playerId);
		}
	}

	private void checkForMergesAndDivides(byte playerId, PartitionCalculatorAlgorithm partitioner, short[] newPartitionsMap) {
		for (int i = PartitionCalculatorAlgorithm.NUMBER_OF_RESERVED_PARTITIONS; i < partitioner.getNumberOfPartitions(); i++) {
			// traverse the border of the partition and collect the partitions around the partition
			PartitionsListingBorderVisitor borderVisitor = new PartitionsListingBorderVisitor(this, blockingProvider);

			ShortPoint2D pos = partitioner.getPartitionBorderPos(i);
			if (blockingProvider.isBlocked(pos.x, pos.y)) {
				continue; // do not check the blocked partition
			}

			final short innerPartition = newPartitionsMap[i];

			BorderTraversingAlgorithm.traverseBorder(new IContainingProvider() {
				@Override
				public boolean contains(int x, int y) {
					return partitionObjects[partitions[x + y * width]] == partitionObjects[innerPartition];
				}
			}, pos, borderVisitor, true);

			// get the partitions around the partition.
			LinkedList<Tuple<Short, ShortPoint2D>> partitionsList = borderVisitor.getPartitionsList();

			checkMergesAndDividesOnPartitionsList(playerId, innerPartition, partitionsList);
		}
	}

	private void checkMergesAndDividesOnPartitionsList(byte playerId, final short innerPartition,
			LinkedList<Tuple<Short, ShortPoint2D>> partitionsList) {
		if (partitionsList.isEmpty()) {
			return; // nothing to do
		}

		// check for divides
		HashMap<Short, ShortPoint2D> foundPartitionsSet = new HashMap<Short, ShortPoint2D>();
		for (Tuple<Short, ShortPoint2D> currPartition : partitionsList) {
			ShortPoint2D existingPartitionPos = foundPartitionsSet.get(currPartition.e1);
			if (existingPartitionPos != null) {
				checkIfDividePartition(currPartition.e1, currPartition.e2, existingPartitionPos);
			} else {
				foundPartitionsSet.put(currPartition.e1, currPartition.e2);
			}
		}

		// check if partitions need to be merged
		partitionsList.addLast(partitionsList.getFirst()); // add first at the end

		for (Tuple<Short, ShortPoint2D> currPartition : partitionsList) {
			Partition currPartitionObject = partitionObjects[currPartition.e1];
			if (currPartitionObject.playerId == playerId && partitionObjects[currPartition.e1] != partitionObjects[innerPartition]) {
				mergePartitions(currPartition.e1, innerPartition);
			}
		}
	}

	/**
	 * Checks if the given partitions is divided and the both given positions are on separated parts of the partition.
	 * 
	 * @param partition
	 * @param pos1
	 * @param pos2
	 */
	private void checkIfDividePartition(Short partition, ShortPoint2D pos1, ShortPoint2D pos2) {
		MutableInt partition1Size = new MutableInt();
		MutableInt partition2Size = new MutableInt();
		if (partition != NO_PLAYER_PARTITION_ID
				&& PartitionsDividedTester.isPartitionDivided(partitionObjects, partitions, width, pos1, partition1Size, pos2,
						partition2Size, partition)) {
			if (partition1Size.value < partition2Size.value) {
				dividePartition(partition, pos1, pos2);
			} else {
				dividePartition(partition, pos2, pos1);
			}
		}
	}

	private short[] acquirePartitionedArea(byte playerId, PartitionCalculatorAlgorithm partitioner) {
		int numberOfNewPartitions = partitioner.getNumberOfPartitions();
		short[] newPartitionsMap = new short[numberOfNewPartitions];
		newPartitionsMap[PartitionCalculatorAlgorithm.BLOCKED_PARTITION] = blockedPartitionsForPlayers[playerId];

		for (int i = PartitionCalculatorAlgorithm.NUMBER_OF_RESERVED_PARTITIONS; i < numberOfNewPartitions; i++) {
			newPartitionsMap[i] = createNewPartition(playerId);
		}

		int minX = partitioner.getMinX();
		int minY = partitioner.getMinY();
		int width = partitioner.getWidth();
		int height = partitioner.getHeight();

		for (short dY = 0; dY < height; dY++) {
			for (int dX = 0; dX < width; dX++) {
				short partition = partitioner.getPartitionAt(dX, dY);

				if (partition != PartitionCalculatorAlgorithm.NO_PARTITION) {
					short x = (short) (dX + minX);
					short y = (short) (dY + minY);

					// Set the new partitions and take over goods and so on
					changePartitionUncheckedAt(x, y, newPartitionsMap[partition]);
				}
			}
		}

		return newPartitionsMap;
	}

	private void changeTowerCounter(final byte playerId, Iterable<ShortPoint2D> influencingArea, int delta) {
		IPredicate<ShortPoint2D> predicate = new IPredicate<ShortPoint2D>() {
			@Override
			public boolean evaluate(ShortPoint2D pos) {
				return partitionObjects[partitions[pos.x + pos.y * width]].playerId == playerId;
			}
		};

		IteratorFilter<ShortPoint2D> filtered = new IteratorFilter<ShortPoint2D>(influencingArea, predicate);

		for (ShortPoint2D pos : filtered) {
			towers[pos.x + pos.y * width] += delta;
		}
	}

	/**
	 * Merges two partitions. The smaller partition is merged into the bigger one.
	 * 
	 * @param partition1
	 * @param partition2
	 * @return The resulting partition
	 */
	short mergePartitions(short partition1, short partition2) {
		short biggerPartition;
		short smallerPartition;

		if (partitionObjects[partition1].getNumberOfElements() >= partitionObjects[partition2].getNumberOfElements()) {
			biggerPartition = partition1;
			smallerPartition = partition2;
		} else {
			biggerPartition = partition2;
			smallerPartition = partition1;
		}
		biggerPartition = partitionObjects[biggerPartition].partitionId; // ensure that we have the top representative
		smallerPartition = partitionObjects[smallerPartition].partitionId;

		assert biggerPartition != smallerPartition : "the partitions can not be the same!";

		// System.out.println("merging partitions: " + biggerPartition + " and " + smallerPartition);

		Partition biggerPartitionObject = partitionObjects[biggerPartition];
		Partition smallerPartitionObject = partitionObjects[smallerPartition];

		if (biggerPartitionObject.playerId != smallerPartitionObject.playerId) {
			System.err.println("ERROR: Merging partitions of different players!!!");
		} else if (biggerPartition == blockedPartitionsForPlayers[biggerPartitionObject.playerId]
				|| smallerPartition == blockedPartitionsForPlayers[smallerPartitionObject.playerId]) {
			System.err.println("ERROR: Merging blocked partition!!!");
		}

		smallerPartitionObject.mergeInto(biggerPartitionObject);
		smallerPartitionObject.stopManager();

		partitionObjects[smallerPartition] = biggerPartitionObject;

		/**
		 * Flatten all hierarchies: <br>
		 * start situation: 1 <- 2 and 3 <- 4 <br>
		 * merge of 4 and 2 leads to a merge of 1 and 3. Say 1 is the resulting partition. Then we get: <br>
		 * 1 <- 2, 1 <- 3 <- 4 <br>
		 * The chain 1 <- 3 <- 4 will be cut to 1 <- 3 and 1 <- 4 by the following code. <br>
		 */
		int numberOfPartitions = partitionObjects.length;
		for (int i = 1; i < numberOfPartitions; i++) {
			if (partitionObjects[i] == smallerPartitionObject) {
				partitionObjects[i] = biggerPartitionObject;
			}
		}

		return biggerPartition;
	}

	/**
	 * Divides the given partition. The both positions must be on the border of the given partition and be on the parts that are now distinct and
	 * shall be divided. NOTE: There will be no check if the partition is really divided!
	 * 
	 * @param oldPartition
	 *            The original partition that now needs to be divided.
	 * @param relabelStartPosition
	 *            A position in the part of the divided partition that will be relabeled.
	 * @param otherPosition
	 *            A position in the part of the divided partition that will keep the old partition id.
	 */
	private void dividePartition(final short oldPartition, ShortPoint2D relabelStartPosition, ShortPoint2D otherPosition) {
		if (oldPartition == NO_PLAYER_PARTITION_ID) {
			return; // don't divide the no player partition
		}

		Partition partitionObject = partitionObjects[oldPartition];

		System.out.println("Dividing partition " + oldPartition + " with relabelStartPos: " + relabelStartPosition + " and "
				+ partitionObject.getNumberOfElements() + " elements. " + otherPosition + " will keep the old partition id.");

		short newPartition = createNewPartition(partitionObject.playerId);

		relabelArea(oldPartition, relabelStartPosition, newPartition);
	}

	/**
	 * Relabels all of the given old partition connected to the start position to the new partition.
	 * 
	 * @param oldPartition
	 *            The id of the old partition.
	 * @param relabelStartPos
	 *            The start position of the relabeling. Only positions connected to this position by other positions of the old partition will be
	 *            relabeled.
	 * @param newPartition
	 *            The id of the new partition.
	 */
	private void relabelArea(final short oldPartition, ShortPoint2D relabelStartPos, final short newPartition) {
		// relabel the partition
		IContainingProvider containingProvider = new IContainingProvider() {
			@Override
			public boolean contains(int x, int y) {
				return partitionObjects[partitions[x + y * width]].partitionId == oldPartition;
			}
		};

		ITraversingVisitor relabelAreaVisitor = new ITraversingVisitor() {
			@Override
			public boolean visit(int x, int y) {
				changePartitionUncheckedAt(x, y, newPartition);
				return true;
			}
		};
		AreaTraversingAlgorithm.traverseArea(containingProvider, relabelAreaVisitor, relabelStartPos, width, height);
	}

	/**
	 * Changes the partition at the given position to the given new partition. <br>
	 * NOTE: There will be no checks if the new partition exists or if this change divides an other partition or should lead to a merge.
	 * 
	 * @param x
	 *            x coordinate of the position.
	 * @param y
	 *            y coordinate of the position.
	 * @param newPartition
	 *            The new partition that will be set at the given partition.
	 * @return the player id of the new partition.
	 */
	byte changePartitionUncheckedAt(int x, int y, short newPartition) {
		int idx = x + y * width;
		Partition oldPartitionObject = partitionObjects[partitions[idx]];
		Partition newPartitionObject = partitionObjects[newPartition];

		oldPartitionObject.removePositionTo(x, y, newPartitionObject);
		synchronized (partitionsWriteLock) {
			partitions[idx] = newPartition;
		}

		return newPartitionObject.playerId;
	}

	private void notifyPlayerChangedListener(int x, int y, byte newPlayer) {
		playerChangedListener.playerChangedAt(x, y, newPlayer);
	}

	short createNewPartition(byte player) { // package private for tests
		checkNormalizePartitions(NUMBER_OF_START_PARTITION_OBJECTS / 2);

		short newPartitionId = 1;

		while (partitionObjects[newPartitionId] != null) { // get a free partition
			newPartitionId++;
			int length = partitionObjects.length;

			if (newPartitionId >= length) {
				synchronized (partitionsWriteLock) {
					int newLength = (int) (length * PARTITIONS_EXPAND_FACTOR);
					Partition[] newPartitionObjects = new Partition[newLength];

					System.arraycopy(partitionObjects, 0, newPartitionObjects, 0, length);
					partitionObjects = newPartitionObjects;

					System.out.println("PartitionsGrid: Expanded the number of possible partitions from " + length + " to " + newLength);
				}
			}
		}

		Partition newPartitionObject = new Partition(newPartitionId, player);
		newPartitionObject.startManager();
		partitionObjects[newPartitionId] = newPartitionObject;

		return newPartitionId;
	}

	public void setPartitionAt(int x, int y, short newPartition) {
		if (getPartitionAt(x, y) != partitionObjects[newPartition]) {
			byte playerId = changePartitionUncheckedAt(x, y, newPartition);
			notifyPlayerChangedListener(x, y, playerId);
		}
	}

	@Override
	public void blockingChanged(int x, int y, boolean newBlockingValue) {
		if (newBlockingValue) {// if the value changed to blocking, ignore it
			return;
		}

		int idx = x + y * width;
		short currPartition = partitions[idx];

		// if the position has a player
		if (currPartition != NO_PLAYER_PARTITION_ID) {
			// create a new partition for the given position and add the position to the partition
			byte playerId = partitionObjects[currPartition].playerId;
			short newPartition = createNewPartition(playerId);
			changePartitionUncheckedAt(x, y, newPartition);

			// check all neighbors for merges
			for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
				int currNeighborX = x + EDirection.values[i].gridDeltaX;
				int currNeighborY = y + EDirection.values[i].gridDeltaY;

				// if the neighbor is not blocked (if it is blocked, we can not merge)
				if (!blockingProvider.isBlocked(currNeighborX, currNeighborY)) {

					short neighborPartition = partitions[currNeighborX + currNeighborY * width];
					byte neighborPlayer = partitionObjects[neighborPartition].playerId;

					// if the position and the neighbor are from the same player, then merge the partitions
					if (playerId == neighborPlayer && partitionObjects[newPartition] != partitionObjects[neighborPartition]) {
						mergePartitions(newPartition, neighborPartition);
					}
				}
			}
		}
	}

	/**
	 * Sets the given listener. The listener will then be informed of any positions that change their player.
	 * 
	 * @param listener
	 *            The listener to be set or null if no listener should be set.
	 */
	public void setPlayerChangedListener(IPlayerChangedListener listener) {
		if (listener == null) {
			this.playerChangedListener = IPlayerChangedListener.DEFAULT_IMPLEMENTATION;
		} else {
			this.playerChangedListener = listener;
		}
	}

	public byte getNumberOfPlayers() {
		return (byte) players.length;
	}

	private int checkNormalizePartitions(int mergePartitionsThreshold) {
		int maxPartitions = this.partitionObjects.length;
		BitSet stoppedManagers = new BitSet(maxPartitions);

		int counter = 0;

		for (int i = 1; i < maxPartitions; i++) {
			PartitionManager partitionObject = this.partitionObjects[i];

			if (partitionObject != null && this.partitionObjects[i].partitionId != i) {
				stoppedManagers.set(i);
				counter++;
			}
		}

		if (counter <= mergePartitionsThreshold) {
			return 0;// skip the rest if nothing is to do.
		}

		// normalize the partitions
		for (int y = 0; y < height; y++) {
			synchronized (partitionsWriteLock) { // the lock is acquired here to prevent holding it for a long time without requesting it every time
				for (int x = 0; x < width; x++) {
					int idx = x + y * width;
					this.partitions[idx] = this.partitionObjects[this.partitions[idx]].partitionId;
				}
			}
		}

		// clear the partition objects
		synchronized (partitionsWriteLock) {
			for (int i = 1; i < maxPartitions; i++) {
				if (stoppedManagers.get(i)) {
					this.partitionObjects[i] = null;
				}
			}
		}

		return counter;
	}

	public IPartitionData getPartitionDataForManagerAt(int x, int y) {
		Partition partition = getPartitionAt(x, y);
		return new PartitionDataSupplier(partition.getPlayerId(), partition.partitionId, partition.getPartitionSettings(),
				partition.getMaterialCounts());
	}

	public void setMaterialDistributionSettings(ShortPoint2D managerPosition, EMaterialType materialType, float[] probabilities) {
		getPartitionAt(managerPosition.x, managerPosition.y).setMaterialDistributionSettings(materialType, probabilities);
	}

	public void setMaterialPrioritiesSettings(ShortPoint2D managerPosition, EMaterialType[] materialTypeForPriority) {
		getPartitionAt(managerPosition.x, managerPosition.y).setMaterialPrioritiesSettings(materialTypeForPriority);
	}

	public Team[] getTeams() {
		return teams;
	}
}
