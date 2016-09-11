package jsettlers.logic.map.grid.partition.manager.datastructures;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.datastructures.PositionableList.IMovedVisitor;

import java.io.Serializable;

/**
 * Created by Andreas Eberle on 23.08.2016.
 */
public class PrioritizedPositionableList<P extends Enum, T extends ILocatable> implements Serializable {
	private final PositionableList<T>[] lists;

	public PrioritizedPositionableList(int numberOfPriorities) {
		lists = new PositionableList[numberOfPriorities];
		for (int i = 0; i < numberOfPriorities; i++) {
			lists[i] = new PositionableList<>();
		}
	}

	public void insert(P priority, T object) {
		lists[priority.ordinal()].insert(object);
	}

	public T getObjectAt(ShortPoint2D position, P priority) {
		T object = lists[priority.ordinal()].getObjectAt(position);
		if (object != null) {
			return object;
		}
		return null;
	}

	public int size(P minimumIncludedPriority) {
		int size = 0;
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			size += lists[i].size();
		}
		return size;
	}

	public boolean isEmpty(P minimumIncludedPriority) {
		return size(minimumIncludedPriority) <= 0;
	}

	public T getObjectCloseTo(ShortPoint2D position, P minimumIncludedPriority) {
		for (int i = lists.length - 1; i >= minimumIncludedPriority.ordinal(); i--) {
			T object = lists[i].getObjectCloseTo(position);
			if (object != null) {
				return object;
			}
		}
		return null;
	}

	public void remove(T offer) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].remove(offer);
		}
	}

	public void moveObjectsAtPositionTo(ShortPoint2D position, PrioritizedPositionableList<P, T> otherList, IMovedVisitor<T> movedVisitor) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].moveObjectsAtPositionTo(position, otherList.lists[i], movedVisitor);
		}
	}

	public void addAll(PrioritizedPositionableList<P, T> otherList) {
		for (int i = lists.length - 1; i >= 0; i--) {
			lists[i].addAll(otherList.lists[i]);
		}
	}
}
