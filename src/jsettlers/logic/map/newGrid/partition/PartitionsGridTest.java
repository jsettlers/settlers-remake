package jsettlers.logic.map.newGrid.partition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.partitions.IBlockingProvider;
import jsettlers.logic.map.newGrid.partition.manager.objects.MaterialOffer;

import org.junit.Test;

public class PartitionsGridTest {

	private static final short WIDTH = 200;
	private static final short HEIGHT = 200;

	private final PartitionsGrid grid = new PartitionsGrid(WIDTH, HEIGHT, (byte) 10, IBlockingProvider.DEFAULT_PROVIDER);

	@Test
	public void testMergeNoArea() {
		short partition1 = grid.createNewPartition((byte) 1);
		short partition2 = grid.createNewPartition((byte) 1);

		grid.mergePartitions(partition1, partition2);
	}

	@Test
	public void testMergeWithArea() {
		short partition1 = grid.createNewPartition((byte) 1);
		short partition2 = grid.createNewPartition((byte) 1);

		setPartitionInCircle(partition1, 75, 75, 30);
		setPartitionInCircle(partition2, 125, 125, 30);

		grid.mergePartitions(partition1, partition2);
	}

	@Test
	public void testMergeAndRepresentatives() {
		short partition1 = grid.createNewPartition((byte) 1);
		short partition2 = grid.createNewPartition((byte) 1);
		short partition3 = grid.createNewPartition((byte) 1);
		short partition4 = grid.createNewPartition((byte) 1);
		short partition5 = grid.createNewPartition((byte) 1);

		grid.mergePartitions(partition1, partition2);
		grid.mergePartitions(partition3, partition4);
		grid.mergePartitions(partition1, partition4);
		grid.mergePartitions(partition4, partition5);

		for (int i = 1; i < 5; i++) { // all partitions are merged and should have the same representative
			assertEquals(grid.partitionRepresentative[i], grid.partitionRepresentative[i + 1]);
		}
	}

	@Test
	public void testMergeWithAreaAndGoods() {
		short partition1 = grid.createNewPartition((byte) 1);
		short partition2 = grid.createNewPartition((byte) 1);

		ShortPoint2D materialTestPos = new ShortPoint2D(75, 80);

		Partition noPlayerPartition = grid.getPartitionAt(materialTestPos.x, materialTestPos.y);
		assertEquals(WIDTH * HEIGHT, noPlayerPartition.getNumberOfElements()); // test if no player partition has all positions

		// assert no material at the test position
		assertNull(noPlayerPartition.getMaterialOfferAt(materialTestPos));
		// put material at the test position
		noPlayerPartition.addOffer(materialTestPos, EMaterialType.STONE);
		noPlayerPartition.addOffer(materialTestPos, EMaterialType.STONE);
		// assert the material is at the test position
		assertOfferAt(materialTestPos, EMaterialType.STONE, 2);

		// occupy the areas for the both new partitions
		int positionsPartition1 = setPartitionInCircle(partition1, 75, 75, 30);
		assertEquals(positionsPartition1, grid.getPartitionAt((short) 75, (short) 75).getNumberOfElements());

		assertEquals(1, grid.getPartitionAt(materialTestPos.x, materialTestPos.y).getPlayerId());
		assertOfferAt(materialTestPos, EMaterialType.STONE, 2); // assert the offer switch to partition1
		assertNull(noPlayerPartition.getMaterialOfferAt(materialTestPos)); // assert the no player partition removed the offer

		int positionsPartition2 = setPartitionInCircle(partition2, 125, 125, 30);
		assertEquals(positionsPartition2, grid.getPartitionAt((short) 125, (short) 125).getNumberOfElements());

		// material position must be in partition1
		assertEquals(partition1, grid.getPartitionIdAt(materialTestPos.x, materialTestPos.y));

		short mergePartition = grid.mergePartitions(partition1, partition2);

		assertEquals(mergePartition, grid.getPartitionIdAt(materialTestPos.x, materialTestPos.y));
		assertOfferAt(materialTestPos, EMaterialType.STONE, 2);
		assertEquals(mergePartition, grid.getPartitionIdAt((short) 75, (short) 75));
		assertEquals(mergePartition, grid.getPartitionIdAt((short) 125, (short) 125));
	}

	@Test
	public void testRemoveTower() {
		addTower(0, 100, 100, 40);
		assertEquals(0, grid.getPlayerIdAt(140, 100));
		assertEquals(1, grid.getTowerCountAt(140, 100));

		addTower(1, 150, 100, 40);
		addTower(1, 155, 110, 40);

		assertEquals(0, grid.getPlayerIdAt(140, 100));
		assertEquals(1, grid.getTowerCountAt(140, 100));
		assertEquals(2, grid.getTowerCountAt(150, 105));

		removeTower(100, 100);

		assertEquals(1, grid.getPlayerIdAt(140, 100));
		assertEquals(2, grid.getTowerCountAt(140, 100));
	}

	@Test
	public void testMergePartition() {
		addTower(0, 50, 100, 40);
		addTower(0, 150, 100, 40);
		// check if the partitions are not connected
		assertFalse(grid.getPartitionIdAt(50, 100) == grid.getPartitionIdAt(150, 100));

		addTower(0, 100, 100, 40);
		// no the partitions should be connected
		assertEquals(grid.getPartitionIdAt(50, 100), grid.getPartitionIdAt(150, 100));
		assertEquals(grid.getPartitionIdAt(100, 100), grid.getPartitionIdAt(150, 100));
	}

	@Test
	public void testDividePartitionsByNewTower() {
		addTower(0, 50, 100, 40);
		addTower(0, 150, 100, 40);
		// check if the partitions are not connected
		assertTrue(grid.getPartitionIdAt(50, 100) != grid.getPartitionIdAt(150, 100));

		addTower(0, 100, 100, 40);
		// no the partitions should be connected
		assertEquals(grid.getPartitionIdAt(50, 100), grid.getPartitionIdAt(150, 100));
		assertEquals(grid.getPartitionIdAt(100, 100), grid.getPartitionIdAt(150, 100));

		removeTower(100, 100);
		// partitions still should be connected
		assertEquals(grid.getPartitionIdAt(50, 100), grid.getPartitionIdAt(150, 100));

		addTower(1, 100, 100, 40);
		// now the partitions should be divided
		assertTrue(grid.getPartitionIdAt(50, 100) != grid.getPartitionIdAt(150, 100));
		assertCircleIs(getTowerCircle(50, 100, 40), grid.getPartitionIdAt(50, 100));
	}

	@Test
	public void testDividePartitionsByRemovingTower() {
		addTower(0, 50, 100, 40);
		// add two stones
		Partition partition = grid.getPartitionAt(50, 100);
		ShortPoint2D materialPos = new ShortPoint2D(75, 100);
		partition.addOffer(materialPos, EMaterialType.STONE);
		partition.addOffer(materialPos, EMaterialType.STONE);

		addTower(0, 150, 100, 40);
		// check if the partitions are not connected
		assertTrue(grid.getPartitionIdAt(50, 100) != grid.getPartitionIdAt(150, 100));

		addTower(0, 100, 100, 40);
		// no the partitions should be connected
		assertEquals(grid.getPartitionIdAt(50, 100), grid.getPartitionIdAt(150, 100));
		assertEquals(grid.getPartitionIdAt(100, 100), grid.getPartitionIdAt(150, 100));

		addTower(1, 75, 55, 42);
		addTower(1, 125, 150, 42);
		// partitions still should be connected
		assertEquals(grid.getPartitionIdAt(50, 100), grid.getPartitionIdAt(150, 100));

		removeTower(100, 100);
		// now the partitions should be divided
		assertTrue(grid.getPartitionIdAt(50, 100) != grid.getPartitionIdAt(150, 100));
		assertCircleIs(getTowerCircle(50, 100, 40), grid.getPartitionIdAt(50, 100));
		assertCircleIs(getTowerCircle(150, 100, 40), grid.getPartitionIdAt(150, 100));
		assertEquals(grid.getPartitionIdAt(75, 55), grid.getPartitionIdAt(125, 150));

		assertOfferAt(materialPos, EMaterialType.STONE, 2);
	}

	@Test
	public void testTakeOverCloseTower() {
		addTower(0, 50, 100, 40);
		addTower(0, 70, 100, 40);

		assertEquals(2, grid.getTowerCountAt(50, 100));
		assertEquals(2, grid.getTowerCountAt(70, 100));

		short oldPartitionId = grid.getPartitionIdAt(70, 100);
		assertEquals(oldPartitionId, grid.getPartitionIdAt(50, 100));

		changePlayerOfTower(50, 100, 1);

		assertEquals(oldPartitionId, grid.getPartitionIdAt(70, 100));
		assertTrue(oldPartitionId != grid.getPartitionIdAt(50, 100));
	}

	private void changePlayerOfTower(int x, int y, int newPlayer) {
		ShortPoint2D pos = new ShortPoint2D(x, y);
		grid.changePlayerOfTower(pos, (byte) newPlayer, new FreeMapArea(pos, EBuildingType.TOWER.getProtectedTiles()));
	}

	private void assertCircleIs(MapCircle circle, short partition) {
		for (ShortPoint2D pos : circle) {
			assertEquals(partition, grid.getPartitionIdAt(pos.x, pos.y));
		}
	}

	private void removeTower(int x, int y) {
		grid.removeTowerAndFreeOccupiedArea(new ShortPoint2D(x, y));
	}

	private void addTower(int playerId, int x, int y, int radius) {
		grid.addTowerAndOccupyArea((byte) playerId, getTowerCircle(x, y, radius));
	}

	private MapCircle getTowerCircle(int x, int y, int radius) {
		return new MapCircle(new ShortPoint2D(x, y), radius);
	}

	private void assertOfferAt(ShortPoint2D materialTestPos, EMaterialType material, int amount) {
		MaterialOffer offer = grid.getPartitionAt(materialTestPos.x, materialTestPos.y).getMaterialOfferAt(materialTestPos);
		assertNotNull(offer);
		assertEquals(amount, offer.amount);
		assertEquals(material, offer.materialType);
	}

	private int setPartitionInCircle(short partition, int x, int y, float radius) {
		MapCircle circle = new MapCircle(new ShortPoint2D(x, y), radius);
		int positions = 0;
		for (ShortPoint2D curr : circle) {
			grid.changePartitionUncheckedAt(curr.x, curr.y, partition);
			positions++;
		}

		return positions;
	}

}
