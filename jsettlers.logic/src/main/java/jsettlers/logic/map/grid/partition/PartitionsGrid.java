/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java8.util.Lists;
import java8.util.Maps;
import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.algorithms.traversing.area.IAreaVisitor;
import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.common.utils.mutables.MutableInt;
import jsettlers.logic.map.grid.partition.manager.settings.MaterialProductionSettings;
import jsettlers.logic.map.grid.partition.PartitionsListingBorderVisitor.BorderPartitionInfo;
import jsettlers.logic.map.grid.partition.manager.PartitionManager;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.logic.player.Team;

import static java8.util.stream.StreamSupport.stream;

/**
 * This class handles the partitions of the map.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionsGrid implements Serializable {
	private static final long serialVersionUID = 8919380724171427679L;

	private static final int NUMBER_OF_START_PARTITION_OBJECTS = 3000;
	private static final float PARTITIONS_EXPAND_FACTOR = 1.5f;

	private static final short NO_PLAYER_PARTITION_ID = 0;

	private final PartitionOccupyingTowerList occupyingTowers = new PartitionOccupyingTowerList();

	final short width;
	final short height;
	private final Player[] players;
	private final IBlockingProvider blockingProvider;

	final short[] partitions;
	private final byte[] towers;

	private final short[] blockedPartitionsForPlayers;
	Partition[] partitionObjects = new Partition[NUMBER_OF_START_PARTITION_OBJECTS];

	private transient IPlayerChangedListener playerChangedListener = IPlayerChangedListener.DEFAULT_IMPLEMENTATION;

	public PartitionsGrid(short width, short height, PlayerSetting[] playerSettings, IBlockingProvider blockingProvider) {
		this.width = width;
		this.height = height;
		this.blockingProvider = blockingProvider;

		this.players = new Player[playerSettings.length]; // create the players.
		this.blockedPartitionsForPlayers = new short[playerSettings.length];

		Map<Byte, Team> teams = new HashMap<>();
		for (byte playerId = 0; playerId < playerSettings.length; playerId++) {
			PlayerSetting playerSetting = playerSettings[(int) playerId];
			if (playerSetting.isAvailable()) {
				Maps.computeIfAbsent(teams, playerSetting.getTeamId(), Team::new);
				Team team = teams.get(playerSetting.getTeamId());
				this.players[playerId] = new Player(playerId, team, (byte) playerSettings.length, playerSetting.getPlayerType(), playerSetting.getCivilisation());
				team.registerPlayer(this.players[playerId]);
				this.blockedPartitionsForPlayers[playerId] = createNewPartition(playerId); // create a blocked partition for every player
			}
		}

		this.partitions = new short[width * height];
		this.towers = new byte[width * height];

		// the no player partition (the manager won't be started)
		this.partitionObjects[NO_PLAYER_PARTITION_ID] = new Partition(NO_PLAYER_PARTITION_ID, (byte) -1, width * height);
	}

	public void initWithPlayerSettings(PlayerSetting[] playerSettings) {
		for (int i = 0; i < players.length; i++) {
			if (players[i] != null && playerSettings[i].isAvailable()) {
				players[i].setPlayerType(playerSettings[i].getPlayerType());
				players[i].setCivilisation(playerSettings[i].getCivilisation());
			}
		}
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

	public MaterialProductionSettings getMaterialProductionAt(int x, int y) {
		return getPartitionAt(x, y).getMaterialProduction();
	}

	public PartitionManager getPartitionAt(ILocatable locatable) {
		ShortPoint2D pos = locatable.getPosition();
		return getPartitionAt(pos.x, pos.y);
	}

	public byte getPlayerIdAt(int x, int y) {
		return getPartitionAt(x, y).playerId;
	}

	public Player[] getPlayers() {
		return players;
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
	 * @param groundArea
	 *            The ground area of the tower.
	 */
	public void addTowerAndOccupyArea(byte playerId, MapCircle influencingArea, FreeMapArea groundArea) {
		IMapArea filteredArea = new MapShapeFilter(influencingArea, width, height);

		PartitionOccupyingTower tower = new PartitionOccupyingTower(playerId, influencingArea.getCenter(), groundArea, filteredArea,
				influencingArea.getBorders(), (int) influencingArea.getRadius());

		occupyAreaOfTower(tower);
	}

	/**
	 * Removes the tower at the given position from the grid.
	 * 
	 * @param pos
	 *            The position of the tower.
	  */
	public void removeTowerAndFreeOccupiedArea(ShortPoint2D pos) {
		// get the tower object and the informations of it.
		PartitionOccupyingTower tower = occupyingTowers.removeAt(pos);
		if (tower == null) {
			return ;
		}

		// reduce the tower counter
		changeTowerCounter(tower.playerId, tower.area.stream(), -1);
		checkOtherTowersInArea(tower);
	}

	/**
	 * Changes the player of the tower at given position to the new player. After this operation, the given ground area will always be occupied by the new player.
	 * 
	 * @param towerPosition
	 * @param newPlayerId
	 * @return
	 */
	public CoordinateStream changePlayerOfTower(ShortPoint2D towerPosition, byte newPlayerId) {
		// get the tower object and the information of it.
		PartitionOccupyingTower tower = occupyingTowers.removeAt(towerPosition);
		if (tower == null) {
			return CoordinateStream.EMPTY; // return if no tower has been found
		}

		// reduce the tower counter
		changeTowerCounter(tower.playerId, tower.getAreaWithoutGround(), -1);

		// let the other towers occupy the area
		checkOtherTowersInArea(tower);

		PartitionOccupyingTower newTower = new PartitionOccupyingTower(newPlayerId, tower);
		occupyAreaOfTower(newTower);
		return newTower.area.stream();
	}

	private void occupyAreaOfTower(PartitionOccupyingTower tower) {
		// set the tower counter of the groundArea to 0 => the ground area will be occupied
		tower.groundArea.stream().forEach((x, y) -> towers[x + y * width] = 0);

		// occupy the area for the new player
		occupyAreaByTower(tower.playerId, tower.area.stream(), tower.areaBorders);
		occupyingTowers.add(tower);

		// recalculate the tower counter for the ground area
		recalculateTowerCounter(tower, tower.groundArea);
	}

	public void changePlayerAt(ShortPoint2D position, byte playerId) {
		changePlayerAt(position.x, position.y, playerId);
	}

	public void changePlayerAt(int x, int y, byte playerId) {
		int idx = x + y * width;
		if (towers[idx] <= 0) {
			short newPartition = createNewPartition(playerId);
			changePartitionUncheckedAt(x, y, newPartition);
			notifyPlayerChangedListener(x, y, playerId);

			PartitionsListingBorderVisitor borderVisitor = new PartitionsListingBorderVisitor(this, blockingProvider);
			// visit the direct neighbors of the position
			for (EDirection currDir : EDirection.VALUES) {
				borderVisitor.visit(x, y, currDir.gridDeltaX + x, currDir.gridDeltaY + y);
			}

			checkMergesAndDividesOnPartitionsList(playerId, newPartition, borderVisitor.getPartitionsList());
		}
	}

	/**
	 * Recalculates the tower counter for the given area. <br>
	 * NOTE: The given area must completely belong to the given player!
	 *
	 * @param tower
	 * @param tower
	 * @param area
	 */
	private void recalculateTowerCounter(PartitionOccupyingTower tower, IMapArea area) {
		area.stream().forEach((x, y) -> towers[x + y * width] = 0);

		List<Tuple<Integer, PartitionOccupyingTower>> towersInRange = occupyingTowers.getTowersInRange(tower.position, tower.radius, currTower -> currTower.playerId == tower.playerId);
		stream(towersInRange)
				.forEach(currTower -> area.stream()
						.filter(currTower.e2.area::contains)
						.forEach((x, y) -> towers[x + y * width]++));
	}

	/**
	 * Checks if other towers that intersect the area of the given tower can occupy free positions of the area of the given tower and lets them do so.
	 * 
	 * @param tower
	 */
	private void checkOtherTowersInArea(PartitionOccupyingTower tower) {
		// Get the positions that may change their owner (tower counter <= 0)
		// Save these positions in the list because the list must not change during the loop over the other towers
		CoordinateStream freedPositions = tower.area.stream().filter((x, y) -> towers[x + y * width] <= 0).freeze();

		// if at least one position may change the player
		// check if other towers occupy the area
		if (!freedPositions.isEmpty()) {
			List<Tuple<Integer, PartitionOccupyingTower>> towersInRange = occupyingTowers.getTowersInRange(tower.position,
					tower.radius, currTower -> currTower.playerId != tower.playerId);

			// sort the towers by their distance to the removed tower
			Lists.sort(towersInRange, Tuple.getE1Comparator());

			for (Tuple<Integer, PartitionOccupyingTower> curr : towersInRange) {
				final PartitionOccupyingTower currTower = curr.e2;
				final IMapArea currArea = currTower.area;

				CoordinateStream area = freedPositions.filter(currArea::contains);
				occupyAreaByTower(currTower.playerId, area, currTower.areaBorders);

				PartitionsListingBorderVisitor borderVisitor = new PartitionsListingBorderVisitor(this, blockingProvider);
				final FreeMapArea groundArea = currTower.groundArea;
				ShortPoint2D upperLeftGroundAreaPosition = groundArea.getUpperLeftPosition();

				BorderTraversingAlgorithm.traverseBorder(groundArea::contains, upperLeftGroundAreaPosition, borderVisitor, true);
				checkMergesAndDividesOnPartitionsList(currTower.playerId,
						getPartitionIdAt(upperLeftGroundAreaPosition.x, upperLeftGroundAreaPosition.y), borderVisitor.getPartitionsList());
			}
		}
	}

	/**
	 * Occupies the given area for the given playerId.
	 * 
	 * @param playerId
	 * @param influencingArea
	 * @param borders
	 */
	private void occupyAreaByTower(final byte playerId, CoordinateStream influencingArea, SRectangle borders) {
		CoordinateStream filtered = influencingArea.filter((x, y) -> {
			int index = x + y * width;
			return towers[index] <= 0 && partitionObjects[partitions[index]].playerId != playerId;
		}).freeze();

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

		filtered.forEach((x, y) -> notifyPlayerChangedListener(x, y, playerId));
	}

	private void checkForMergesAndDivides(byte playerId, PartitionCalculatorAlgorithm partitioner, short[] newPartitionsMap) {
		for (int i = PartitionCalculatorAlgorithm.NUMBER_OF_RESERVED_PARTITIONS; i < partitioner.getNumberOfPartitions(); i++) {
			// traverse the border of the partition and collect the partitions around the partition
			PartitionsListingBorderVisitor borderVisitor = new PartitionsListingBorderVisitor(this, blockingProvider);

			ShortPoint2D pos = partitioner.getPartitionBorderPos(i);
			short innerPartition = newPartitionsMap[i];

			BorderTraversingAlgorithm.traverseBorder((x, y) -> partitionObjects[partitions[x + y * width]] == partitionObjects[innerPartition], pos, borderVisitor, true);

			checkMergesAndDividesOnPartitionsList(playerId, innerPartition, borderVisitor.getPartitionsList());
		}
	}

	private void checkMergesAndDividesOnPartitionsList(byte playerId, final short innerPartition, LinkedList<BorderPartitionInfo> partitionsList) {
		if (partitionsList.isEmpty()) {
			return; // nothing to do
		}

		// check for divides
		HashMap<Short, BorderPartitionInfo> foundPartitionsSet = new HashMap<>();
		for (BorderPartitionInfo currPartitionInfo : partitionsList) {
			Short currPartitionId = currPartitionInfo.partitionId;
			BorderPartitionInfo existingPartitionInfo = foundPartitionsSet.get(currPartitionId);
			if (existingPartitionInfo != null) {
				if (partitionObjects[currPartitionId].playerId != playerId) { // the player cannot divide its own partitions => only check other player's positions
					checkIfDividePartition(currPartitionInfo, existingPartitionInfo);

					// if the entry of the set changed its partition, replace that entry with the one of the old partition. Further divides can only
					// happen with partitions which also have currPartitionId.
					short newPartitionIdOfExistingPartition = getPartitionIdAt(existingPartitionInfo.positionOfPartition.x,
							existingPartitionInfo.positionOfPartition.y);
					if (newPartitionIdOfExistingPartition != currPartitionId) {
						foundPartitionsSet.put(currPartitionId, currPartitionInfo);
					}
				}
			} else {
				foundPartitionsSet.put(currPartitionId, currPartitionInfo);
			}
		}

		// check if partitions need to be merged
		partitionsList.addLast(partitionsList.getFirst()); // add first at the end

		for (BorderPartitionInfo currPartition : partitionsList) {
			Partition currPartitionObject = partitionObjects[currPartition.partitionId];
			if (currPartitionObject.playerId == playerId && partitionObjects[currPartition.partitionId] != partitionObjects[innerPartition]) {
				mergePartitions(currPartition.partitionId, innerPartition);
			}
		}
	}

	/**
	 * Checks if the given partitions is divided and the both given positions are on separated parts of the partition.
	 * 
	 * @param partitionInfo1
	 * @param partitionInfo2
	 */
	private void checkIfDividePartition(BorderPartitionInfo partitionInfo1, BorderPartitionInfo partitionInfo2) {
		assert partitionInfo1.partitionId == partitionInfo2.partitionId;

		final short partition = partitionInfo1.partitionId;

		MutableInt partition1Size = new MutableInt();
		MutableInt partition2Size = new MutableInt();

		if (partition != NO_PLAYER_PARTITION_ID
				&& PartitionsDividedTester.isPartitionDivided(partitionObjects, partitions, width, partitionInfo1, partition1Size, partitionInfo2, partition2Size)) {
			if (partition1Size.value < partition2Size.value) {
				dividePartition(partition, partitionInfo1.positionOfPartition, partitionInfo2.positionOfPartition);
			} else {
				dividePartition(partition, partitionInfo2.positionOfPartition, partitionInfo1.positionOfPartition);
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

	private void changeTowerCounter(final byte playerId, CoordinateStream influencingArea, int delta) {
		influencingArea
				.filter((x, y) -> partitionObjects[partitions[x + y * width]].playerId == playerId)
				.forEach((x, y) -> towers[x + y * width] += delta);
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
	 * Divides the given partition. The both positions must be on the border of the given partition and be on the parts that are now distinct and shall be divided. NOTE: There will be no check if the
	 * partition is really divided!
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
	 *            The start position of the relabeling. Only positions connected to this position by other positions of the old partition will be relabeled.
	 * @param newPartition
	 *            The id of the new partition.
	 */
	private void relabelArea(final short oldPartition, ShortPoint2D relabelStartPos, final short newPartition) {
		// relabel the partition
		IContainingProvider containingProvider = (x, y) -> partitionObjects[partitions[x + y * width]].partitionId == oldPartition;

		IAreaVisitor relabelAreaVisitor = (x, y) -> {
			changePartitionUncheckedAt(x, y, newPartition);
			return true;
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
		synchronized (this) {
			partitions[idx] = newPartition;
		}

		return newPartitionObject.playerId;
	}

	private void notifyPlayerChangedListener(int x, int y, byte newPlayer) {
		playerChangedListener.playerChangedAt(x, y, newPlayer);
	}

	short createNewPartition(byte playerId) { // package private for tests
		checkNormalizePartitions(NUMBER_OF_START_PARTITION_OBJECTS / 2);

		short newPartitionId = 1;

		while (partitionObjects[newPartitionId] != null) { // get a free partition
			newPartitionId++;
			int length = partitionObjects.length;

			if (newPartitionId >= length) {
				synchronized (this) {
					int newLength = (int) (length * PARTITIONS_EXPAND_FACTOR);
					Partition[] newPartitionObjects = new Partition[newLength];

					System.arraycopy(partitionObjects, 0, newPartitionObjects, 0, length);
					partitionObjects = newPartitionObjects;

					System.out.println("PartitionsGrid: Expanded the number of possible partitions from " + length + " to " + newLength);
				}
			}
		}

		Partition newPartitionObject = new Partition(newPartitionId, playerId, players[playerId]);
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
			synchronized (this) { // the lock is acquired here to prevent holding it for a long time without requesting it every time
				for (int x = 0; x < width; x++) {
					int idx = x + y * width;
					this.partitions[idx] = this.partitionObjects[this.partitions[idx]].partitionId;
				}
			}
		}

		// clear the partition objects
		synchronized (this) {
			for (int i = 1; i < maxPartitions; i++) {
				if (stoppedManagers.get(i)) {
					this.partitionObjects[i] = null;
				}
			}
		}

		return counter;
	}

	public IPartitionData getPartitionDataForManagerAt(int x, int y) {
		return getPartitionAt(x, y).getPartitionData();
	}

	public PartitionManagerSettings getPartitionSettings(ShortPoint2D position) {
		return getPartitionSettings(position.x, position.y);
	}

	public PartitionManagerSettings getPartitionSettings(int x, int y) {
		return getPartitionAt(x, y).getPartitionSettings();
	}
}
