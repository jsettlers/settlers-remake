package jsettlers.logic.map.newGrid.partition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.Tuple;

/**
 * A data structure to store the towers that occupy areas on the {@link PartitionsGrid}.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionOccupyingTowerList extends LinkedList<PartitionOccupyingTower> {
	private static final long serialVersionUID = -2459360464464831879L;

	/**
	 * Returns the {@link PartitionOccupyingTower} object at the given position if it exists or null.
	 * 
	 * @param position
	 *            The position the expected {@link PartitionOccupyingTower} is located.
	 * @return
	 * 
	 */
	public PartitionOccupyingTower removeAt(ShortPoint2D position) {
		Iterator<PartitionOccupyingTower> iter = iterator();
		while (iter.hasNext()) {
			PartitionOccupyingTower curr = iter.next();
			if (curr.position.equals(position)) {
				iter.remove();
				return curr;
			}
		}
		return null;
	}

	/**
	 * Returns the {@link PartitionOccupyingTower} objects with areas that intersect the given area and aren't of the same player with their distance
	 * to given position.<br>
	 * 
	 * @param position
	 *            Position of the tower defining the center of it's area.
	 * @param radius
	 *            Radius of it's area.
	 * @param playerId
	 *            The id of the towers player. Only towers with another playerId will be returned.
	 * @return
	 */
	public List<Tuple<Integer, PartitionOccupyingTower>> getTowersOfOthersInRange(ShortPoint2D position, int radius, byte playerId) {
		LinkedList<Tuple<Integer, PartitionOccupyingTower>> result = new LinkedList<Tuple<Integer, PartitionOccupyingTower>>();

		for (PartitionOccupyingTower curr : this) {
			if (curr.playerId == playerId) {// skip the towers of this player.
				continue;
			}

			int sqDist = (int) MapCircle.getDistanceSquared(position.x, position.y, curr.position.x, curr.position.y);
			int maxDist = radius + (int) (curr.area.getRadius());

			if (sqDist <= (maxDist * maxDist)) {
				result.add(new Tuple<Integer, PartitionOccupyingTower>(sqDist, curr));
			}
		}

		return result;
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.writeInt(super.size());
		for (PartitionOccupyingTower curr : this) {
			oos.writeObject(curr);
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		int size = ois.readInt();
		for (int i = 0; i < size; i++) {
			this.add((PartitionOccupyingTower) ois.readObject());
		}
	}
}
